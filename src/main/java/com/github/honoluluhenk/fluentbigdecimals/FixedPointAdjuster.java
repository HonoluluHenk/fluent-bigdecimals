package com.github.honoluluhenk.fluentbigdecimals;

import java.math.BigDecimal;

public class FixedPointAdjuster implements Adjuster {

    private static final long serialVersionUID = -9021712353948342589L;

    @Override
    public BigDecimal adjust(BigDecimal value) {
        return value;
    }
}
