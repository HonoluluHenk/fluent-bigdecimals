package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.WithScale;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

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

    private final MathContext mathContext;
    private final Scaler scaler;
    private final Factory<T> factory;

    @Getter(NONE)
    @Setter(NONE)
    private final ConcurrentHashMap<BigDecimal, T> constantsCache
        = new ConcurrentHashMap<>(CONSTANTS_IN_BIGDECIMAL);

    public static <T extends AbstractFluentBigDecimal<T>> Configuration<T> createConfiguration(
        MathContext mathContext,
        Scaler scaler,
        Factory<T> factory
    ) {
        return new Configuration<>(mathContext, scaler, factory);
    }

    @Override
    public String toString() {
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
    public T ofRaw(String bigDecimal) {
        return ofRaw(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, <strong>un</strong>rounded instance.
     */
    public T ofRaw(BigDecimal value) {
        return factory.create(value, this);
    }

    /**
     * Create a new, rounded instance.
     */
    public T of(BigDecimal value) {
        return factory.create(value, this)
            .round();
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(String)}.
     */
    public T of(String bigDecimal) {
        return of(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(char[])}.
     */
    public T of(char[] bigDecimal) {
        return of(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(char[], int, int)}.
     */
    public T of(char[] text, int offset, int len) {
        return of(new BigDecimal(text, offset, len));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(int)}.
     */
    public T of(int val) {
        return of(new BigDecimal(val));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(BigInteger)}.
     */
    public T of(BigInteger val) {
        return of(new BigDecimal(val));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(BigInteger, int)}.
     */
    public T of(BigInteger unscaledVal, int scale) {
        return of(new BigDecimal(unscaledVal, scale));
    }

    /**
     * Create a new, rounded instance.
     */
    public T of(long val) {
        return of(new BigDecimal(val));
    }

    /**
     * Create a new, rounded instance.
     */
    public T of(double val) {
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
    public T ofExact(BigDecimal value) {
        T parsed = of(value);
        if (!parsed.comparesTo(value)) {
            throwNotExactException(value);
        }

        return parsed;
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(String)}.
     */
    public T ofExact(String bigDecimal) {
        return ofExact(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(char[])}.
     */
    public T ofExact(char[] bigDecimal) {
        return ofExact(new BigDecimal(bigDecimal));
    }

    /**
     * Create a new, rounded instance using {@link BigDecimal#BigDecimal(char[], int, int)}.
     */
    public T ofExact(char[] text, int offset, int len) {
        return ofExact(new BigDecimal(text, offset, len));
    }

    private void throwNotExactException(Serializable value) {
        throw new NotExactException(value, this);
    }

    /**
     * Convenience: create a new, rounded instance using a wrapped BigDecimal, see: {@link BigDecimal#valueOf(long)}.
     */
    public T valueOf(long val) {
        return of(BigDecimal.valueOf(val));
    }

    /**
     * Convenience: create a new, rounded instance using a wrapped BigDecimal, see: {@link BigDecimal#valueOf(double)}.
     */
    public T valueOf(double val) {
        return of(BigDecimal.valueOf(val));
    }

    /**
     * Convenience: create a new, rounded instance using a wrapped BigDecimal,
     * see: {@link BigDecimal#valueOf(long, int)}.
     */
    public T valueOf(long unscaledVal, int scale) {
        return of(BigDecimal.valueOf(unscaledVal, scale));
    }

    public Configuration<T> withMathContext(MathContext mathContext) {
        return new Configuration<>(mathContext, scaler, factory);
    }

    public Configuration<T> withScaler(Scaler scaler) {
        return new Configuration<>(mathContext, scaler, factory);
    }

    public <S extends Scaler & WithScale<S>> ScalingConfiguration<T> withScalingScaler(S scaler) {
        return new ScalingConfiguration<>(getMathContext(), scaler, getFactory());
    }

    public <O extends AbstractFluentBigDecimal<O>> Configuration<O> withFactory(Factory<O> factory) {
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
