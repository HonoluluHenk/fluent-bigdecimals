package com.github.honoluluhenk.fluentbigdecimals.scaler;

import com.github.honoluluhenk.fluentbigdecimals.Configuration;
import com.github.honoluluhenk.fluentbigdecimals.ConfigurationFactory;
import com.github.honoluluhenk.fluentbigdecimals.FluentBigDecimal;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

class MaxPrecisionScalerTest {
    public static final Configuration<FluentBigDecimal> CONFIG = ConfigurationFactory
        .create(4, RoundingMode.HALF_UP, new MaxPrecisionScaler());
    public static final BigDecimal FIXTURE_VALUE = new BigDecimal("123.45");

    @Nested
    class Scale {

        @Test
        void rounds_to_precision() {
            var actual = CONFIG.of(FIXTURE_VALUE)
                .round();

            assertThat(actual.getValue().toPlainString())
                .isEqualTo("123.5");
        }
    }

    @Nested
    class ToString {

        @Test
        void contains_class_name() {
            assertThat(new MaxPrecisionScaler().toString())
                .isEqualTo(MaxPrecisionScaler.class.getSimpleName());
        }
    }


}
