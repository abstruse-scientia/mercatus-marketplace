package com.scientia.mercatus.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CurrencyConversionUtil {

    public long toINRMinor(BigDecimal totalAmount) {
        return  totalAmount.movePointRight(2).longValueExact();
    }

}
