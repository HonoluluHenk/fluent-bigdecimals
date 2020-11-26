package com.github.honoluluhenk.fluentbigdecimals;

import java.io.Serializable;
import java.math.BigDecimal;

@FunctionalInterface
public interface Adjuster extends Serializable {
    BigDecimal adjust(BigDecimal value);

    default boolean needsAdjusting(BigDecimal value) {
        return true;
    }
}
