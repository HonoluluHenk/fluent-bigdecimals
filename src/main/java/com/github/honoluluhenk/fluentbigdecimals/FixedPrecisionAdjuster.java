package com.github.honoluluhenk.fluentbigdecimals;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Getter
@EqualsAndHashCode
public class FixedPrecisionAdjuster implements Adjuster {
    private static final long serialVersionUID = -8755733728910066293L;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    private final int precision;
    private final RoundingMode roundingMode;
    private final int maxScale;
    @EqualsAndHashCode.Exclude
    private final MathContext mathContext;

    public FixedPrecisionAdjuster(int precision, int maxScale, RoundingMode roundingMode) {
        if (precision < 1) {
            throw new IllegalArgumentException(format("Precision must be > 0 but was: %d", precision));
        }
        if (maxScale >= precision) {
            // example value: "0.006" might have precision 1 and scale 3
            // Just adding up precision and scale yields an "intuitively" usable precision
            this.precision = maxScale + precision;
        } else {
            this.precision = precision;
        }
        this.maxScale = maxScale;
        this.roundingMode = requireNonNull(roundingMode, "roundingMode required");
        mathContext = new MathContext(precision, roundingMode);
    }

    @Override
    public BigDecimal adjust(BigDecimal value) {
        BigDecimal result = value
            .round(getMathContext());

        if (result.scale() > getMaxScale()) {
            result = result.setScale(getMaxScale(), getRoundingMode());
        }

        return result;
    }

    public BigDecimalExt withValue(BigDecimal value) {
        return new BigDecimalExt(value, this);
    }

    public BigDecimalExt withValue(String bigDecimal) {
        return new BigDecimalExt(new BigDecimal(bigDecimal), this);
    }

    public static FixedPrecisionAdjuster from(int precision, int maxScale, RoundingMode roundingMode) {
        return new FixedPrecisionAdjuster(precision, maxScale, roundingMode);
    }

    /**
     * See {@link #from(int, int, RoundingMode)}, using {@link RoundingMode#HALF_UP} (used by most business applications).
     */
    //FIXME: re-introduce lateron. This might introduce errors while developing if I forget to pass the RoundingMode
    public static FixedPrecisionAdjuster from(int precision, int maxScale) {
        return from(precision, maxScale, DEFAULT_ROUNDING_MODE);
    }

    public static FixedPrecisionAdjuster from(FixedPrecisionAdjuster other) {
        return new FixedPrecisionAdjuster(other.getPrecision(), other.getMaxScale(), other.getRoundingMode());
    }

    public static FixedPrecisionAdjuster from(BigDecimal srcValue, RoundingMode roundingMode) {
        requireNonNull(srcValue, "srcValue required");
        requireNonNull(roundingMode, "roundingMode required");

        return new FixedPrecisionAdjuster(srcValue.precision(), srcValue.scale(), roundingMode);
    }

    //FIXME: re-introduce lateron. This might introduce errors while developing if I forget to pass the RoundingMode
//    public static BigDecimalContext from(BigDecimal input) {
//        return from(input, RoundingMode.HALF_UP);
//    }

    public FixedPrecisionAdjuster withPrecision(int precision) {
        return new FixedPrecisionAdjuster(precision, getMaxScale(), getRoundingMode());
    }

    public FixedPrecisionAdjuster withMaxScale(int maxScale) {
        return new FixedPrecisionAdjuster(getPrecision(), maxScale, getRoundingMode());
    }

    public FixedPrecisionAdjuster withRoundingMode(RoundingMode roundingMode) {
        return new FixedPrecisionAdjuster(getPrecision(), getMaxScale(), roundingMode);
    }

    @Override
    public String toString() {
        return String.format("%s[%s,%d,%s]", getClass().getSimpleName(), precision, maxScale, roundingMode);
    }
}
