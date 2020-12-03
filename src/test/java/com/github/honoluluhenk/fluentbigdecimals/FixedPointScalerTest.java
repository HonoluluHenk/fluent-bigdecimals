package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.FixedPointScaler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.github.honoluluhenk.fluentbigdecimals.ProjectionFunction.identity;
import static com.github.honoluluhenk.fluentbigdecimals.scaler.FixedPointScaler.from;
import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(generator = DisplayNameGenerator.ReplaceUnderscores.class)
// we want to test runtime null-checks:
class FixedPointScalerTest {

    public static final FixedPointScaler FIXTURE = from(5, HALF_UP, 2);

    @Nested
    class From {
        @Test
        void passes_values_to_getters() {
            FixedPointScaler scaler = from(10, HALF_UP, 7);

            assertValues(scaler, 10, HALF_UP, 7);
        }
    }

    @Test
    void copy_factory_produces_same_properties() {
        FixedPointScaler scaler = from(10, HALF_UP, 2);

        FixedPointScaler actual = from(scaler);
        assertValues(actual, 10, HALF_UP, 2);
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
            assertValues(actual, FIXTURE.getMathContext(), 3);
        }
    }

    static void assertValues(FixedPointScaler scaler, int precision, RoundingMode roundingMode, int maxScale) {
        assertThat(scaler.getMathContext())
            .describedAs("mathContext")
            .isEqualTo(new MathContext(precision, roundingMode));

        assertThat(scaler.getMaxScale())
            .describedAs("maxScale")
            .isEqualTo(maxScale);
    }

    static void assertValues(FixedPointScaler scaler, MathContext mathContext, int maxScale) {
        assertThat(scaler.getMathContext())
            .describedAs("mathContext")
            .isEqualTo(mathContext);

        assertThat(scaler.getMaxScale())
            .describedAs("maxScale")
            .isEqualTo(maxScale);
    }

    @Nested
    class HashCodeEquals {

        @Test
        void equals_for_same_values() {
            FixedPointScaler b = from(5, HALF_UP, 2);

            assertThat(FIXTURE)
                .isEqualTo(b);

            assertThat(FIXTURE.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_scale() {
            FixedPointScaler b = from(2, HALF_UP, 2);

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
        private final BigDecimal ignored = BigDecimal.ZERO;

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
            BigDecimal actual = FIXTURE.apply(identity(), input, ignored);

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
            BigDecimal actual = FIXTURE.apply(identity(), input, ignored);

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
            BigDecimal actual = FIXTURE.apply(identity(), input, ignored);

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
                () -> FIXTURE.apply(identity(), input, ignored)
            );
        }

    }
}
