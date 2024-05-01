package com.github.honoluluhenk.fluentbigdecimals.scaler;

import com.github.honoluluhenk.fluentbigdecimals.CashRounding;
import com.github.honoluluhenk.fluentbigdecimals.CashRoundingUnits;
import lombok.*;
import org.checkerframework.checker.nullness.qual.NonNull;

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

    private final @NonNull CashRounding cashRounding;

    public static CashRoundingScaler of(@NonNull CashRoundingUnits unit) {
        return of(unit.getUnit(), CASH_ROUNDING_MODE);
    }

    public static CashRoundingScaler of(@NonNull BigDecimal unit, @NonNull RoundingMode roundingMode) {
        return new CashRoundingScaler(new CashRounding(unit, roundingMode));
    }

    @Override
    public @NonNull BigDecimal scale(@NonNull BigDecimal value, @NonNull MathContext mathContext) {
        var result = cashRounding.round(value);

        return result;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", CashRoundingScaler.class.getSimpleName(), cashRounding);
    }
}
