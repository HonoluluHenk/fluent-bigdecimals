package com.github.honoluluhenk.fluentbigdecimals;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.math.BigDecimal;
import java.math.MathContext;

@FunctionalInterface
public interface Projection {
    static @NonNull Projection identity() {
        return (bigDecimal, mathContext) -> bigDecimal;
    }

    @NonNull
    BigDecimal project(@NonNull BigDecimal value, @NonNull MathContext mathContext);

}
