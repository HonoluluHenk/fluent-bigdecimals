package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Scale to scale 0 (i.e.: convert to integer).
 */
@EqualsAndHashCode(callSuper = false)
public class IntegerScaler implements Scaler {
    private static final long serialVersionUID = 1951794142376474049L;

    @Override
    public BigDecimal scale(BigDecimal value, MathContext mathContext) {
        return value.setScale(0, mathContext.getRoundingMode());
    }

    @Override
    public String toString() {
        return IntegerScaler.class.getSimpleName();
    }
}
