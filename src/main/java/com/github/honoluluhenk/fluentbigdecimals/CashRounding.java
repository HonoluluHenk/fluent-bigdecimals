package com.github.honoluluhenk.fluentbigdecimals;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_UP;

@AllArgsConstructor
@Getter
@With
public class CashRounding implements Serializable {
    private static final long serialVersionUID = 7429229149421339478L;
    public static final int CASH_ROUNDING_SCALE = 2;
    public static final RoundingMode CASH_ROUNDING_MODE = HALF_UP;

    private final @NonNull BigDecimal unit;
    private final @NonNull RoundingMode roundingMode;

    public static @NonNull CashRounding of(@NonNull BigDecimal unit) {
        return of(unit, CASH_ROUNDING_MODE);
    }

    public static @NonNull CashRounding of(@NonNull CashRoundingUnits unit) {
        return of(unit.getUnit(), CASH_ROUNDING_MODE);
    }

    public static @NonNull CashRounding of(@NonNull CashRoundingUnits unit, @NonNull RoundingMode roundingMode) {
        return of(unit.getUnit(), roundingMode);
    }

    public static @NonNull CashRounding of(@NonNull BigDecimal unit, @NonNull RoundingMode roundingMode) {
        return new CashRounding(unit, roundingMode);
    }

    public @NonNull BigDecimal round(@NonNull BigDecimal value) {
        var mathContext = new MathContext(value.precision(), roundingMode);
        var factor = unitToFactor(unit);


        var result = value
            .divide(factor, mathContext)
            .setScale(unit.scale(), mathContext.getRoundingMode())
            .multiply(factor)
            .setScale(unit.scale(), mathContext.getRoundingMode());

        return result;
    }

    private static BigDecimal unitToFactor(@NonNull BigDecimal unit) {
        BigDecimal result = unit.multiply(new BigDecimal("100"))
            .setScale(unit.scale() - 2, RoundingMode.UNNECESSARY);

        return result;
    }

}
