package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.FixedPointScaler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.github.honoluluhenk.fluentbigdecimals.scaler.FixedPointScaler.from;
import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(generator = DisplayNameGenerator.ReplaceUnderscores.class)
// we want to test runtime null-checks:
class FixedPointScalerTest {

    @Nested
    class From {
        @Test
        void passes_values_to_getters() {
            FixedPointScaler scaler = from(7);

            assertValues(scaler, 7);
        }
    }

    @Test
    void copy_factory_produces_same_properties() {
        FixedPointScaler scaler = from(2);

        FixedPointScaler actual = from(scaler);
        assertValues(actual, 2);
    }

    @Nested
    class WithMaxScale {
        private final FixedPointScaler scaler = from(2);
        private final FixedPointScaler actual = scaler.withMaxScale(3);

        @Test
        void creates_new_instance() {
            assertThat(actual)
                .isNotSameAs(scaler);
        }

        @Test
        void updates_only_maxScale() {
            assertValues(actual, 3);
        }
    }

    static void assertValues(FixedPointScaler scaler, int maxScale) {
        assertThat(scaler.getMaxScale())
            .describedAs("maxScale")
            .isEqualTo(maxScale);
    }

    @Nested
    class HashCodeEquals {

        @Test
        void equals_for_same_values() {
            FixedPointScaler a = from(1);
            FixedPointScaler b = from(1);

            assertThat(a)
                .isEqualTo(b);

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_scale() {
            FixedPointScaler a = from(1);
            FixedPointScaler b = from(2);

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }
    }

    @Nested
    class ToString {
        @Test
        void includes_all_params() {
            var actual = from(1).toString();

            assertThat(actual)
                .isEqualTo(FixedPointScaler.class.getSimpleName() + "[1]");
        }
    }

    @Nested
    class AdjustInto {

        @ParameterizedTest
        @CsvSource({
            "0",
            "0.00",
            "123.45",
            "-123.45",
            "999.99",
            "-999.99",
            "0.99",
            "-0.99",
        })
        void returns_same_instance_if_input_is_within_bounds(BigDecimal input) {
            FixedPointScaler adjuster = from(2);
            BigDecimal outcome = adjuster.scale(input, new MathContext(5, HALF_UP));

            assertThat(outcome)
                .isSameAs(input);
        }

        @ParameterizedTest
        @CsvSource({
            "12.345, 12.35",
            "0.0000, 0.00",
            "0.99999, 1.00",
            "99999E-3, 100.00",
        })
        void reduces_scale_if_needed_using_rounding(BigDecimal input, BigDecimal expected) {
            FixedPointScaler adjuster = from(2);
            MathContext mathContext = new MathContext(5, HALF_UP);

            BigDecimal outcome = adjuster.scale(input, mathContext);

            assertThat(outcome)
                .isEqualTo(expected);
        }

        @Test
        void rounds_to_smaller_scale() {
            FixedPointScaler smallScale = from(1);
            BigDecimal value = new BigDecimal("123.45");
            MathContext mathContext = new MathContext(4, HALF_UP);

            BigDecimal actual = smallScale.scale(value, mathContext);

            assertThat(actual)
                .isEqualByComparingTo("123.5");
        }

        @ParameterizedTest
        @CsvSource({
            "123.456,  123.46",
            "123.4567,  123.46",
        })
        void reducing_scale_rounds_off_digits(
            BigDecimal input,
            BigDecimal expectedValue
        ) {
            var scaler = FixedPointScaler.from(2);

            var actual = scaler.scale(input, new MathContext(5, HALF_UP));

            assertThat(actual)
                .isEqualByComparingTo(expectedValue);
        }

        @ParameterizedTest
        @CsvSource({
            "123",
            "123.4",
            "123.45",
        })
        void expanding_scale_does_nothing_and_returns_same_instance(
            BigDecimal input
        ) {
            var scaler = FixedPointScaler.from(2);

            var actual = scaler.scale(input, new MathContext(5, HALF_UP));

            assertThat(actual)
                .isSameAs(input);
        }


        // FIXME: moar edge cases
        @Test
        void throws_on_values_exceeding_remaining() {
            int fullPrecision = 5;
            var input = new BigDecimal("99999"); // full precision is taken up by integers
            MathContext mathContext = new MathContext(fullPrecision, HALF_UP);

            FixedPointScaler scaler = FixedPointScaler.from(2);

            assertThrows(
                ArithmeticException.class,
                () -> scaler.scale(input, mathContext)
            );
        }

    }
}
