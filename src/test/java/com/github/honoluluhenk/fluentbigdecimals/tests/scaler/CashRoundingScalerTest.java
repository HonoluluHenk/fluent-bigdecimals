package com.github.honoluluhenk.fluentbigdecimals.tests.scaler;

import com.github.honoluluhenk.fluentbigdecimals.CashRounding;
import com.github.honoluluhenk.fluentbigdecimals.CashRoundingUnits;
import com.github.honoluluhenk.fluentbigdecimals.scaler.CashRoundingScaler;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class CashRoundingScalerTest {

    @Test
    void calls_calls_CashRounding() {
        CashRounding roundingSpy = spy(CashRounding.of(CashRoundingUnits.ROUND_DOT05));

        new CashRoundingScaler(roundingSpy)
            .scale(BigDecimal.ONE, new MathContext(10, RoundingMode.HALF_UP));

        verify(roundingSpy)
            .round(BigDecimal.ONE);

    }

    @Nested
    class Factories {
        @Test
        void of_units_uses_the_unit_and_defaults_to_HALF_UP() {
            var actual = CashRoundingScaler.of(CashRoundingUnits.ROUND_DOT05);

            assertThat(actual.getCashRounding().getUnit())
                .isEqualTo(CashRoundingUnits.ROUND_DOT05.getUnit());
            assertThat(actual.getCashRounding().getRoundingMode())
                .isEqualTo(HALF_UP);
        }

        @Test
        void of_unit_and_roundingMode_passes_both_on() {
            var actual = CashRoundingScaler.of(new BigDecimal("0.25"), DOWN);

            assertThat(actual.getCashRounding().getUnit())
                .isEqualTo(new BigDecimal("0.25"));
            assertThat(actual.getCashRounding().getRoundingMode())
                .isEqualTo(DOWN);
        }
    }

    @Nested
    class ToString {
        @Test
        void includes_all_params() {
            var actual = CashRoundingScaler.of(CashRoundingUnits.ROUND_DOT05)
                .toString();

            assertThat(actual)
                .isEqualTo("CashRoundingScaler[CashRounding(0.05, HALF_UP)]");
        }
    }

}
