package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static java.util.Objects.requireNonNull;

/**
 * Regarding equals/hashcode/compareTo: see {@link BigDecimal};
 */
@Getter
@With
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FluentBigDecimal implements Serializable, Comparable<FluentBigDecimal> {
    private static final long serialVersionUID = 1646116594300550112L;

    public static final BigDecimal HUNDRED = new BigDecimal("100");

    @EqualsAndHashCode.Include
    private final BigDecimal value;
    private final MathContext mathContext;
    private final Scaler scaler;

    protected FluentBigDecimal(
        BigDecimal value,
        MathContext mathContext,
        Scaler scaler
    ) {
        this.value = requireNonNull(value, "value required");
        this.mathContext = requireNonNull(mathContext, "mathContext required"); // FIXME: test rnn
        this.scaler = requireNonNull(scaler, "scaler required");
    }

    public static FluentBigDecimal valueOf(BigDecimal value, MathContext mathContext, Scaler scaler) {
        return new FluentBigDecimal(value, mathContext, scaler);
    }

    public static FluentBigDecimal valueOf(String bigDecimal, MathContext mathContext, Scaler scaler) {
        return valueOf(new BigDecimal(bigDecimal), mathContext, scaler);
    }

    public static FluentBigDecimal valueOf(long value, MathContext mathContext, Scaler scaler) {
        return valueOf(BigDecimal.valueOf(value), mathContext, scaler);
    }

    public static FluentBigDecimal valueOf(double value, MathContext mathContext, Scaler scaler) {
        return valueOf(BigDecimal.valueOf(value), mathContext, scaler);
    }

    public static FluentBigDecimal valueOf(BigInteger value, MathContext mathContext, Scaler scaler) {
        return valueOf(new BigDecimal(value), mathContext, scaler);
    }

    public FluentBigDecimal adjust() {
        ProjectionFunction<BigDecimal, BigDecimal, BigDecimal> identity = (a, b, mc) -> a;
        FluentBigDecimal result = apply(identity, getValue());

        return result;
    }
//
//    private FluentBigDecimal adjusted(BigDecimal value) {
//        var adjusted = scaler.scale(value, getMathContext());
//        FluentBigDecimal result = withValue(adjusted);
//
//        return result;
//    }

    /**
     * Switch to new scaler and adjust value accordingly.
     * <p>
     * Related: {@link #withScaler(Scaler)}.
     */
    public FluentBigDecimal adjustInto(Scaler scaler) {
        var result = withScaler(scaler)
            .adjust();

        return result;
    }

    /**
     * Compares {@link #getValue()} and delegates to {@link BigDecimal#compareTo(BigDecimal)}.
     */
    @Override
    public int compareTo(FluentBigDecimal o) {
        int result = getValue().compareTo(o.getValue());

        return result;
    }

//    public FluentBigDecimal applyUnary(UnaryOperator<BigDecimal> function) {
//        BigDecimal temp = function.apply(getValue());
//        requireNonNull(temp);
//
//        var result = adjusted(temp);
//
//        return result;
//    }


    public FluentBigDecimal apply(ProjectionFunction<BigDecimal, BigDecimal, BigDecimal> function, @Nullable BigDecimal argument) {
        if (argument == null) {
            return this;
        }

        BigDecimal outcome = scaler.apply(function, getValue(), argument);
        requireNonNull(outcome, "Null result from Scaler not allowed");

        var result = withValue(outcome);

        return result;
    }

    @Override
    public String toString() {
        String result = String.format("BigDecimalExt[%s, %s]", value.toPlainString(), scaler);

        return result;
    }

    public FluentBigDecimal add(@Nullable BigDecimal addend) {
        var result = apply(BigDecimal::add, addend);

        return result;
    }

    public FluentBigDecimal add(@Nullable FluentBigDecimal addend) {
        FluentBigDecimal result = add(mapValue(addend));

        return result;
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
        BigDecimal result = input.getValue();

        return result;
    }

}
