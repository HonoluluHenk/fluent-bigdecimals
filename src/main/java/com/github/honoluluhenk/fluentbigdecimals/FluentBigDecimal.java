package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Function;

import static com.github.honoluluhenk.fluentbigdecimals.Projection.identity;
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

    public @NonNull FluentBigDecimal adjust() {
        @NonNull FluentBigDecimal result = apply(identity());
        return result;
    }

    /**
     * Switch to new scaler and adjust value accordingly.
     * <p>
     * Related: {@link #withScaler(Scaler)}.
     */
    public @NonNull FluentBigDecimal adjustInto(Scaler scaler) {
        var result = withScaler(scaler)
            .adjust();

        return result;
    }

    /**
     * Compares {@link #getValue()} and delegates to {@link BigDecimal#compareTo(BigDecimal)}.
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
        @NonNull var outcome = projection.project(value, getMathContext());

        @NonNull var scaled = scaler.scale(outcome, getMathContext());

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
