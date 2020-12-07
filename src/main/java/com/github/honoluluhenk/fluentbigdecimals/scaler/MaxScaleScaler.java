package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * First round to precision, then reduce scale if needed ("Database" mode).
 * <p>
 * Databases usually allow only a definable <i>maximum</i> scale for decimals.
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class MaxScaleScaler implements Scaler {
    private static final long serialVersionUID = -8755733728910066293L;

    @With
    private final int maxScale;

    public MaxScaleScaler(int maxScale) {
        this.maxScale = maxScale;
    }

    @Override
    public @NonNull BigDecimal scale(@NonNull BigDecimal value, @NonNull MathContext mathContext) {
        int maxIntegerPrecision = mathContext.getPrecision() - getMaxScale();

        int intPrecision = value.precision() - value.scale();
        boolean precisionExceeded = intPrecision > maxIntegerPrecision;
        if (precisionExceeded) {
            throw new ArithmeticException(String.format("Cannot fit computed outcome %s into precision %s while still alowing for %s decimals.",
                value.toPlainString(), mathContext.getPrecision(), getMaxScale()));
        }

        BigDecimal scaled = adjustScale(value, mathContext.getRoundingMode());

        return scaled;
    }

    private @NonNull BigDecimal adjustScale(@NonNull BigDecimal value, @NonNull RoundingMode roundingMode) {
        boolean isScaleOk = value.scale() <= getMaxScale();
        if (isScaleOk) {
            return value;
        }

        BigDecimal result = value.setScale(getMaxScale(), roundingMode);

        return result;
    }

    public static MaxScaleScaler from(int maxScale) {
        return new MaxScaleScaler(maxScale);
    }

    /**
     * Copy-Factory.
     */
    public static MaxScaleScaler from(MaxScaleScaler other) {
        return new MaxScaleScaler(other.getMaxScale());
    }

    @Override
    public String toString() {
        return String.format("%s[%d]",
            getClass().getSimpleName(),
            getMaxScale()
        );
    }

}
