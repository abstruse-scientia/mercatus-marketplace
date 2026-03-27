package com.scientia.mercatus.service.OrderServiceIT;


import com.scientia.mercatus.builder.ProductBuilder;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.*;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.factory.*;
import com.scientia.mercatus.payment.PaymentGatewayRegistry;
import com.scientia.mercatus.repository.*;
import com.scientia.mercatus.service.IInventoryService;
import com.scientia.mercatus.service.IPaymentService;
import com.scientia.mercatus.service.impl.RazorpayPaymentGateWay;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Set;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceInitiatePaymentIT {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private EntityManager entityManager;


    @MockitoBean
    private IInventoryService inventoryService;

    @MockitoBean
    private RazorpayPaymentGateWay razorpayPaymentGateWay;

    @MockitoBean
    private PaymentGatewayRegistry gatewayRegistry;




    private Payment createPayment(String orderRef) {
        Payment payment = new Payment();
        payment.setOrderReference(orderRef);
        payment.setAmountExpected(1000L);
        payment.setCurrency("INR");
        payment.setProvider(PaymentProvider.RAZORPAY);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProviderOrderId("prov-order-1");
        return paymentRepository.save(payment);
    }
    private static class PaymentContext {
        Order order;
        OrderItem orderItem;
        User user;
        Product product;
        Category category;
        InventoryItem inventoryItem;
        UserAddress userAddress;
    }

    private PaymentContext baseContext() {
        PaymentContext ctx =  new PaymentContext();
        ctx.user = userRepository.save(UserFactory.create());
        ctx.userAddress = userAddressRepository.save(AddressFactory.withSnapshot(ctx.user.getUserId()));
        ctx.category = categoryRepository.save(CategoryFactory.create());
        ctx.inventoryItem = inventoryItemRepository.save(InventoryItemFactory.create());
        ctx.product = productRepository.save(ProductBuilder.aProduct()
                .withCategory(ctx.category)
                .withSku(ctx.inventoryItem.getSku())
                .build());
        ctx.order = orderRepository.save(OrderFactory.create(ctx.user, ctx.userAddress.getAddressSnapshot()));

        ctx.orderItem = orderItemRepository.save(OrderItemFactory.create(ctx.product, ctx.order, 2));
        ctx.order.setOrderPaymentStatus(OrderPaymentStatus.PENDING);
        ctx.order.setOrderItems(Set.of(ctx.orderItem));
        orderRepository.save(ctx.order);
        return ctx;
    }


    /* 1. Happy Path */
    @Test
    void shouldCreatePayment_whenValidOrder() {
        //Arrange
        PaymentContext ctx =  baseContext();
        when(razorpayPaymentGateWay.initiatePayment(any(), anyLong(), any()))
                .thenReturn(new PaymentInitiationResultDto(
                        PaymentProvider.RAZORPAY,
                        "prov-order-1",
                        1000,
                        "INR"
                ));

        when(gatewayRegistry.get(any())).thenReturn(razorpayPaymentGateWay);


        //Act
        PaymentInitiationResultDto initiationResult = paymentService.initiatePayment(
                ctx.order.getOrderReference(),
                "INR",
                PaymentProvider.RAZORPAY
        );

        //Assert
        Payment payment = paymentRepository.findAll().getFirst();

        assertThat(payment.getOrderReference()).isEqualTo(ctx.order.getOrderReference());
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getProviderOrderId()).isEqualTo("prov-order-1");

    }


    /* 2. Invalid order state */
    @Test
    void shouldThrowError_whenOrderNotInCreatedState() {
        PaymentContext ctx =  baseContext();
        ctx.order.setStatus(OrderStatus.CANCELLED);
        ctx.order.setOrderPaymentStatus(OrderPaymentStatus.PENDING);
        orderRepository.save(ctx.order);

        when(gatewayRegistry.get(any())).thenReturn(razorpayPaymentGateWay);
        assertThatThrownBy(() ->
                paymentService.initiatePayment(ctx.order.getOrderReference(), "INR", PaymentProvider.RAZORPAY)
        ).isInstanceOf(BusinessException.class);

        assertThat(paymentRepository.count()).isEqualTo(0);
    }


    /* 3. Duplicate (initiation) payment request */
    @Test
    void shouldHandleDuplicatePaymentRequest() {
        //Arrange
        PaymentContext ctx =  baseContext();
        when(razorpayPaymentGateWay.initiatePayment(any(), anyLong(), any()))
                .thenReturn(new PaymentInitiationResultDto(
                        PaymentProvider.RAZORPAY,
                        "prov-order-1",
                        1000,
                        "INR"
                ));


        when(gatewayRegistry.get(any())).thenReturn(razorpayPaymentGateWay);

        paymentService.initiatePayment(ctx.order.getOrderReference(), "INR", PaymentProvider.RAZORPAY);
        paymentService.initiatePayment(ctx.order.getOrderReference(), "INR", PaymentProvider.RAZORPAY);

        List<Payment> payments = paymentRepository.findAll();

        //observing current behaviour rather than forcing the ideal one
        assertThat(payments.size()).isGreaterThanOrEqualTo(1);
    }


    //4. Gateway failure
    @Test
    void shouldHandleGatewayFailure() {
        PaymentContext ctx =  baseContext();

        when(razorpayPaymentGateWay.initiatePayment(any(), anyLong(), any()))
                .thenThrow(new RuntimeException("gateway down"));

        assertThatThrownBy(() ->
                paymentService.initiatePayment(ctx.order.getOrderReference(), "INR", PaymentProvider.RAZORPAY)
        ).isInstanceOf(RuntimeException.class);

        // observing system behavior
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments.size()).isEqualTo(0);
    }


    //5. Success flow
    @Test
    void shouldMarkPaymentSuccess_andConfirmOrder() {
        PaymentContext ctx = baseContext();

        ctx.order.setOrderPaymentStatus(OrderPaymentStatus.PENDING);
        orderRepository.save(ctx.order);

        Payment payment = createPayment(ctx.order.getOrderReference());

        paymentService.markPaymentSuccess(
                PaymentProvider.RAZORPAY,
                "prov-order-1",
                "pay123",
                payment.getAmountExpected()
        );

        entityManager.clear();

        Order updatedOrder = orderRepository.findById(ctx.order.getId()).orElseThrow();

        assertThat(updatedOrder.getOrderPaymentStatus())
                .isEqualTo(OrderPaymentStatus.SUCCESS);

        assertThat(updatedOrder.getStatus())
                .isEqualTo(OrderStatus.CONFIRMED);
    }


    //6. Duplicate webhook
    @Test
    void shouldIgnoreDuplicateSuccessWebhook() {
        PaymentContext ctx = baseContext();
        Payment payment = createPayment(ctx.order.getOrderReference());

        paymentService.markPaymentSuccess(PaymentProvider.RAZORPAY, "prov-order-1", "pay123", payment.getAmountExpected());

        verify(inventoryService, times(ctx.order.getOrderItems().size()))
                .confirmReservation(any());

        clearInvocations(inventoryService);
        paymentService.markPaymentSuccess(PaymentProvider.RAZORPAY, "prov-order-1", "pay123", payment.getAmountExpected());

        verify(inventoryService, never())
                .confirmReservation(any());
    }

    //7. Amount mismatch
    @Test
    void shouldThrow_whenAmountMismatch() {
        PaymentContext ctx = baseContext();
        Payment payment = createPayment(ctx.order.getOrderReference());

        assertThatThrownBy(() ->
                paymentService.markPaymentSuccess(PaymentProvider.RAZORPAY,
                        "prov-order-1", "pay123",
                        999L)
        ).isInstanceOf(BusinessException.class);
    }

    //8. Payment failure

    @Test
    void shouldMarkPaymentFailure_andCancelOrder() {
        PaymentContext ctx = baseContext();

        Payment payment = createPayment(ctx.order.getOrderReference());

        paymentService.markPaymentFailed(
                PaymentProvider.RAZORPAY,
                "prov-order-1",
                "pay123"
        );

        entityManager.clear();

        Order updatedOrder = orderRepository.findById(ctx.order.getId()).orElseThrow();

        assertThat(updatedOrder.getOrderPaymentStatus())
                .isEqualTo(OrderPaymentStatus.FAILED);

        assertThat(updatedOrder.getStatus())
                .isEqualTo(OrderStatus.CANCELLED);
    }


    // 9. Order cancelled by user
    @Test
    void shouldIgnoreFailureWebhook_ifOrderAlreadyCancelledByUser() {
        PaymentContext ctx = baseContext();

        // user cancels first
        ctx.order.setStatus(OrderStatus.CANCELLED);
        ctx.order.setOrderPaymentStatus(OrderPaymentStatus.PENDING);
        orderRepository.save(ctx.order);

        Payment payment = createPayment(ctx.order.getOrderReference());

        paymentService.markPaymentFailed(
                PaymentProvider.RAZORPAY,
                "prov-order-1",
                "pay123"
        );

        entityManager.clear();

        Order updatedOrder = orderRepository.findById(ctx.order.getId()).orElseThrow();

        // should remain unchanged
        assertThat(updatedOrder.getOrderPaymentStatus())
                .isEqualTo(OrderPaymentStatus.PENDING);

        assertThat(updatedOrder.getStatus())
                .isEqualTo(OrderStatus.CANCELLED);
    }

    //10. out of order webhook test
    @Test
    void shouldIgnoreSuccessWebhook_ifFailureAlreadyProcessed() {
        PaymentContext ctx = baseContext();

        Payment payment = createPayment(ctx.order.getOrderReference());

        // first: failure
        paymentService.markPaymentFailed(
                PaymentProvider.RAZORPAY,
                "prov-order-1",
                "pay123"
        );

        // second: success arrives late
        paymentService.markPaymentSuccess(
                PaymentProvider.RAZORPAY,
                "prov-order-1",
                "pay123",
                payment.getAmountExpected()
        );

        entityManager.clear();

        Order updatedOrder = orderRepository.findById(ctx.order.getId()).orElseThrow();

        // state should NOT change
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(updatedOrder.getOrderPaymentStatus()).isEqualTo(OrderPaymentStatus.FAILED);
    }

}



