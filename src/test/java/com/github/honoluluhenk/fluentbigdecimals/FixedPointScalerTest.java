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

    public static final FixedPointScaler FIXTURE = from(2);
    private static final MathContext MATH_CONTEXT = new MathContext(10, HALF_UP);

    @Nested
    class From {
        @Test
        void passes_values_to_getters() {
            FixedPointScaler scaler = from(7);

            assertThat(scaler.getMaxScale())
                .isEqualTo(7);
        }
    }

    @Test
    void copy_factory_produces_same_properties() {
        FixedPointScaler scaler = from(7);

        FixedPointScaler copy = from(scaler);

        assertThat(copy.getMaxScale())
            .isEqualTo(7);
    }

    @Nested
    class WithMaxScale {
        private final FixedPointScaler actual = FIXTURE.withMaxScale(3);

        @Test
        void creates_new_instance() {
            assertThat(actual)
                .isNotSameAs(FIXTURE);
        }

        @Test
        void updates_only_maxScale() {
            assertThat(actual.getMaxScale())
                .isEqualTo(3);
        }
    }

    @Nested
    class HashCodeEquals {

        @Test
        void equals_for_same_values() {
            FixedPointScaler b = from(2);

            assertThat(FIXTURE)
                .isEqualTo(b);

            assertThat(FIXTURE.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_scale() {
            FixedPointScaler b = from(2);

            assertThat(FIXTURE)
                .isNotEqualTo(b);

            assertThat(FIXTURE.hashCode())
                .isNotEqualTo(b.hashCode());
        }
    }

    @Nested
    class ToString {
        @Test
        void includes_all_params() {
            var actual = FIXTURE.toString();

            assertThat(actual)
                .isEqualTo(FixedPointScaler.class.getSimpleName() + "[5,2,HALF_UP]");
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
            BigDecimal actual = FIXTURE.scale(input, MATH_CONTEXT);

            assertThat(actual)
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
            BigDecimal actual = FIXTURE.scale(input, MATH_CONTEXT);

            assertThat(actual)
                .isEqualTo(expected);
        }

//        @Test
//        void rounds_to_smaller_scale() {
//            FixedPointScaler smallScale = from(1);
//            BigDecimal value = new BigDecimal("123.45");
//            MathContext mathContext = new MathContext(4, HALF_UP);
//
//            BigDecimal actual = FIXTURE.apply(identity(), input, ignored);
//
//            assertThat(actual)
//                .isEqualByComparingTo("123.5");
//        }

//        @ParameterizedTest
//        @CsvSource({
//            "123.456,  123.46",
//            "123.4567,  123.46",
//        })
//        void reducing_scale_rounds_off_digits(
//            BigDecimal input,
//            BigDecimal expectedValue
//        ) {
//            var scaler = FixedPointScaler.from(2);
//
//            var actual = scaler.scale(input, new MathContext(5, HALF_UP));
//
//            assertThat(actual)
//                .isEqualByComparingTo(expectedValue);
//        }

        @ParameterizedTest
        @CsvSource({
            "123",
            "123.4",
            "123.45",
        })
        void expanding_scale_does_nothing_and_returns_same_instance(
            BigDecimal input
        ) {
            BigDecimal actual = FIXTURE.scale(input, MATH_CONTEXT);

            assertThat(actual)
                .isSameAs(input);
        }


        // FIXME: moar edge cases?
        @ParameterizedTest
        @CsvSource({
            "99999",
            "9999.9",
        })
        void throws_on_values_exceeding_remaining(BigDecimal input) {

            assertThrows(
                ArithmeticException.class,
                () -> FIXTURE.scale(input, MATH_CONTEXT)
            );
        }

    }
}
