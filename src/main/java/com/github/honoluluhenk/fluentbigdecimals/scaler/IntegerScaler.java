package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Scale to scale 0 (i.e.: convert to integer).
 */
@EqualsAndHashCode(callSuper = false)
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
