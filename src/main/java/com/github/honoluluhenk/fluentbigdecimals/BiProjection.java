package com.github.honoluluhenk.fluentbigdecimals;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.math.BigDecimal;
import java.math.MathContext;

@FunctionalInterface
public interface BiProjection<Arg> {
    @NonNull
    BigDecimal project(@NonNull BigDecimal value, @NonNull Arg argument, @NonNull MathContext mathContext);
}
