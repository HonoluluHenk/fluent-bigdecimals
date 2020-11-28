package com.github.honoluluhenk.fluentbigdecimals;

import lombok.EqualsAndHashCode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

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

    BigDecimalExt(BigDecimal value, Adjuster adjuster) {
        this.adjuster = requireNonNull(adjuster, "adjuster required");
        requireNonNull(value, "value required");
        this.value = adjuster.adjust(value);
    }

    public static BigDecimalExt of(BigDecimal value, Adjuster adjuster) {
        return new BigDecimalExt(value, adjuster);
    }

    public static BigDecimalExt of(String bigDecimal, Adjuster adjuster) {
        return new BigDecimalExt(new BigDecimal(bigDecimal), adjuster);
    }

    public BigDecimalExt withValue(BigDecimal value) {
        return new BigDecimalExt(value, adjuster);
    }

    /**
     * Compares {@link #getValue()} and delegates to {@link BigDecimal#compareTo(BigDecimal)}.
     */
    @Override
    public int compareTo(BigDecimalExt o) {
        return getValue().compareTo(o.getValue());
    }

    // FIXME: make public?
    private BigDecimalExt apply(UnaryOperator<BigDecimal> function) {
        BigDecimal temp = function.apply(getValue());
        requireNonNull(temp);

        var result = withValue(temp);

        return result;
    }


    // FIXME: make public?
    private BigDecimalExt apply(BinaryOperator<BigDecimal> function, @Nullable BigDecimal argument) {
        if (argument == null) {
            return this;
        }

        BigDecimal temp = function.apply(getValue(), argument);
        requireNonNull(temp);

        var result = withValue(temp);

        return result;
    }

    public BigDecimalExt using(Adjuster adjuster) {
        return new BigDecimalExt(getValue(), adjuster);
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

    public BigDecimalExt add(@Nullable String bigDecimal) {
        return add(mapValue(bigDecimal));
    }

    public BigDecimalExt subtract(@Nullable BigDecimal subtrahend) {
        BigDecimalExt result = apply(BigDecimal::subtract, subtrahend);

        return result;
    }

    public BigDecimalExt multiply(@Nullable BigDecimal multiplicand) {
        BigDecimalExt result = apply(BigDecimal::multiply, multiplicand);

        return result;
    }

    public BigDecimalExt divide(@Nullable BigDecimal divisor) {
        if (divisor == null) {
            return this;
        }

        if (0 == BigDecimal.ZERO.compareTo(divisor)) {
            throw new IllegalArgumentException("Divide by zero: " + getValue() + '/' + divisor);
        }

        var result = apply(BigDecimal::divide, divisor);

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

    private static @Nullable BigDecimal mapValue(@Nullable String bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }

        return new BigDecimal(bigDecimal);
    }
}
