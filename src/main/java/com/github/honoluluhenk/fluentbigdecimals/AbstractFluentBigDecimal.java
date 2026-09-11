package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public abstract
class AbstractFluentBigDecimal<T extends AbstractFluentBigDecimal<T>>
    extends Number
    implements Serializable, Comparable<AbstractFluentBigDecimal<?>> {
    private static final long serialVersionUID = 1646116594300550112L;

    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @EqualsAndHashCode.Include
    private final BigDecimal value;
    private final Configuration<T> configuration;

    protected AbstractFluentBigDecimal(BigDecimal value, Configuration<T> configuration) {
        this.value = requireNonNull(value, "value required");
        this.configuration = requireNonNull(configuration, "configuration required");
    }

    private static <T extends AbstractFluentBigDecimal<T>> T newRawInstance(
        BigDecimal value,
        Configuration<T> configuration) {
        return configuration
            .getFactory()
            .create(value, configuration);
    }

    /**
     * Actually round <strong>and</strong> scale.
     * <p>
     * Mostly needed when you started with a raw value and need to round it.
     */
    public T round() {
        return apply(BigDecimal::round);
    }

    /**
     * Switch to new MathContext/Scaler and round/scale value accordingly.
     * <p>
     * If you need to switch to a new Configuration <i>without</i> scaling/rounding,
     * use {@link #withConfiguration(Configuration)}.
     */
    public <R extends AbstractFluentBigDecimal<R>> R roundInto(Configuration<R> configuration) {
        return newRawInstance(getValue(), configuration)
            .round();
    }

    public T withValue(BigDecimal value) {
        return newRawInstance(value, getConfiguration());
    }

    public T withConfiguration(Configuration<T> configuration) {
        return newRawInstance(getValue(), configuration);
    }

    public T withMathContext(MathContext mathContext) {
        return newRawInstance(getValue(), getConfiguration().withMathContext(mathContext));
    }

    public T withScaler(Scaler scaler) {
        return newRawInstance(getValue(), getConfiguration().withScaler(scaler));
    }

    /**
     * Compares current value and delegates to {@link BigDecimal#compareTo(BigDecimal)}.
     */
    @Override
    public int compareTo(AbstractFluentBigDecimal<?> o) {
        return getValue().compareTo(o.getValue());
    }

    /**
     * Convenience: compare current value to other using {@link BigDecimal#compareTo(BigDecimal)}.
     */
    public int compareTo(BigDecimal other) {
        return getValue().compareTo(other);
    }

    public <Arg> T apply(BiProjection<Arg> projection, @Nullable Arg argument) {
        if (argument == null) {
            @SuppressWarnings("unchecked")
            T t = (T) this;

            return t;
        }

        return apply((value, mathContext) -> projection.project(value, argument, mathContext));
    }

    public T apply(Projection projection) {
        Configuration<T> configuration = getConfiguration();

        var outcome = projection.project(value, configuration.getMathContext());
        requireNonNull(outcome, "Result of projection must not be null");

        var scaler = configuration.getScaler();
        var scaled = scaler.scale(outcome, configuration.getMathContext());
        requireNonNull(scaled, "Scaler must not return null");

        return newRawInstance(scaled, configuration);
    }

    public <R> R map(Function<BigDecimal, R> projection) {
        return projection.apply(getValue());
    }

    @Override
    public String toString() {
        return String.format(
            "%s[%s,%s]",
            getClass().getSimpleName(),
            value.toPlainString(),
            getConfiguration()
        );
    }

    /**
     * See {@link BigDecimal#toPlainString()}.
     */
    public String toPlainString() {
        return getValue().toPlainString();
    }

    /**
     * See {@link BigDecimal#toEngineeringString()}.
     */
    public String toEngineeringString() {
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

    public T add(@Nullable BigDecimal addend) {
        return apply(BigDecimal::add, addend);
    }

    public <Other extends AbstractFluentBigDecimal<Other>> T add(@Nullable Other addend) {
        return add(mapValue(addend));
    }

    public T add(String addendBigDecimal) {
        return add(new BigDecimal(addendBigDecimal));
    }

    public T add(double addend) {
        return add(new BigDecimal(addend));
    }

    public T add(long addend) {
        return add(new BigDecimal(addend));
    }

    public T subtract(@Nullable BigDecimal subtrahend) {
        return apply(BigDecimal::subtract, subtrahend);
    }

    public <Other extends AbstractFluentBigDecimal<Other>> T subtract(@Nullable Other subtrahend) {
        return subtract(mapValue(subtrahend));
    }

    public T subtract(String subtrahendBigDecimal) {
        return subtract(new BigDecimal(subtrahendBigDecimal));
    }

    public T subtract(double subtrahend) {
        return subtract(new BigDecimal(subtrahend));
    }

    public T subtract(long subtrahend) {
        return subtract(new BigDecimal(subtrahend));
    }

    public T multiply(@Nullable BigDecimal multiplicand) {
        return apply(BigDecimal::multiply, multiplicand);
    }

    public <Other extends AbstractFluentBigDecimal<Other>> T multiply(@Nullable Other multiplicand) {
        return multiply(mapValue(multiplicand));
    }

    public T multiply(String multiplicandBigDecimal) {
        return multiply(new BigDecimal(multiplicandBigDecimal));
    }

    public T multiply(double multiplicand) {
        return multiply(new BigDecimal(multiplicand));
    }

    public T multiply(long multiplicand) {
        return multiply(new BigDecimal(multiplicand));
    }

    public T divide(@Nullable BigDecimal divisor) {
        return apply(BigDecimal::divide, divisor);
    }

    public <Other extends AbstractFluentBigDecimal<Other>> T divide(@Nullable Other divisor) {
        return divide(mapValue(divisor));
    }

    public T divide(String divisorBigDecimal) {
        return divide(new BigDecimal(divisorBigDecimal));
    }

    public T divide(double divisor) {
        return divide(new BigDecimal(divisor));
    }

    public T divide(long divisor) {
        return divide(new BigDecimal(divisor));
    }

    public T pctToFraction() {
        return divide(HUNDRED);
    }

    public T fractionToPct() {
        return multiply(HUNDRED);
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
        return input.getValue();
    }

}
