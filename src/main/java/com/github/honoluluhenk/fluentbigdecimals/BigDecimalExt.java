package com.github.honoluluhenk.fluentbigdecimals;

import lombok.EqualsAndHashCode;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@EqualsAndHashCode()
public class BigDecimalExt implements Serializable {
    private static final long serialVersionUID = 1646116594300550112L;

    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100, 0);

    private final BigDecimal value;
    private final BigDecimalContext context;

    public BigDecimalExt(BigDecimal value, BigDecimalContext context) {
        this.context = requireNonNull(context, "context required");
        requireNonNull(value, "value required");
        this.value = roundTo(value, context);
    }

    public static BigDecimalExt of(BigDecimal value) {
        return new BigDecimalExt(value, BigDecimalContext.from(value));
    }

    public static BigDecimalExt of(String bigDecimalValue) {
        return of(new BigDecimal(bigDecimalValue));
    }

    public BigDecimalExt withValue(BigDecimal value) {
        return new BigDecimalExt(value, context);
    }

    // FIXME: make public
    private BigDecimalExt apply(Function<BigDecimal, BigDecimal> function) {
        BigDecimal result = function.apply(getValue());
        requireNonNull(result);

        return withValue(result);
    }

    // FIXME: make public
    private BigDecimalExt apply(BiFunction<BigDecimal, BigDecimal, BigDecimal> function, BigDecimal argument) {
        requireNonNull(argument);

        BigDecimal result = function.apply(getValue(), argument);
        requireNonNull(result);

        return withValue(result);
    }

    // FIXME: make public
    private BigDecimalExt apply(BiFunction<BigDecimal, Integer, BigDecimal> function, int argument) {
        BigDecimal result = function.apply(getValue(), argument);
        requireNonNull(result);

        return withValue(result);
    }

    BigDecimal roundTo(
        @UnderInitialization BigDecimalExt this,
        BigDecimal value,
        BigDecimalContext context
    ) {
        BigDecimal result = value
            .round(context.getMathContext());

        if (result.scale() > context.getMaxScale()) {
            result = result.setScale(context.getMaxScale(), context.getRoundingMode());
        }

        return result;
    }

    public BigDecimalExt roundTo(BigDecimalContext context) {
        return new BigDecimalExt(getValue(), context);
    }

    public BigDecimal getValue() {
        return value;
    }

    public BigDecimalContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return String.format("BigDecimalExt[%s, context=%s]", value.toPlainString(), context);
    }

    private BigDecimal addImpl(BigDecimal value, @Nullable BigDecimal augend) {
        if (augend == null) {
            return value;
        }

        BigDecimal unscaled = value
            .add(augend);

        boolean needsScaling = unscaled.scale() > context.getMaxScale();
        BigDecimal result = needsScaling
            ? unscaled.setScale(context.getMaxScale(), context.getRoundingMode())
            : unscaled;

        return result;
    }

    public BigDecimalExt add(@Nullable BigDecimal augend) {
        BigDecimal exact = addImpl(value, augend);
        BigDecimalExt result = withValue(exact);

        return result;
    }

    public BigDecimalExt add(@Nullable BigDecimalExt augend) {
        return add(mapValue(augend));
    }

    /**
     * adds augement parameters to value, null values are treated as zero
     *
     * @throws IllegalArgumentException if the resulting value exceeds the defined precision
     */
    public BigDecimalExt addAll(@Nullable BigDecimal... augend) {

        BigDecimal result = value;
        for (BigDecimal valueToAdd : augend) {
            if (valueToAdd == null) {
                continue;
            }

            result = result
                .add(valueToAdd, context.getMathContext())
                .setScale(context.getMaxScale(), context.getRoundingMode());
        }

        return withValue(result);
    }

    private BigDecimal subtractImpl(BigDecimal value, BigDecimal subtrahend) {
        BigDecimal result = value
            .subtract(subtrahend, context.getMathContext())
            .setScale(context.getMaxScale(), context.getRoundingMode());

        return result;
    }

    /**
     * @throws IllegalArgumentException if the resulting value exceeds the defined precision
     */
    public BigDecimalExt subtract(@Nullable BigDecimal subtrahend) {
        if (subtrahend == null) {
            return this;
        }
        BigDecimal result = subtractImpl(value, subtrahend);

        return withValue(result);
    }

    public BigDecimalExt subtractAll(@Nullable BigDecimal... subtrahends) {
        BigDecimal result = value;
        for (BigDecimal subtrahend : subtrahends) {
            if (subtrahend == null) {
                continue;
            }

            result = subtractImpl(result, subtrahend);
        }

        return withValue(result);
    }

    private BigDecimal multiplyImpl(BigDecimal value, BigDecimal multiplicand) {
        BigDecimal result = value
            .multiply(multiplicand, context.getMathContext())
            .setScale(context.getMaxScale(), context.getRoundingMode());

        return result;
    }

    /**
     * @throws IllegalArgumentException if the resulting value exceeds the defined precision
     */
    public BigDecimalExt multiply(@Nullable BigDecimal multiplicand) {
        if (multiplicand == null) {
            return this;
        }

        BigDecimal result = multiplyImpl(value, multiplicand);

        return withValue(result);
    }

    /**
     * @throws IllegalArgumentException if the resulting values exceeds the defined precision
     */
    public BigDecimalExt multiplyAll(@Nullable BigDecimal... values) {
        BigDecimal result = Arrays.stream(values)
            .filter(Objects::nonNull)
            .map(Helpers::castNonNull)
            .reduce(BigDecimal.ONE, this::multiplyImpl);

        requireNonNull(result);

        return withValue(result);
    }

    private BigDecimal divideImpl(BigDecimal dividend, BigDecimal divisor) {
        if (0 == BigDecimal.ZERO.compareTo(divisor)) {
            throw new IllegalArgumentException("Divide by zero: " + dividend + '/' + divisor);
        }
        BigDecimal result = dividend.divide(
            divisor,
            context.getMaxScale(),
            context.getRoundingMode());

        return result;
    }

    /**
     * @throws IllegalArgumentException if the resulting value exceeds the defined precision
     */
    public BigDecimalExt divide(@Nullable BigDecimal divisor) {
        if (divisor == null) {
            return this;
        }

        BigDecimal result = divideImpl(value, divisor);

        return withValue(result);
    }

    /**
     * @throws IllegalArgumentException if the resulting values exceeds the defined precision
     */
    public BigDecimalExt divideAll(@Nullable BigDecimal... values) {
        BigDecimal result = Arrays.stream(values)
            .filter(Objects::nonNull)
            .map(Helpers::castNonNull)
            .reduce(BigDecimal.ONE, this::divideImpl);

        return withValue(result);
    }

    /**
     * Konvertiert eine Prozentzahl (z.B. 34%) in eine Bruchzahl (z.B. 0.34), i.E.: dividiert durch 100
     */
    public BigDecimalExt pctToFraction() {
        BigDecimal result = divideImpl(value, HUNDRED);

        return withValue(result);
    }

    /**
     * Konvertiert eine Bruchzahl (z.B. 0.34) in eine Prozentzahl (z.B. 34%), i.E.: multipliziert mit 100
     */
    public BigDecimalExt fractionToPct() {
        BigDecimal result = multiplyImpl(value, HUNDRED);

        return withValue(result);
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
        BigDecimalContext this$context = getContext();
        BigDecimalContext other$context = other.getContext();
        boolean result = Objects.equals(this$context, other$context);

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
