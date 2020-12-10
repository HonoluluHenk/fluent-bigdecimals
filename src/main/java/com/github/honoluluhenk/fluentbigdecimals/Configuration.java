package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.math.BigDecimal;
import java.math.MathContext;

@Value
@NonFinal
public class Configuration<T extends AbstractFluentBigDecimal<T>> {
    private final @NonNull MathContext mathContext;
    private final @NonNull Scaler scaler;
    private final @NonNull Factory<T> factory;

    @Override
    public @NonNull String toString() {
        return String.format("[%s,%s,%s]",
            getMathContext().getPrecision(),
            getMathContext().getRoundingMode(),
            getScaler());
    }

    public @NonNull T ofRaw(@NonNull String bigDecimal) {
        return ofRaw(new BigDecimal(bigDecimal));
    }

    public @NonNull T ofRaw(@NonNull BigDecimal value) {
        return factory.create(value, this);
    }

    public @NonNull T of(@NonNull String bigDecimal) {
        return of(new BigDecimal(bigDecimal));
    }

    public @NonNull T of(BigDecimal value) {
        return factory.create(value, this)
            .round();
    }

    public Configuration<T> withMathContext(@NonNull MathContext mathContext) {
        return this.mathContext == mathContext
            ? this
            : new Configuration<T>(mathContext, scaler, factory);
    }

    public Configuration<T> withScaler(@NonNull Scaler scaler) {
        return this.scaler == scaler ? this : new Configuration<T>(mathContext, scaler, factory);
    }

    public <O extends AbstractFluentBigDecimal<O>> Configuration<O> withFactory(@NonNull Factory<O> factory) {
        return new Configuration<>(getMathContext(), getScaler(), factory);
    }

}
