package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.WithScale;
import lombok.*;
import lombok.experimental.NonFinal;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.concurrent.ConcurrentHashMap;

import static lombok.AccessLevel.NONE;

@Value
@NonFinal
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("RedundantModifiersValueLombok")
public class Configuration<T extends AbstractFluentBigDecimal<T>> implements Serializable {
    private static final long serialVersionUID = -8556901571320467482L;

    // ZERO, ONE, TEN (at the time of this writing)
    private static final int CONSTANTS_IN_BIGDECIMAL = 3;

    private final @NonNull MathContext mathContext;
    private final @NonNull Scaler scaler;
    private final @NonNull Factory<T> factory;

    @Getter(NONE)
    @Setter(NONE)
    private final @NonNull ConcurrentHashMap<BigDecimal, T> constantsCache
        = new ConcurrentHashMap<>(CONSTANTS_IN_BIGDECIMAL);

    public static <T extends AbstractFluentBigDecimal<T>> Configuration<T> createConfiguration(
        @NonNull MathContext mathContext,
        @NonNull Scaler scaler,
        @NonNull Factory<T> factory
    ) {
        return new Configuration<>(mathContext, scaler, factory);
    }

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
    public @NonNull T of(@NonNull BigDecimal value) {
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
    public @NonNull T of(char @NonNull [] bigDecimal) {
        return of(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(char[], int, int)}.
     */
    public @NonNull T of(char @NonNull [] text, int offset, int len) {
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
     * Create a new instance but throws if value does not already match the {@link Configuration}.
     *
     * <p>Useful for e.g. parsing that absolutely must not do rounding.</p>
     * <p>Example: if data from the database gets rounded this might be an error in the persistence layer.</p>
     *
     * @throws NotExactException if value does not already match the {@link Configuration}.
     */
    public @NonNull T ofExact(@NonNull BigDecimal value) {
        T parsed = of(value);
        if (!parsed.comparesTo(value)) {
            throwNotExactException(value);
        }

        return parsed;
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(String)}.
     */
    public @NonNull T ofExact(@NonNull String bigDecimal) {
        return ofExact(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(char[])}.
     */
    public @NonNull T ofExact(char[] bigDecimal) {
        return ofExact(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(char[], int, int)}.
     */
    public @NonNull T ofExact(char[] text, int offset, int len) {
        return ofExact(new BigDecimal(text, offset, len));
    }

    private void throwNotExactException(Serializable value) {
        throw new NotExactException(value, this);
    }

    /**
     * Convenience: create a new, rounded instance using a wrapped BigDecimal, see: {@link BigDecimal#valueOf(long)}.
     */
    public @NonNull T valueOf(long val) {
        return of(BigDecimal.valueOf(val));
    }

    /**
     * Convenience: create a new, rounded instance using a wrapped BigDecimal, see: {@link BigDecimal#valueOf(double)}.
     */
    public @NonNull T valueOf(double val) {
        return of(BigDecimal.valueOf(val));
    }

    /**
     * Convenience: create a new, rounded instance using a wrapped BigDecimal,
     * see: {@link BigDecimal#valueOf(long, int)}.
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

    /**
     * For subclasses: define you own constants, see e.g. {@link #ZERO()}.
     */
    protected T memoizedConstant(BigDecimal constant) {
        return constantsCache.computeIfAbsent(constant, this::of);
    }

    /**
     * Returns a {@link BigDecimal#ZERO} in this configuration.
     */
    public T ZERO() {
        return memoizedConstant(BigDecimal.ZERO);
    }

    /**
     * Returns a {@link BigDecimal#ONE} in this configuration.
     */
    public T ONE() {
        return memoizedConstant(BigDecimal.ONE);
    }

    /**
     * Returns a {@link BigDecimal#TEN} in this configuration.
     */
    public T TEN() {
        return memoizedConstant(BigDecimal.TEN);
    }

}


