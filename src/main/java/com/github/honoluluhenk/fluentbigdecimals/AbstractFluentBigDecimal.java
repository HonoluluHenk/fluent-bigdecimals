package com.github.honoluluhenk.fluentbigdecimals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.function.Function;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.var;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Regarding equals/hashcode/compareTo: see {@link BigDecimal};
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public abstract
class AbstractFluentBigDecimal<T extends AbstractFluentBigDecimal<T>>
    extends Number
    implements Serializable, Comparable<AbstractFluentBigDecimal<?>> {
    private static final long serialVersionUID = 1646116594300550112L;

    public static final BigDecimal HUNDRED = new BigDecimal("100");

    @EqualsAndHashCode.Include
    private final @NonNull BigDecimal value;
    private final @NonNull Configuration<T> configuration;

    protected AbstractFluentBigDecimal(@NonNull BigDecimal value, @NonNull Configuration<T> configuration) {
        this.value = requireNonNull(value, "value required");
        this.configuration = requireNonNull(configuration, "configuration required");
    }

    private static @NonNull <T extends AbstractFluentBigDecimal<T>> T newRawInstance(
        @NonNull BigDecimal value,
        @NonNull Configuration<T> configuration) {
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
    public <R extends AbstractFluentBigDecimal<R>> @NonNull R roundInto(Configuration<R> configuration) {
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
    public int compareTo(@NonNull AbstractFluentBigDecimal<?> o) {
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
            @SuppressWarnings("unchecked")
            T t = (T) this;

            return t;
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
        String result = String.format(
            "%s[%s,%s]",
            getClass().getSimpleName(),
            value.toPlainString(),
            getConfiguration()
        );

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

    public <Other extends AbstractFluentBigDecimal<Other>> @NonNull T add(@Nullable Other addend) {
        T result = add(mapValue(addend));

        return result;
    }

    public @NonNull T add(@NonNull String addendBigDecimal) {
        var result = add(new BigDecimal(addendBigDecimal));

        return result;
    }

    public @NonNull T add(double addend) {
        var result = add(new BigDecimal(addend));

        return result;
    }

    public @NonNull T add(long addend) {
        var result = add(new BigDecimal(addend));

        return result;
    }

    public @NonNull T subtract(@Nullable BigDecimal subtrahend) {
        T result = apply(BigDecimal::subtract, subtrahend);

        return result;
    }

    public <Other extends AbstractFluentBigDecimal<Other>> @NonNull T subtract(@Nullable Other subtrahend) {
        T result = subtract(mapValue(subtrahend));

        return result;
    }

    public @NonNull T subtract(@NonNull String subtrahendBigDecimal) {
        var result = subtract(new BigDecimal(subtrahendBigDecimal));

        return result;
    }

    public @NonNull T subtract(double subtrahend) {
        var result = subtract(new BigDecimal(subtrahend));

        return result;
    }

    public @NonNull T subtract(long subtrahend) {
        var result = subtract(new BigDecimal(subtrahend));

        return result;
    }

    public @NonNull T multiply(@Nullable BigDecimal multiplicand) {
        T result = apply(BigDecimal::multiply, multiplicand);

        return result;
    }

    public <Other extends AbstractFluentBigDecimal<Other>> @NonNull T multiply(@Nullable Other multiplicand) {
        T result = multiply(mapValue(multiplicand));

        return result;
    }

    public @NonNull T multiply(@NonNull String multiplicandBigDecimal) {
        T result = multiply(new BigDecimal(multiplicandBigDecimal));

        return result;
    }

    public @NonNull T multiply(double multiplicand) {
        T result = multiply(new BigDecimal(multiplicand));

        return result;
    }

    public @NonNull T multiply(long multiplicand) {
        T result = multiply(new BigDecimal(multiplicand));

        return result;
    }

    public @NonNull T divide(@Nullable BigDecimal divisor) {
        T result = apply(BigDecimal::divide, divisor);

        return result;
    }

    public <Other extends AbstractFluentBigDecimal<Other>> @NonNull T divide(@Nullable Other divisor) {
        T result = divide(mapValue(divisor));

        return result;
    }

    public @NonNull T divide(@NonNull String divisorBigDecimal) {
        var result = divide(new BigDecimal(divisorBigDecimal));

        return result;
    }

    public @NonNull T divide(double divisor) {
        var result = divide(new BigDecimal(divisor));

        return result;
    }

    public @NonNull T divide(long divisor) {
        var result = divide(new BigDecimal(divisor));

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

    @Override
    public int intValue() {
        return getValue().intValue();
    }

    @Override
    public long longValue() {
        return getValue().longValue();
    }

    @Override
    public float floatValue() {
        return getValue().floatValue();
    }

    @Override
    public double doubleValue() {
        return getValue().doubleValue();
    }

    /**
     * Convenience: shortcut for: foo.compareTo(other) == 0.
     * <p>
     * See semantics of {@link BigDecimal#compareTo(BigDecimal)} for more details.
     */
    public <Other extends AbstractFluentBigDecimal<Other>> boolean comparesTo(Other other) {
        return compareTo(other) == 0;
    }

    /**
     * Convenience: shortcut for: foo.compareTo(other) == 0.
     * <p>
     * See semantics of {@link BigDecimal#compareTo(BigDecimal)} for more details.
     */
    public boolean comparesTo(BigDecimal other) {
        return compareTo(other) == 0;
    }

    /**
     * Convenience: shortcut for: foo.compareTo(BigDecimal.ZERO) == 0.
     */
    public boolean isZero() {
        return comparesTo(BigDecimal.ZERO);
    }

    /**
     * See {@link BigDecimal#longValueExact()}.
     */
    public long longValueExact() {
        return getValue().longValueExact();
    }

    /**
     * See {@link BigDecimal#intValueExact()}.
     */
    public int intValueExact() {
        return getValue().intValueExact();
    }

    /**
     * See {@link BigDecimal#shortValueExact()}.
     */
    public short shortValueExact() {
        return getValue().shortValueExact();
    }

    /**
     * See {@link BigDecimal#byteValueExact()}.
     */
    public byte byteValueExact() {
        return getValue().byteValueExact();
    }

    private static @Nullable BigDecimal mapValue(@Nullable AbstractFluentBigDecimal<?> input) {
        if (input == null) {
            return null;
        }
        BigDecimal result = input.getValue();

        return result;
    }

}
