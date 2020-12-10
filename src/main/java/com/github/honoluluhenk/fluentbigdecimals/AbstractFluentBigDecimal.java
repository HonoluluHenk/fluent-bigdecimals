package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Regarding equals/hashcode/compareTo: see {@link BigDecimal};
 */
@Getter
@EqualsAndHashCode
public abstract class AbstractFluentBigDecimal<T extends AbstractFluentBigDecimal<T>> implements Serializable, Comparable<T> {
    private static final long serialVersionUID = 1646116594300550112L;

    public static final BigDecimal HUNDRED = new BigDecimal("100");

    private final @NonNull BigDecimal value;
    @EqualsAndHashCode.Exclude
    private final @NonNull Configuration<T> configuration;

    protected AbstractFluentBigDecimal(@NonNull BigDecimal value, @NonNull Configuration<T> configuration) {
        this.value = requireNonNull(value, "value required");
        this.configuration = requireNonNull(configuration, "configuration required");
    }

    private static @NonNull <T extends AbstractFluentBigDecimal<T>> T newRawInstance(@NonNull BigDecimal value, @NonNull Configuration<T> configuration) {
        return configuration
            .getFactory()
            .create(value, configuration);
    }

    /**
     * Actually round <strong>and</strong> scale.
     * <p>
     * Mostly needed when you started with a raw value and need to round it.
     */
    public @NonNull T round() {
        @NonNull T result = apply(BigDecimal::round);
        return result;
    }

    /**
     * Switch to new MathContext/Scaler and round/scale value accordingly.
     * <p>
     * If you need to switch to a new Configuration <i>without</i> scaling/rounding,
     * use {@link #withConfiguration(Configuration)}.
     */
    public @NonNull T roundInto(Configuration<T> configuration) {
        return newRawInstance(getValue(), configuration)
            .round();
    }

    public T withValue(@NonNull BigDecimal value) {
        return newRawInstance(value, getConfiguration());
    }

    public T withConfiguration(@NonNull Configuration<T> configuration) {
        return newRawInstance(getValue(), configuration);
    }

    public @NonNull T withMathContext(MathContext mathContext) {
        return newRawInstance(getValue(), getConfiguration().withMathContext(mathContext));
    }

    public @NonNull T withScaler(Scaler scaler) {
        return newRawInstance(getValue(), getConfiguration().withScaler(scaler));
    }

    /**
     * Compares current value and delegates to {@link BigDecimal#compareTo(BigDecimal)}.
     */
    @Override
    public int compareTo(@NonNull T o) {
        return getValue().compareTo(o.getValue());
    }

    /**
     * Convenience: compare current value to other using {@link BigDecimal#compareTo(BigDecimal)}.
     */
    public int compareTo(@NonNull BigDecimal other) {
        return getValue().compareTo(other);
    }

    public <Arg> @NonNull T apply(@NonNull BiProjection<Arg> projection, @Nullable Arg argument) {
        if (argument == null) {
            //noinspection unchecked
            return (T) this;
        }

        var result = apply((value, mathContext) -> projection.project(value, argument, mathContext));

        return result;
    }

    public @NonNull T apply(@NonNull Projection projection) {
        Configuration<T> configuration = getConfiguration();

        var outcome = projection.project(value, configuration.getMathContext());
        requireNonNull(outcome, "Result of projection must not be null");

        var scaler = configuration.getScaler();
        var scaled = scaler.scale(outcome, configuration.getMathContext());
        requireNonNull(scaled, "Scaler must not return null");

        var result = newRawInstance(scaled, configuration);

        return result;
    }

    public <R> R map(@NonNull Function<BigDecimal, R> projection) {
        var result = projection.apply(getValue());

        return result;
    }

    @Override
    public @NonNull String toString() {
        String result = String.format("%s[%s,%s]",
            getClass().getSimpleName(),
            value.toPlainString(),
            getConfiguration());

        return result;
    }

    /**
     * See {@link BigDecimal#toPlainString()}.
     */
    public @NonNull String toPlainString() {
        return getValue().toPlainString();
    }

    /**
     * See {@link BigDecimal#toEngineeringString()}.
     */
    public @NonNull String toEngineeringString() {
        return getValue().toEngineeringString();
    }

    /**
     * See {@link BigDecimal#toBigInteger()}.
     */
    public BigInteger toBigInteger() {
        return getValue().toBigInteger();
    }

    /**
     * See {@link BigDecimal#toBigIntegerExact()}.
     */
    public BigInteger toBigIntegerExact() {
        return getValue().toBigIntegerExact();
    }

    public @NonNull T add(@Nullable BigDecimal addend) {
        var result = apply(BigDecimal::add, addend);

        return result;
    }

    public @NonNull T add(@Nullable T addend) {
        T result = add(mapValue(addend));

        return result;
    }

    public @NonNull T subtract(@Nullable BigDecimal subtrahend) {
        T result = apply(BigDecimal::subtract, subtrahend);

        return result;
    }

    public @NonNull T subtract(@Nullable T subtrahend) {
        T result = subtract(mapValue(subtrahend));

        return result;
    }

    public @NonNull T multiply(@Nullable BigDecimal multiplicand) {
        T result = apply(BigDecimal::multiply, multiplicand);

        return result;
    }

    public @NonNull T multiply(@Nullable T multiplicand) {
        T result = multiply(mapValue(multiplicand));

        return result;
    }

    public @NonNull T divide(@Nullable BigDecimal divisor) {
        T result = apply(BigDecimal::divide, divisor);

        return result;
    }

    public @NonNull T divide(@Nullable T divisor) {
        T result = divide(mapValue(divisor));

        return result;
    }

    public @NonNull T pctToFraction() {
        T result = divide(HUNDRED);

        return result;
    }

    public @NonNull T fractionToPct() {
        T result = multiply(HUNDRED);

        return result;
    }

    private static @Nullable BigDecimal mapValue(@Nullable AbstractFluentBigDecimal<?> input) {
        if (input == null) {
            return null;
        }
        BigDecimal result = input.getValue();

        return result;
    }

}
