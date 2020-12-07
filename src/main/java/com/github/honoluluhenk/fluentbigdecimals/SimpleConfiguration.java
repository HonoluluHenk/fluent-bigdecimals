package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.NonNull;
import lombok.Value;

import java.math.MathContext;

@Value
public class SimpleConfiguration implements Configuration {
    private final MathContext mathContext;
    private final Scaler scaler;

    @Override
    public @NonNull String toString() {
        return String.format("%s[%s,%s,%s]",
            SimpleConfiguration.class.getSimpleName(),
            getMathContext().getPrecision(),
            getMathContext().getRoundingMode(),
            getScaler());
    }
}
