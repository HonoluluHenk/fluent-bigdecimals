package com.github.honoluluhenk.fluentbigdecimals.tests;

import com.github.honoluluhenk.fluentbigdecimals.CashRounding;
import com.github.honoluluhenk.fluentbigdecimals.CashRoundingUnits;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UP;
import static org.assertj.core.api.Assertions.assertThat;

class CashRoundingTest {
    private final CashRounding cashRounding = CashRounding.of(CashRoundingUnits.ROUND_DOT05);

    @Nested
    class Factories {
        @Test
        void of_unit_defaults_to_HALF_UP() {
            var actual = CashRounding.of(new BigDecimal("0.05"));

            assertThat(actual.getUnit())
                .isEqualTo(new BigDecimal("0.05"));
            assertThat(actual.getRoundingMode())
                .isEqualTo(HALF_UP);
        }

        @Test
        void of_units_defaults_to_HALF_UP() {
            var actual = CashRounding.of(CashRoundingUnits.ROUND_DOT25);

            assertThat(actual.getUnit())
                .isEqualTo(CashRoundingUnits.ROUND_DOT25.getUnit());
            assertThat(actual.getRoundingMode())
                .isEqualTo(HALF_UP);
        }

        @Test
        void of_units_honours_given_roundingMode() {
            var actual = CashRounding.of(CashRoundingUnits.ROUND_DOT05, DOWN);

            assertThat(actual.getUnit())
                .isEqualTo(CashRoundingUnits.ROUND_DOT05.getUnit());
            assertThat(actual.getRoundingMode())
                .isEqualTo(DOWN);
        }

        @Test
        void of_unit_and_roundingMode_honours_given_roundingMode() {
            var actual = CashRounding.of(new BigDecimal("0.10"), DOWN);

            assertThat(actual.getUnit())
                .isEqualTo(new BigDecimal("0.10"));
            assertThat(actual.getRoundingMode())
                .isEqualTo(DOWN);
        }
    }

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

        @ParameterizedTest
        @CsvSource({
            "ROUND_DOT05, 0    , 0.00",
            "ROUND_DOT05, 0.07 , 0.05",
            "ROUND_DOT05, 0.12 , 0.10",
            "ROUND_DOT05, 0.13 , 0.15",
            "ROUND_DOT05, 12.4 , 12.40",
            "ROUND_DOT05, -0.13, -0.15",
            "ROUND_DOT05, 3.000, 3.00",
            "ROUND_DOT10, 0.07 , 0.10",
            "ROUND_DOT10, 0.12 , 0.10",
            "ROUND_DOT10, 0.13 , 0.10",
            "ROUND_DOT10, 12.5 , 12.50",
            "ROUND_DOT10, -0.13, -0.10",
            "ROUND_DOT10, 3.000, 3.00",
            "ROUND_DOT25, 0.07 , 0.00",
            "ROUND_DOT25, 0.13 , 0.25",
            "ROUND_DOT25, 12.4 , 12.50",
            "ROUND_DOT25, 12.5 , 12.50",
            "ROUND_DOT25, -0.13, -0.25",
            "ROUND_DOT25, 3.000, 3.00",
        })
        void rounds_to_the_nearest_unit(CashRoundingUnits unit, BigDecimal input, BigDecimal expected) {
            var actual = CashRounding.of(unit)
                .round(input);

            assertThat(actual)
                .isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "ROUND_DOT05, 0.07 , 0.05",
            "ROUND_DOT05, 0.13 , 0.10",
            "ROUND_DOT05, -0.13, -0.10",
            "ROUND_DOT10, 0.07 , 0.00",
            "ROUND_DOT10, 0.19 , 0.10",
            "ROUND_DOT25, 0.13 , 0.00",
            "ROUND_DOT25, 12.4 , 12.25",
            "ROUND_DOT25, -0.13, 0.00",
        })
        void rounds_down_if_told_so(CashRoundingUnits unit, BigDecimal input, BigDecimal expected) {
            var actual = CashRounding.of(unit, DOWN)
                .round(input);

            assertThat(actual)
                .isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
            "ROUND_DOT05, 0.07 , 0.10",
            "ROUND_DOT05, 0.12 , 0.15",
            "ROUND_DOT05, -0.13, -0.15",
            "ROUND_DOT10, 0.07 , 0.10",
            "ROUND_DOT10, 0.12 , 0.20",
            "ROUND_DOT25, 0.07 , 0.25",
            "ROUND_DOT25, 12.4 , 12.50",
            "ROUND_DOT25, -0.13, -0.25",
        })
        void rounds_up_if_told_so(CashRoundingUnits unit, BigDecimal input, BigDecimal expected) {
            var actual = CashRounding.of(unit, UP)
                .round(input);

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
                .isEqualTo("CashRounding(0.05, HALF_UP)");
        }
    }

}
