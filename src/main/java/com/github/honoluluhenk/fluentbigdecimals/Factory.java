package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.math.MathContext;

@AllArgsConstructor
public class Factory {

    private final @NonNull MathContext mathContext;
    private final @NonNull Scaler scaler;

    public static Factory factory(@NonNull MathContext mathContext, @NonNull Scaler scaler) {
        return new Factory(mathContext, scaler);
    }

    public FluentBigDecimal fromValue(@NonNull BigDecimal value) {
        return new FluentBigDecimal(value, mathContext, scaler);
    }
}
