package com.github.honoluluhenk.fluentbigdecimals.scaler;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;

@FunctionalInterface
public interface Scaler extends Serializable {

    @NonNull
    BigDecimal scale(@NonNull BigDecimal value, @NonNull MathContext mathContext);

}
