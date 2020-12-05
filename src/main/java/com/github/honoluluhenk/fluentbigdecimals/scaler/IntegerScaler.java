package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.NonNull;

import java.math.BigDecimal;
import java.math.MathContext;

public class IntegerScaler implements Scaler {
    private static final long serialVersionUID = 1951794142376474049L;

    @Override
    public @NonNull BigDecimal scale(@NonNull BigDecimal value, @NonNull MathContext mathContext) {
        var result = value.setScale(0, mathContext.getRoundingMode());

        return result;
    }

    @Override
    public String toString() {
        return IntegerScaler.class.getSimpleName();
    }
}
