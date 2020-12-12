package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.NonNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;

class FixedScaleScalerTest {

    private final MathContext MATH_CONTEXT = new MathContext(10, HALF_UP);

    @Nested
    class Scale {

        @ParameterizedTest
        @CsvSource({
            "123,     123.00",
            "123.0,   123.00",
            "123.00,  123.00",
            "123.000, 123.00",
            "123.989, 123.99",
            "123.999, 124.00",
        })
        void always_scales_to_fixed_scale(@NonNull BigDecimal input, BigDecimal expected) {

            BigDecimal scaled = new FixedScaleScaler(2).scale(input, MATH_CONTEXT);
            assertThat(scaled)
                .isEqualTo(expected);
        }
    }

    @Nested
    class ToSTring {

        @Test
        void includes_all_values() {
            assertThat(new FixedScaleScaler(12).toString())
                .isEqualTo("FixedScaleScaler[12]");
        }
    }


}
