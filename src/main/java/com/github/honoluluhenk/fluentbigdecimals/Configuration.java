package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.WithScale;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

@Value
@NonFinal
@SuppressWarnings("RedundantModifiersValueLombok")
public class Configuration<T extends AbstractFluentBigDecimal<T>> implements Serializable {
    private static final long serialVersionUID = -8556901571320467482L;

    private final @NonNull MathContext mathContext;
    private final @NonNull Scaler scaler;
    private final @NonNull Factory<T> factory;

    @Override
    public @NonNull String toString() {
        return String.format(
            "[%s,%s,%s]",
            getMathContext().getPrecision(),
            getMathContext().getRoundingMode(),
            getScaler()
        );
    }

    /**
     * Create a new, <strong>un</strong>rounded instance using {@link BigDecimal#BigDecimal(String)}.
     */
    public @NonNull T ofRaw(@NonNull String bigDecimal) {
        return ofRaw(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, <strong>un</strong>rounded instance.
     */
    public @NonNull T ofRaw(@NonNull BigDecimal value) {
        return factory.create(value, this);
    }

    /**
     * Create a new, rounded instance.
     */
    public @NonNull T of(BigDecimal value) {
        return factory.create(value, this)
            .round();
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(String)}.
     */
    public @NonNull T of(@NonNull String bigDecimal) {
        return of(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(char[])}.
     */
    public @NonNull T of(@NonNull char[] bigDecimal) {
        return of(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(char[], int, int)}.
     */
    public @NonNull T of(@NonNull char[] text, int offset, int len) {
        return of(new BigDecimal(text, offset, len));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(int)}.
     */
    public @NonNull T of(int val) {
        return of(new BigDecimal(val));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(BigInteger)}.
     */
    public @NonNull T of(@NonNull BigInteger val) {
        return of(new BigDecimal(val));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(BigInteger, int)}.
     */
    public @NonNull T of(@NonNull BigInteger unscaledVal, int scale) {
        return of(new BigDecimal(unscaledVal, scale));
    }

    /**
     * Create a new, rounded instance.
     */
    public @NonNull T of(long val) {
        return of(new BigDecimal(val));
    }

    /**
     * Create a new, rounded instance.
     */
    public @NonNull T of(double val) {
        return of(new BigDecimal(val));
    }

    /**
     * Convenience: create a new, rounded instance using a wrapped BigDecimal, see: {@link BigDecimal#valueOf(long)}.
     */
    public @NonNull T valueOf(@NonNull long val) {
        return of(BigDecimal.valueOf(val));
    }

    /**
     * Convenience: create a new, rounded instance using a wrapped BigDecimal, see: {@link BigDecimal#valueOf(double)}.
     */
    public @NonNull T valueOf(@NonNull double val) {
        return of(BigDecimal.valueOf(val));
    }

    /**
     * Convenience: create a new, rounded instance using a wrapped BigDecimal, see: {@link BigDecimal#valueOf(long, int)}.
     */
    public @NonNull T valueOf(long unscaledVal, int scale) {
        return of(BigDecimal.valueOf(unscaledVal, scale));
    }

    public Configuration<T> withMathContext(@NonNull MathContext mathContext) {
        return new Configuration<>(mathContext, scaler, factory);
    }

    public Configuration<T> withScaler(@NonNull Scaler scaler) {
        return new Configuration<>(mathContext, scaler, factory);
    }

    public <S extends Scaler & WithScale<S>> ScalingConfiguration<T> withScalingScaler(@NonNull S scaler) {
        return new ScalingConfiguration<>(getMathContext(), scaler, getFactory());
    }

    public <O extends AbstractFluentBigDecimal<O>> Configuration<O> withFactory(@NonNull Factory<O> factory) {
        return new Configuration<>(getMathContext(), getScaler(), factory);
    }

}


