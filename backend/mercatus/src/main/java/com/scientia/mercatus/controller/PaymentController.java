package com.scientia.mercatus.controller;

import com.scientia.mercatus.dto.Payment.CreatePaymentRequestDto;
import com.scientia.mercatus.dto.Payment.CreatePaymentResponseDto;
import com.scientia.mercatus.dto.Payment.PaymentIntentResultDto;
import com.scientia.mercatus.entity.Payment;
import com.scientia.mercatus.service.IPaymentService;
import com.scientia.mercatus.service.impl.PaymentApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    @PostMapping
    public ResponseEntity<CreatePaymentResponseDto> createPayment(
            @RequestBody @Valid CreatePaymentRequestDto createPaymentRequestDto) {

        PaymentIntentResultDto result = paymentApplicationService.createPayment(
                createPaymentRequestDto.getOrderReference(),
                createPaymentRequestDto.getAmountMinor(),
                createPaymentRequestDto.getCurrency()
        );

        return ResponseEntity.ok(new CreatePaymentResponseDto(result.paymentId(),  result.clientSecret()));

    }
}
