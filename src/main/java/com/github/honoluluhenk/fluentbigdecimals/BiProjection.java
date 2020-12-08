package com.github.honoluluhenk.fluentbigdecimals;

import lombok.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;

@FunctionalInterface
public interface BiProjection<Arg> {
    @NonNull BigDecimal project(@NonNull BigDecimal value, @Nullable Arg argument, @NonNull MathContext mathContext);
}
