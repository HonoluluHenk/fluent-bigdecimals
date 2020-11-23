package com.github.honoluluhenk.fluentbigdecimals;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Getter
@EqualsAndHashCode
public class BigDecimalContext implements Serializable {
    private static final long serialVersionUID = -8755733728910066293L;

    private final int precision;
    private final RoundingMode roundingMode;
    private final int maxScale;
    private final MathContext mathContext;

    BigDecimalContext(int precision, int maxScale, RoundingMode roundingMode) {
        if (precision < 1) {
            throw new IllegalArgumentException(format("Precision must be > 0 but was: %d", precision));
        }
        if (maxScale >= precision) {
            throw new IllegalArgumentException(format("Scale must be < precision but was: %d < %d",
                    maxScale, precision));
        }
        this.precision = precision;
        this.maxScale = maxScale;
        this.roundingMode = requireNonNull(roundingMode, "RoundingMode required");
        mathContext = new MathContext(precision, roundingMode);
    }

    public BigDecimalExt withValue(BigDecimal value) {
        return new BigDecimalExt(value, this);
    }

    public static BigDecimalContext from(int precision, int maxScale, RoundingMode roundingMode) {
        return new BigDecimalContext(precision, maxScale, roundingMode);
    }

    /**
     * See {@link #from(int, int, RoundingMode)}, using {@link RoundingMode#HALF_UP} (used by most business applications).
     */
    public static BigDecimalContext from(int precision, int maxScale) {
        return from(precision, maxScale, RoundingMode.HALF_UP);
    }

    public static BigDecimalContext from(BigDecimalContext other) {
        return new BigDecimalContext(other.getPrecision(), other.getMaxScale(), other.getRoundingMode());
    }

    public BigDecimalContext withPrecision(int precision) {
        return new BigDecimalContext(precision, getMaxScale(), getRoundingMode());
    }

    public BigDecimalContext withMaxScale(int maxScale) {
        return new BigDecimalContext(getPrecision(), maxScale, getRoundingMode());
    }

    public BigDecimalContext withRoundingMode(RoundingMode roundingMode) {
        return new BigDecimalContext(getPrecision(), getMaxScale(), roundingMode);
    }
}
