package com.github.honoluluhenk.fluentbigdecimals.adjuster;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Handles Precision/Scale like most databases: allow max (precision - scale) integers and max (scale) decimals.
 * <p>
 * This is done by first rounding to precision and then reducing scale by rounding.
 */
public class FixedPointAdjuster implements Adjuster {

    private static final long serialVersionUID = -9021712353948342589L;

    @Override
    public BigDecimal adjust(BigDecimal value) {
        return value;
    }

    @Override
    public MathContext getMathContext() {
        throw new IllegalStateException("not implemented yet");
    }
}
