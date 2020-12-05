package com.github.honoluluhenk.fluentbigdecimals.scaler;

import com.github.honoluluhenk.fluentbigdecimals.FluentBigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerScalerTest {

    public static final BigDecimal FIXTURE_VALUE = new BigDecimal("123.45");
    public static final MathContext FIXTURE_MATH_CONTEXT = new MathContext(5, RoundingMode.HALF_UP);
    public static final IntegerScaler SCALER = new IntegerScaler();

    @Nested
    class Scale {

        @Test
        void rounds_to_integer() {
            var actual = new FluentBigDecimal(FIXTURE_VALUE, FIXTURE_MATH_CONTEXT, SCALER)
                .round();

            assertThat(actual.getValue().toPlainString())
                .isEqualTo("123");
        }
    }

    @Nested
    class ToString {

        @Test
        void contains_class_name() {
            assertThat(SCALER.toString())
                .isEqualTo(IntegerScaler.class.getSimpleName());
        }
    }


}