package com.github.honoluluhenk.fluentbigdecimals.scaler;

import com.github.honoluluhenk.fluentbigdecimals.ProjectionFunction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.util.Objects.requireNonNull;

/**
 * First round to precision, then reduce scale if needed.
 */
@Getter
@With
@EqualsAndHashCode
public class FixedPointScaler implements Scaler {
    private static final long serialVersionUID = -8755733728910066293L;

    private final MathContext mathContext;
    private final int maxScale;

    public FixedPointScaler(MathContext mathContext, int maxScale) {
        this.mathContext = requireNonNull(mathContext, "mathContext required"); // FIXME: test rnn
        this.maxScale = maxScale;
    }

    @Override
    public <Argument> BigDecimal apply(ProjectionFunction<BigDecimal, Argument, BigDecimal> function, BigDecimal value, Argument argument) {
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


    // FIXME: change signature: precision, scale, roundingMode
    public static FixedPointScaler from(int precision, RoundingMode roundingMode, int maxScale) {
        return new FixedPointScaler(new MathContext(precision, roundingMode), maxScale);
    }


    public static FixedPointScaler from(MathContext mathContext, int maxScale) {
        return new FixedPointScaler(mathContext, maxScale);
    }

    /**
     * Copy-Factory.
     */
    public static FixedPointScaler from(FixedPointScaler other) {
        return new FixedPointScaler(other.getMathContext(), other.getMaxScale());
    }

    @Override
    public String toString() {
        return String.format("%s[%d,%d,%s]",
            getClass().getSimpleName(),
            getMathContext().getPrecision(),
            getMaxScale(),
            getMathContext().getRoundingMode()
        );
    }

}
