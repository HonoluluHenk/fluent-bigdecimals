package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * First round to precision, then reduce scale if needed.
 */
@Getter
@With
@EqualsAndHashCode
public class FixedPointScaler implements Scaler {
    private static final long serialVersionUID = -8755733728910066293L;

    private final int maxScale;

    public FixedPointScaler(int maxScale) {
        this.maxScale = maxScale;
    }

    @Override
    public BigDecimal scale(BigDecimal value, MathContext mathContext) {
        int maxIntegerPrecision = mathContext.getPrecision() - maxScale;
        int intPrecision = value.precision() - value.scale();

        boolean precisionExceeded = intPrecision > maxIntegerPrecision;
        if (precisionExceeded) {
            throw new ArithmeticException(String.format("Cannot fit %s into precision %s while still alowing for %s decimals.",
                value.toPlainString(), mathContext.getPrecision(), maxScale));
        }
        BigDecimal scaled = adjustScale(value, mathContext.getRoundingMode());

        return scaled;
    }

    @NonNull
    private BigDecimal adjustScale(BigDecimal value, RoundingMode roundingMode) {
        boolean isScaleOk = value.scale() <= getMaxScale();
        if (isScaleOk) {
            return value;
        }

        BigDecimal result = value.setScale(getMaxScale(), roundingMode);

        return result;
    }


    public static FixedPointScaler from(int maxScale) {
        return new FixedPointScaler(maxScale);
    }

    /**
     * Copy-Factory.
     */
    public static FixedPointScaler from(FixedPointScaler other) {
        return new FixedPointScaler(other.getMaxScale());
    }

    @Override
    public String toString() {
        return String.format("%s[%d]",
            getClass().getSimpleName(),
            getMaxScale()
        );
    }
}
