package com.github.honoluluhenk.fluentbigdecimals.scaler;

import com.github.honoluluhenk.fluentbigdecimals.CashRounding;
import com.github.honoluluhenk.fluentbigdecimals.CashRoundingUnits;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.github.honoluluhenk.fluentbigdecimals.CashRounding.CASH_ROUNDING_MODE;

@AllArgsConstructor
@Getter
@With
@EqualsAndHashCode(callSuper = false)
public class CashRoundingScaler implements Scaler {
    private static final long serialVersionUID = 3757986364820547766L;

    private final CashRounding cashRounding;

    public static CashRoundingScaler of(CashRoundingUnits unit) {
        return of(unit.getUnit(), CASH_ROUNDING_MODE);
    }

    public static CashRoundingScaler of(BigDecimal unit, RoundingMode roundingMode) {
        return new CashRoundingScaler(CashRounding.of(unit, roundingMode));
    }

    @Override
    public BigDecimal scale(BigDecimal value, MathContext mathContext) {
        return cashRounding.round(value);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", CashRoundingScaler.class.getSimpleName(), cashRounding);
    }
}
