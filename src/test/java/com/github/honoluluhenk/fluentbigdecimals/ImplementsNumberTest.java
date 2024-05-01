package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import lombok.var;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ImplementsNumberTest {
    public static class NumericFluentBigDecimal extends AbstractFluentBigDecimal<NumericFluentBigDecimal> {
        private static final long serialVersionUID = -2810595855995466398L;

        protected NumericFluentBigDecimal(@NonNull BigDecimal value, @NonNull Configuration<NumericFluentBigDecimal> configuration) {
            super(value, configuration);
        }
    }

    private static final Configuration<NumericFluentBigDecimal> CONFIG = ConfigurationFactory
        .create(10, HALF_UP, new NopScaler())
        .withFactory(NumericFluentBigDecimal::new);

    @Nested
    class NumberImplemented {

        @Test
        void base_class_implements_all_Number_methods_and_thus_does_not_need_own_methods() {
            var allMethods = NumericFluentBigDecimal.class.getDeclaredMethods();
            var actualMethods = Arrays.stream(allMethods)
                .filter(m -> !m.getName().startsWith("$jacoco"))
                .toArray();

            assertThat(actualMethods)
                .isEmpty();
        }
    }


    @Nested
    class IntValue {

        @Test
        void returns_same_as_bigdecimal() {
            var input = new BigDecimal("42.23");

            var actual = CONFIG.of(input);

            assertThat(actual.intValue())
                .isEqualTo(input.intValue());
        }
    }

    @Nested
    class LongValue {

        @Test
        void returns_same_as_bigdecimal() {
            var input = new BigDecimal("42.23");

            var actual = CONFIG.of(input);

            assertThat(actual.longValue())
                .isEqualTo(input.longValue());
        }
    }

    @Nested
    class FloatValue {

        @Test
        void returns_same_as_bigdecimal() {
            var input = new BigDecimal("42.23");

            var actual = CONFIG.of(input);

            assertThat(actual.floatValue())
                .isEqualTo(input.floatValue());
        }
    }

    @Nested
    class DoubleValue {

        @Test
        void returns_same_as_bigdecimal() {
            var input = new BigDecimal("42.23");

            var actual = CONFIG.of(input);

            assertThat(actual.doubleValue())
                .isEqualTo(input.doubleValue());
        }
    }

}
