package com.scientia.mercatus.messaging;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailEvent {

    @NotNull(message = "Customer name can not be null")
    private String customerName;


    @NotBlank(message = "Order reference can not be blank")
    private String orderReference;

    @NotNull(message = "Email address can not be null")
    private String emailAddress;


    private String message;
}
