package com.github.honoluluhenk.fluentbigdecimals;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_UP;

@AllArgsConstructor
@Getter
@With
@ToString
public class CashRounding implements Serializable {
    private static final long serialVersionUID = 7429229149421339478L;
    public static final int CASH_ROUNDING_SCALE = 2;
    public static final RoundingMode CASH_ROUNDING_MODE = HALF_UP;

    private final BigDecimal unit;
    private final RoundingMode roundingMode;

    public static CashRounding of(BigDecimal unit) {
        return of(unit, CASH_ROUNDING_MODE);
    }

    public static CashRounding of(CashRoundingUnits unit) {
        return of(unit.getUnit(), CASH_ROUNDING_MODE);
    }

    public static CashRounding of(CashRoundingUnits unit, RoundingMode roundingMode) {
        return of(unit.getUnit(), roundingMode);
    }

    public static CashRounding of(BigDecimal unit, RoundingMode roundingMode) {
        return new CashRounding(unit, roundingMode);
    }

    public BigDecimal round(BigDecimal value) {
        var mathContext = new MathContext(value.precision(), roundingMode);
        var factor = unitToFactor(unit);

        return value
            .divide(factor, unit.scale(), mathContext.getRoundingMode())
            .setScale(unit.scale(), mathContext.getRoundingMode())
            .multiply(factor)
            .setScale(unit.scale(), mathContext.getRoundingMode());
    }

    private static BigDecimal unitToFactor(BigDecimal unit) {
        return unit.multiply(BigDecimal.valueOf(100))
            .setScale(unit.scale() - 2, RoundingMode.UNNECESSARY);
    }

}
