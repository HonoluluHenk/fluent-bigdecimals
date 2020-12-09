package com.github.honoluluhenk.fluentbigdecimals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CashRoundingTest {

    @Nested
    class Round {
        private final CashRounding cashRounding = CashRounding.of(CashRoundingUnits.ROUND_DOT05);

        @ParameterizedTest
        @CsvSource({
            "0.00, 0.00",
            "0.01, 0.00",

            "0.75, 0.75",
            "0.76, 0.75",
            "0.77, 0.75",
            "0.78, 0.80",
            "0.79, 0.80",
            "0.80, 0.80",
            "0.80, 0.80",
        })
        void rounds(BigDecimal input, BigDecimal expected) {
            var actual = cashRounding.round(input);

            assertThat(actual)
                .isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "1, 1.00",
            "1.0, 1.00",
            "1.00, 1.00",
            "1.000, 1.00",
            "1.0000, 1.00",
        })
        void keeps_fixed_scale(BigDecimal input, BigDecimal expected) {
            var actual = cashRounding.round(input);

            assertThat(actual)
                .isEqualTo(expected);
        }
    }


}
