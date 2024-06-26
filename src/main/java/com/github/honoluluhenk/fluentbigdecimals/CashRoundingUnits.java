package com.github.honoluluhenk.fluentbigdecimals;


import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.math.BigDecimal;

@Getter
public enum CashRoundingUnits {
    ROUND_DOT05("0.05"),
    ROUND_DOT10("0.10"),
    ROUND_DOT25("0.25"),
    ROUND_1("1"),
    ROUND_5("5");

    private final @NonNull BigDecimal unit;

    CashRoundingUnits(@NonNull String unit) {
        this.unit = new BigDecimal(unit);
    }
}
