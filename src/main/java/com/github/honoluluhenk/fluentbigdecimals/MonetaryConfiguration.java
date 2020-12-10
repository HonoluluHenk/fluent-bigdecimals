package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxScaleScaler;
import lombok.NonNull;

import java.math.MathContext;

public class MonetaryConfiguration<T extends AbstractFluentBigDecimal<T>> extends Configuration<T> {
    public MonetaryConfiguration(@NonNull MathContext mathContext, @NonNull MaxScaleScaler scaler, @NonNull Factory<T> factory) {
        super(mathContext, scaler, factory);
    }

    public @NonNull MonetaryConfiguration<T> withScale(int scale) {
        return new MonetaryConfiguration<>(getMathContext(), new MaxScaleScaler(scale), getFactory());
    }
}
