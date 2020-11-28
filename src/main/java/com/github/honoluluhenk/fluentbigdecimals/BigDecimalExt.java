package com.github.honoluluhenk.fluentbigdecimals;

import lombok.EqualsAndHashCode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.github.honoluluhenk.fluentbigdecimals.Helpers.curryReverse;
import static java.util.Objects.requireNonNull;

/**
 * Regarding equals/hashcode/compareTo: see {@link BigDecimal};
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BigDecimalExt implements Serializable, Comparable<BigDecimalExt> {
    private static final long serialVersionUID = 1646116594300550112L;

    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100, 0);

    @EqualsAndHashCode.Include
    private final BigDecimal value;
    private final Adjuster adjuster;
    private final BiFunction<BigDecimal, Adjuster, ? extends BigDecimalExt> instantiator;

    BigDecimalExt(BigDecimal value, Adjuster adjuster) {
        this(value, adjuster, BigDecimalExt::new);
    }

    protected BigDecimalExt(
        BigDecimal value,
        Adjuster adjuster,
        BiFunction<BigDecimal, Adjuster, ? extends BigDecimalExt> instantiator
    ) {
        this.adjuster = requireNonNull(adjuster, "adjuster required");
        this.value = requireNonNull(value, "value required");
        this.instantiator = instantiator;
    }

    public static BigDecimalExt valueOf(BigDecimal value, Adjuster adjuster) {
        return new BigDecimalExt(value, adjuster);
    }

    public static BigDecimalExt valueOf(String bigDecimal, Adjuster adjuster) {
        return valueOf(new BigDecimal(bigDecimal), adjuster);
    }

    public static BigDecimalExt valueOf(long value, Adjuster adjuster) {
        return valueOf(BigDecimal.valueOf(value), adjuster);
    }

    public static BigDecimalExt valueOf(double value, Adjuster adjuster) {
        return valueOf(BigDecimal.valueOf(value), adjuster);
    }

    public static BigDecimalExt valueOf(BigInteger value, Adjuster adjuster) {
        return valueOf(new BigDecimal(value), adjuster);
    }

    public BigDecimalExt withValue(BigDecimal value) {
        return instantiator.apply(value, adjuster);
    }

    public BigDecimalExt adjust() {
        return adjusted(getValue());
    }

    private BigDecimalExt adjusted(BigDecimal value) {
        var adjusted = adjuster.adjust(value);

        return withValue(adjusted);
    }

    /**
     * Compares {@link #getValue()} and delegates to {@link BigDecimal#compareTo(BigDecimal)}.
     */
    @Override
    public int compareTo(BigDecimalExt o) {
        return getValue().compareTo(o.getValue());
    }

    public BigDecimalExt apply(Function<BigDecimal, BigDecimal> function) {
        BigDecimal temp = function.apply(getValue());
        requireNonNull(temp);

        var result = adjusted(temp);

        return result;
    }


    public BigDecimalExt apply(BiFunction<BigDecimal, BigDecimal, BigDecimal> function, @Nullable BigDecimal argument) {
        if (argument == null) {
            return this;
        }

        Function<BigDecimal, BigDecimal> operation = curryReverse(function, argument);
        var result = apply(operation);

        return result;
    }

    public BigDecimalExt using(Adjuster adjuster) {
        return instantiator.apply(getValue(), adjuster);
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

    public BigDecimalExt add(@Nullable BigDecimal addend) {
        var result = apply(BigDecimal::add, addend);

        return result;
    }

    public BigDecimalExt add(@Nullable BigDecimalExt addend) {
        return add(mapValue(addend));
    }

    public BigDecimalExt subtract(@Nullable BigDecimal subtrahend) {
        BigDecimalExt result = apply(BigDecimal::subtract, subtrahend);

        return result;
    }

    public BigDecimalExt subtract(@Nullable BigDecimalExt subtrahend) {
        BigDecimalExt result = subtract(mapValue(subtrahend));

        return result;
    }

    public BigDecimalExt multiply(@Nullable BigDecimal multiplicand) {
        BigDecimalExt result = apply(BigDecimal::multiply, multiplicand);

        return result;
    }

    public BigDecimalExt multiply(@Nullable BigDecimalExt multiplicand) {
        BigDecimalExt result = multiply(mapValue(multiplicand));

        return result;
    }

    public BigDecimalExt divide(@Nullable BigDecimal divisor) {
        BigDecimalExt result = apply(BigDecimal::divide, divisor);

        return result;
    }

    public BigDecimalExt divide(@Nullable BigDecimalExt divisor) {
        BigDecimalExt result = divide(mapValue(divisor));

        return result;
    }

    public BigDecimalExt pctToFraction() {
        BigDecimalExt result = divide(HUNDRED);

        return result;
    }

    public BigDecimalExt fractionToPct() {
        BigDecimalExt result = multiply(HUNDRED);

        return result;
    }

    private static @Nullable BigDecimal mapValue(@Nullable BigDecimalExt input) {
        if (input == null) {
            return null;
        }

        return input.getValue();
    }

}
