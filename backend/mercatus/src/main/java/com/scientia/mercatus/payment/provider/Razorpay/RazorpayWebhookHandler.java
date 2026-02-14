package com.scientia.mercatus.payment.provider.Razorpay;


import com.scientia.mercatus.entity.PaymentProvider;
import com.scientia.mercatus.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RazorpayWebhookHandler {

    private final RazorpaySignatureVerifier signatureVerifier;
    private final IPaymentService paymentService;

    public void handleWebhook(String payload, String signature) {
        signatureVerifier.verify(payload, signature);

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        JSONObject paymentEntity = json.getJSONObject("payload")
                .getJSONObject("payment").getJSONObject("entity");


        String providerPaymentId = paymentEntity.getString("id");
        String providerOrderId = paymentEntity.getString("order_id");
        long amount = paymentEntity.getLong("amount");

        switch (event) {

            case "payment.captured" -> paymentService.markPaymentSuccess(
                    PaymentProvider.RAZORPAY,
                    providerOrderId,
                    providerPaymentId,
                    amount
            );

            case "payment.failed" -> paymentService.markPaymentFailed(
                    PaymentProvider.RAZORPAY,
                    providerOrderId,
                    providerPaymentId
            );

            default -> {
                // ignore other events
            }
        }
    }
}


