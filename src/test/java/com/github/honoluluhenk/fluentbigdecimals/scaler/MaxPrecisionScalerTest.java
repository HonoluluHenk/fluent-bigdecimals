package com.github.honoluluhenk.fluentbigdecimals.scaler;

import com.github.honoluluhenk.fluentbigdecimals.FluentBigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

class MaxPrecisionScalerTest {
    public static final BigDecimal FIXTURE_VALUE = new BigDecimal("123.45");
    public static final MathContext FIXTURE_MATH_CONTEXT = new MathContext(4, RoundingMode.HALF_UP);
    public static final MaxPrecisionScaler SCALER = new MaxPrecisionScaler();

    @Nested
    class Scale {

        @Test
        void rounds_to_precision() {
            var actual = new FluentBigDecimal(FIXTURE_VALUE, FIXTURE_MATH_CONTEXT, SCALER)
                .round();

            assertThat(actual.getValue().toPlainString())
                .isEqualTo("123.5");
        }
    }

    @Nested
    class ToString {

        @Test
        void contains_class_name() {
            assertThat(SCALER.toString())
                .isEqualTo(MaxPrecisionScaler.class.getSimpleName());
        }
    }


}
