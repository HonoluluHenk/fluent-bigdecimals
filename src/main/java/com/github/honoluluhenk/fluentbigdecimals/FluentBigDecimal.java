package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.adjuster.Adjuster;
import lombok.EqualsAndHashCode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Regarding equals/hashcode/compareTo: see {@link BigDecimal};
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FluentBigDecimal implements Serializable, Comparable<FluentBigDecimal> {
    private static final long serialVersionUID = 1646116594300550112L;

    public static final BigDecimal HUNDRED = new BigDecimal("100");

    @EqualsAndHashCode.Include
    private final BigDecimal value;
    private final Adjuster adjuster;
    private final BiFunction<BigDecimal, Adjuster, ? extends FluentBigDecimal> instantiator;

    FluentBigDecimal(BigDecimal value, Adjuster adjuster) {
        this(value, adjuster, FluentBigDecimal::new);
    }

    protected FluentBigDecimal(
        BigDecimal value,
        Adjuster adjuster,
        BiFunction<BigDecimal, Adjuster, ? extends FluentBigDecimal> instantiator
    ) {
        this.adjuster = requireNonNull(adjuster, "adjuster required");
        this.value = requireNonNull(value, "value required");
        this.instantiator = instantiator;
    }

    public static FluentBigDecimal valueOf(BigDecimal value, Adjuster adjuster) {
        return new FluentBigDecimal(value, adjuster);
    }

    public static FluentBigDecimal valueOf(String bigDecimal, Adjuster adjuster) {
        return valueOf(new BigDecimal(bigDecimal), adjuster);
    }

    public static FluentBigDecimal valueOf(long value, Adjuster adjuster) {
        return valueOf(BigDecimal.valueOf(value), adjuster);
    }

    public static FluentBigDecimal valueOf(double value, Adjuster adjuster) {
        return valueOf(BigDecimal.valueOf(value), adjuster);
    }

    public static FluentBigDecimal valueOf(BigInteger value, Adjuster adjuster) {
        return valueOf(new BigDecimal(value), adjuster);
    }

    public FluentBigDecimal withValue(BigDecimal value) {
        return instantiator.apply(value, adjuster);
    }

    public FluentBigDecimal adjust() {
        return adjusted(getValue());
    }

    private FluentBigDecimal adjusted(BigDecimal value) {
        var adjusted = adjuster.adjust(value);

        return withValue(adjusted);
    }

    /**
     * Switch adjuster while keeeping the value unchanged.
     * <p>
     * Related: {@link #adjustInto(Adjuster)}.
     */
    public FluentBigDecimal withAdjuster(Adjuster adjuster) {
        return instantiator.apply(getValue(), adjuster);
    }

    /**
     * Switch to new adjuster and adjust value accordingly.
     * <p>
     * Related: {@link #withAdjuster(Adjuster)}.
     */
    public FluentBigDecimal adjustInto(Adjuster adjuster) {
        var result = valueOf(getValue(), adjuster)
            .adjust();

        return result;
    }

    /**
     * Compares {@link #getValue()} and delegates to {@link BigDecimal#compareTo(BigDecimal)}.
     */
    @Override
    public int compareTo(FluentBigDecimal o) {
        return getValue().compareTo(o.getValue());
    }

    public FluentBigDecimal apply(Function<BigDecimal, BigDecimal> function) {
        BigDecimal temp = function.apply(getValue());
        requireNonNull(temp);

        var result = adjusted(temp);

        return result;
    }


    public FluentBigDecimal apply(ProjectionFunction<BigDecimal, BigDecimal, BigDecimal> function, @Nullable BigDecimal argument) {
        if (argument == null) {
            return this;
        }

        BigDecimal temp = function.apply(getValue(), argument, adjuster.getMathContext());
        requireNonNull(temp);

        var result = adjusted(temp);


        return result;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Adjuster getAdjuster() {
        return adjuster;
    }

    @Override
    public String toString() {
        return String.format("BigDecimalExt[%s, %s]", value.toPlainString(), adjuster);
    }

    public FluentBigDecimal add(@Nullable BigDecimal addend) {
        var result = apply(BigDecimal::add, addend);

        return result;
    }

    public FluentBigDecimal add(@Nullable FluentBigDecimal addend) {
        return add(mapValue(addend));
    }

    public FluentBigDecimal subtract(@Nullable BigDecimal subtrahend) {
        FluentBigDecimal result = apply(BigDecimal::subtract, subtrahend);

        return result;
    }

    public FluentBigDecimal subtract(@Nullable FluentBigDecimal subtrahend) {
        FluentBigDecimal result = subtract(mapValue(subtrahend));

        return result;
    }

    public FluentBigDecimal multiply(@Nullable BigDecimal multiplicand) {
        FluentBigDecimal result = apply(BigDecimal::multiply, multiplicand);

        return result;
    }

    public FluentBigDecimal multiply(@Nullable FluentBigDecimal multiplicand) {
        FluentBigDecimal result = multiply(mapValue(multiplicand));

        return result;
    }

    public FluentBigDecimal divide(@Nullable BigDecimal divisor) {
        FluentBigDecimal result = apply(BigDecimal::divide, divisor);

        return result;
    }

    public FluentBigDecimal divide(@Nullable FluentBigDecimal divisor) {
        FluentBigDecimal result = divide(mapValue(divisor));

        return result;
    }

    public FluentBigDecimal pctToFraction() {
        FluentBigDecimal result = divide(HUNDRED);

        return result;
    }

    public FluentBigDecimal fractionToPct() {
        FluentBigDecimal result = multiply(HUNDRED);

        return result;
    }

    private static @Nullable BigDecimal mapValue(@Nullable FluentBigDecimal input) {
        if (input == null) {
            return null;
        }

        return input.getValue();
    }

}
