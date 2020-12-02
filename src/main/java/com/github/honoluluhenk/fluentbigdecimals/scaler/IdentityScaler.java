package com.github.honoluluhenk.fluentbigdecimals.scaler;

import java.math.BigDecimal;
import java.math.MathContext;

public class IdentityScaler implements Scaler {
    private static final long serialVersionUID = 7043803811609303754L;

    @Override
    public BigDecimal scale(BigDecimal value, MathContext mathContext) {
        return value;
    }

    @Override
    public String toString() {
        return "IdentityScaler";
    }

}
