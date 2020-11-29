package com.github.honoluluhenk.fluentbigdecimals.adjuster;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * First round to precision, then reduce scale if needed.
 */
@Getter
@EqualsAndHashCode
public class FloatingPointAdjuster implements Adjuster {
    private static final long serialVersionUID = -8755733728910066293L;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    private final int maxScale;
    private final MathContext mathContext;

    public FloatingPointAdjuster(int precision, int maxScale, RoundingMode roundingMode) {
        if (precision < 1) {
            throw new IllegalArgumentException(format("Precision must be > 0 but was: %d", precision));
        }

        int actualPrecision = precision;
        //FIXME: should this not be done here but in from*()?
        if (maxScale >= precision) {
            // example value: "0.006" might have precision 1 and scale 3
            // Just adding up precision and scale yields an "intuitively" usable precision
            actualPrecision = maxScale + precision;
        }
        this.maxScale = maxScale;
        mathContext = new MathContext(
            actualPrecision,
            requireNonNull(roundingMode, "roundingMode required")
        );
    }

    @Override
    public BigDecimal adjust(BigDecimal value) {
        BigDecimal precisionAdjusted = adjustPrecision(value);
        BigDecimal scaled = adjustScale(precisionAdjusted);

        return scaled;
    }

    @NonNull
    private BigDecimal adjustPrecision(BigDecimal value) {
        boolean isPrecisionOk = value.precision() <= getPrecision();
        if (isPrecisionOk) {
            return value;
        }

        BigDecimal result = value
            .round(getMathContext());

        return result;
    }

    @NonNull
    private BigDecimal adjustScale(BigDecimal value) {
        boolean isScaleOk = value.scale() <= getMaxScale();
        if (isScaleOk) {
            return value;
        }

        BigDecimal result = value.setScale(getMaxScale(), getRoundingMode());

        return result;
    }


    public static FloatingPointAdjuster from(int precision, int maxScale, RoundingMode roundingMode) {
        return new FloatingPointAdjuster(precision, maxScale, roundingMode);
    }

    /**
     * See {@link #from(int, int, RoundingMode)}, using {@link RoundingMode#HALF_UP} (used by most business applications).
     */
    //FIXME: re-introduce lateron. This might introduce errors while developing if I forget to pass the RoundingMode
//    static FloatingPointAdjuster from(int precision, int maxScale) {
//        return from(precision, maxScale, DEFAULT_ROUNDING_MODE);
//    }
    public static FloatingPointAdjuster from(FloatingPointAdjuster other) {
        return new FloatingPointAdjuster(other.getPrecision(), other.getMaxScale(), other.getRoundingMode());
    }

    public int getPrecision() {
        return getMathContext().getPrecision();
    }

    public RoundingMode getRoundingMode() {
        return getMathContext().getRoundingMode();
    }

    public static FloatingPointAdjuster from(BigDecimal srcValue, RoundingMode roundingMode) {
        requireNonNull(srcValue, "srcValue required");
        requireNonNull(roundingMode, "roundingMode required");

        return new FloatingPointAdjuster(srcValue.precision(), srcValue.scale(), roundingMode);
    }

    public FloatingPointAdjuster withPrecision(int precision) {
        return new FloatingPointAdjuster(precision, getMaxScale(), getRoundingMode());
    }

    public FloatingPointAdjuster withMaxScale(int maxScale) {
        return new FloatingPointAdjuster(getPrecision(), maxScale, getRoundingMode());
    }

    public FloatingPointAdjuster withRoundingMode(RoundingMode roundingMode) {
        return new FloatingPointAdjuster(getPrecision(), getMaxScale(), roundingMode);
    }

    @Override
    public String toString() {
        return String.format("%s[%s,%d,%s]",
            getClass().getSimpleName(),
            getPrecision(),
            getMaxScale(),
            getRoundingMode()
        );
    }
}
