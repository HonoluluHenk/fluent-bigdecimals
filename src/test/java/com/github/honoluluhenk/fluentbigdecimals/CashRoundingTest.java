package com.github.honoluluhenk.fluentbigdecimals;

import lombok.var;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CashRoundingTest {
    private final CashRounding cashRounding = CashRounding.of(CashRoundingUnits.ROUND_DOT05);

    @Nested
    class Round {

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
            "6.000, 6.00",
            "6.001, 6.00",
            "6.004, 6.00",
            "6.005, 6.00",
            "6.006, 6.00",
            "6.007, 6.00",
            "6.008, 6.00",
            "6.009, 6.00",
            "6.0 , 6.00",
            "6   , 6.00",
        })
        void rounds_on_more_or_less_precission(BigDecimal input, BigDecimal expected) {
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

    @Nested
    class ToString {
        @Test
        void has_nice_message() {
            var actual = cashRounding.toString();

            assertThat(actual)
                .contains(CashRounding.class.getSimpleName())
                .contains("0.05")
                .contains("HALF_UP");
        }
    }

}
