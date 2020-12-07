package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;
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
@With
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FluentBigDecimal implements Serializable, Comparable<FluentBigDecimal> {
    private static final long serialVersionUID = 1646116594300550112L;

    public static final BigDecimal HUNDRED = new BigDecimal("100");

    @EqualsAndHashCode.Include
    private final @NonNull BigDecimal value;
    private final @NonNull MathContext mathContext;
    private final @NonNull Scaler scaler;

    public FluentBigDecimal(
        @NonNull BigDecimal value,
        @NonNull MathContext mathContext,
        @NonNull Scaler scaler
    ) {
        this.value = requireNonNull(value, "value required");
        this.mathContext = requireNonNull(mathContext, "mathContext required");
        this.scaler = requireNonNull(scaler, "scaler required");
    }

    public static @NonNull FluentBigDecimal of(@NonNull BigDecimal value, @NonNull MathContext mathContext, @NonNull Scaler scaler) {
        return ofRaw(value, mathContext, scaler)
            .round();
    }

    public static @NonNull FluentBigDecimal ofRaw(@NonNull BigDecimal value, @NonNull MathContext mathContext, @NonNull Scaler scaler) {
        return new FluentBigDecimal(value, mathContext, scaler);
    }

    /**
     * Actually round <strong>and</strong> scale.
     * <p>
     * Mostly needed when you started with a raw value and need to round it.
     */
    public @NonNull FluentBigDecimal round() {
        @NonNull FluentBigDecimal result = apply(BigDecimal::round);
        return result;
    }

    /**
     * Switch to new scaler and scale value accordingly.
     * <p>
     * If you need to switch to a new scaler <i>without</i> scaling, use {@link #withScaler(Scaler)}.
     */
    public @NonNull FluentBigDecimal roundInto(Scaler scaler) {
        var result = withScaler(scaler)
            .round();

        return result;
    }

    /**
     * Switch to new MathContext/Scaler and round/scale value accordingly.
     * <p>
     * If you need to switch to a new Configuration <i>without</i> scaling/rounding, use {@link #with(Configuration}.
     */
    public @NonNull FluentBigDecimal roundInto(Configuration configuration) {
        return of(getValue(), configuration.getMathContext(), configuration.getScaler());
    }

    public @NonNull FluentBigDecimal with(MathContext mathContext, Scaler scaler) {
        return ofRaw(getValue(), mathContext, scaler);
    }

    public @NonNull FluentBigDecimal with(Configuration configuration) {
        return ofRaw(getValue(), configuration.getMathContext(), configuration.getScaler());
    }

    /**
     * Compares current value and delegates to {@link BigDecimal#compareTo(BigDecimal)}.
     */
    @Override
    public int compareTo(@NonNull FluentBigDecimal o) {
        return getValue().compareTo(o.getValue());
    }

    public @NonNull FluentBigDecimal apply(@NonNull BiProjection projection, @Nullable BigDecimal argument) {
        if (argument == null) {
            return this;
        }

        var result = apply((value, mathContext) -> projection.project(value, argument, mathContext));

        return result;
    }

    public @NonNull FluentBigDecimal apply(@NonNull Projection projection) {
        var outcome = projection.project(value, getMathContext());
        requireNonNull(outcome, "Result of projection must not be null");

        var scaled = scaler.scale(outcome, getMathContext());
        requireNonNull(scaled, "Scaler must not return null");

        var result = withValue(scaled);

        return result;
    }

    public <T> T map(@NonNull Function<BigDecimal, T> projection) {
        var result = projection.apply(getValue());

        return result;
    }

    @Override
    public @NonNull String toString() {
        String result = String.format("%s[%s, %s]",
            FluentBigDecimal.class.getSimpleName(), value.toPlainString(), scaler);

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

    public @NonNull FluentBigDecimal add(@Nullable BigDecimal addend) {
        var result = apply(BigDecimal::add, addend);

        return result;
    }

    public @NonNull FluentBigDecimal add(@Nullable FluentBigDecimal addend) {
        FluentBigDecimal result = add(mapValue(addend));

        return result;
    }

    public @NonNull FluentBigDecimal subtract(@Nullable BigDecimal subtrahend) {
        FluentBigDecimal result = apply(BigDecimal::subtract, subtrahend);

        return result;
    }

    public @NonNull FluentBigDecimal subtract(@Nullable FluentBigDecimal subtrahend) {
        FluentBigDecimal result = subtract(mapValue(subtrahend));

        return result;
    }

    public @NonNull FluentBigDecimal multiply(@Nullable BigDecimal multiplicand) {
        FluentBigDecimal result = apply(BigDecimal::multiply, multiplicand);

        return result;
    }

    public @NonNull FluentBigDecimal multiply(@Nullable FluentBigDecimal multiplicand) {
        FluentBigDecimal result = multiply(mapValue(multiplicand));

        return result;
    }

    public @NonNull FluentBigDecimal divide(@Nullable BigDecimal divisor) {
        FluentBigDecimal result = apply(BigDecimal::divide, divisor);

        return result;
    }

    public @NonNull FluentBigDecimal divide(@Nullable FluentBigDecimal divisor) {
        FluentBigDecimal result = divide(mapValue(divisor));

        return result;
    }

    public @NonNull FluentBigDecimal pctToFraction() {
        FluentBigDecimal result = divide(HUNDRED);

        return result;
    }

    public @NonNull FluentBigDecimal fractionToPct() {
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
