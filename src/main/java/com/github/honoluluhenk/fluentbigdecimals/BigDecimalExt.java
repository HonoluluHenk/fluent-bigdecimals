package com.github.honoluluhenk.fluentbigdecimals;

import lombok.EqualsAndHashCode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@EqualsAndHashCode()
public class BigDecimalExt implements Serializable {
    private static final long serialVersionUID = 1646116594300550112L;

    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100, 0);

    private final BigDecimal value;
    private final Adjuster adjuster;

    BigDecimalExt(BigDecimal value, Adjuster adjuster) {
        this.adjuster = requireNonNull(adjuster, "adjuster required");
        requireNonNull(value, "value required");
        this.value = adjust(value, adjuster);
    }

    private static BigDecimal adjust(BigDecimal value, Adjuster adjuster) {
        if (!adjuster.needsAdjusting(value)) {
            return value;
        }

        return adjuster.adjust(value);
    }

    private BigDecimal adjust(BigDecimal value) {
        return adjust(value, getAdjuster());
    }

    public static BigDecimalExt of(BigDecimal value, Adjuster adjuster) {
        return new BigDecimalExt(value, adjuster);
    }

    public BigDecimalExt withValue(BigDecimal value) {
        return new BigDecimalExt(value, adjuster);
    }

    // FIXME: make public?
    private BigDecimalExt apply(Function<BigDecimal, BigDecimal> function) {
        BigDecimal temp = function.apply(getValue());
        requireNonNull(temp);

        var result = withValue(temp);

        return result;
    }

    // FIXME: make public?
    private BigDecimalExt apply(BiFunction<BigDecimal, BigDecimal, BigDecimal> function, @Nullable BigDecimal argument) {
        if (argument == null) {
            return this;
        }

        BigDecimal temp = function.apply(getValue(), argument);
        requireNonNull(temp);

        var result = withValue(temp);

        return result;
    }

    public BigDecimalExt roundTo(Adjuster adjuster) {
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

    public BigDecimalExt add(@Nullable BigDecimal augend) {
        var result = apply(BigDecimal::add, augend);

        return result;
    }

    public BigDecimalExt add(@Nullable BigDecimalExt augend) {
        return add(mapValue(augend));
    }

    public BigDecimalExt add(@Nullable String bigDecimal) {
        if (bigDecimal == null) {
            return this;
        }

        return add(new BigDecimal(bigDecimal));
    }

    private BigDecimalExt addAll(Stream<BigDecimalExt> augend) {
        var result = augend.reduce(
            this,
            BigDecimalExt::add
        );

        return result;
    }

    /**
     * adds augement parameters to value, null values are ignored.
     */
    public BigDecimalExt addAll(@Nullable BigDecimal... augend) {
        var stream = Arrays.stream(augend)
            .filter(Objects::nonNull)
            .map(Helpers::castNonNull)
            .map(a -> BigDecimalExt.of(a, getAdjuster()));

        var result = addAll(stream);

        return result;
    }

    public BigDecimalExt addAll(@Nullable BigDecimalExt... augend) {
        Stream<BigDecimalExt> stream = Arrays.stream(augend)
            .filter(Objects::nonNull)
            .map(Helpers::castNonNull);

        var result = addAll(stream);

        return result;
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

    public boolean equalsComparingValue(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BigDecimalExt)) {
            return false;
        }
        BigDecimalExt other = (BigDecimalExt) o;
        if (!other.canEqual(this)) {
            return false;
        }
        BigDecimal this$value = getValue();
        BigDecimal other$value = other.getValue();
        if (this$value == null ? other$value != null : this$value.compareTo(other$value) != 0) {
            return false;
        }
        Adjuster this$adjuster = getAdjuster();
        Adjuster other$adjuster = other.getAdjuster();
        boolean result = Objects.equals(this$adjuster, other$adjuster);

        return result;
    }

    //    private void validatePrecision(BigDecimal value) {
    //        if (value.precision() > mathContext.getPrecision()) {
    //            throw new IllegalArgumentException(
    //                    String.format("precision does not match with expected precision: %d>%d, value=%s",
    //                            value.precision(), mathContext.getPrecision(), value.toPlainString()));
    //        }
    //        if (value.scale() > maxScale) {
    //            throw new IllegalArgumentException(
    //                    String.format("scale does not match with expected scale: %d>%d, value=%s",
    //                            value.scale(), maxScale, value.toPlainString()));
    //        }
    //    }

    private static @Nullable BigDecimal mapValue(@Nullable BigDecimalExt input) {
        if (input == null) {
            return null;
        }

        return input.getValue();
    }
}
