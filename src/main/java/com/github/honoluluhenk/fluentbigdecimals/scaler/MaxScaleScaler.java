package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Reduce scale if it exceeds the maximum ("Database" mode, also typically monetary calculations).
 * <p>
 * Databases usually allows only a definable <i>maximum</i> scale for decimals.
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class MaxScaleScaler implements Scaler, WithScale<MaxScaleScaler> {
    private static final long serialVersionUID = -8755733728910066293L;

    @With
    private final int maxScale;

    public MaxScaleScaler(int maxScale) {
        this.maxScale = maxScale;
    }

    @Override
    public BigDecimal scale(BigDecimal value, MathContext mathContext) {
        int maxIntegerPrecision = mathContext.getPrecision() - getMaxScale();

        int intPrecision = value.precision() - value.scale();
        boolean precisionExceeded = intPrecision > maxIntegerPrecision;
        if (precisionExceeded) {
            throw new ArithmeticException(String.format("Cannot fit computed outcome %s into precision %s while still allowing for %s decimals.",
                value.toPlainString(), mathContext.getPrecision(), getMaxScale()));
        }

        return adjustScale(value, mathContext.getRoundingMode());
    }

    private BigDecimal adjustScale(BigDecimal value, RoundingMode roundingMode) {
        int newScale = Math.min(value.scale(), getMaxScale());
        return value.setScale(newScale, roundingMode);
    }

    public static MaxScaleScaler of(int maxScale) {
        return new MaxScaleScaler(maxScale);
    }

    /**
     * Copy-Factory.
     */
    public static MaxScaleScaler of(MaxScaleScaler other) {
        return new MaxScaleScaler(other.getMaxScale());
    }

    @Override
    public String toString() {
        return String.format("%s[%d]",
            getClass().getSimpleName(),
            getMaxScale()
        );
    }

    @Override
    public MaxScaleScaler withScale(int newScale) {
        return of(newScale);
    }
}
