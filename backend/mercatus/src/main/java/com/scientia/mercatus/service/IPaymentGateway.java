package com.scientia.mercatus.service;

import com.razorpay.RazorpayException;
import com.scientia.mercatus.dto.Payment.PaymentInitiationResultDto;

public interface IPaymentGateway {

    PaymentInitiationResultDto initiatePayment(String orderReference, long amountMinor, String currency) ;
}
