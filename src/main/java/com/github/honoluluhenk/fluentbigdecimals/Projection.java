package com.github.honoluluhenk.fluentbigdecimals;

import java.math.BigDecimal;
import java.math.MathContext;

@FunctionalInterface
public interface Projection {
    static Projection identity() {
        return (bigDecimal, mathContext) -> bigDecimal;
    }

    BigDecimal project(BigDecimal value, MathContext mathContext);

}
