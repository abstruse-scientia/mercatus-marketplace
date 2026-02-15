package com.scientia.mercatus.dto.Payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreatePaymentRequestDto {

    @NotNull
    private String orderReference;


    @NotNull
    private Long amountMinor;

    @NotNull
    @Size(min = 3, max = 3)
    private String currency;

}
