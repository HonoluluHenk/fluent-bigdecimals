package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.math.MathContext;

@Value
public class Configuration {
    private final @NonNull MathContext mathContext;
    private final @NonNull Scaler scaler;

    @Override
    public @NonNull String toString() {
        return String.format("[%s,%s,%s]",
            getMathContext().getPrecision(),
            getMathContext().getRoundingMode(),
            getScaler());
    }

    public @NonNull FluentBigDecimal of(@NonNull BigDecimal value) {
        return FluentBigDecimal.of(value, mathContext, scaler);
    }

    public @NonNull FluentBigDecimal of(@NonNull String bigDecimal) {
        return of(new BigDecimal(bigDecimal));
    }

    public @NonNull FluentBigDecimal ofRaw(@NonNull BigDecimal value) {
        return FluentBigDecimal.ofRaw(value, mathContext, scaler);
    }

    public @NonNull FluentBigDecimal ofRaw(@NonNull String bigDecimal) {
        return ofRaw(new BigDecimal(bigDecimal));
    }

}
