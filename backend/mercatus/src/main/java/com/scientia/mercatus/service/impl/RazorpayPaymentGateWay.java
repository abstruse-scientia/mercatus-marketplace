package com.scientia.mercatus.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;
import com.scientia.mercatus.entity.PaymentProvider;
import com.scientia.mercatus.exception.BusinessException;
import com.scientia.mercatus.exception.ErrorEnum;


import com.scientia.mercatus.service.IPaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RazorpayPaymentGateWay implements IPaymentGateway {



    private final RazorpayClient razorpayClient;

    @Transactional
    @Override
    public PaymentInitiationResultDto initiatePayment(String orderReference, long amountExpected, String currency)  {


        try {
            Order razorpayOrder = createOrder(orderReference, amountExpected, currency);



            return new PaymentInitiationResultDto(
                    PaymentProvider.RAZORPAY,
                    razorpayOrder.get("id"),
                    razorpayOrder.get("amount"),
                    razorpayOrder.get("currency")
            );

        }catch (RazorpayException e) {
            log.info("Razor Exception for order reference{}",  orderReference);
            throw new BusinessException(ErrorEnum.PAYMENT_GATEWAY_ERROR);
        }

    }

    /* Note: This Order is Razorpay's return-type not Order Entity class */
    private Order createOrder(String orderReference, long amountExpected, String currency) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountExpected);
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", orderReference);

        return razorpayClient.orders.create(orderRequest);

    }
}
