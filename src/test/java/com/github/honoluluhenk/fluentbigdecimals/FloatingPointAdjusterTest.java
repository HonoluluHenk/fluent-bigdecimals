package com.github.honoluluhenk.fluentbigdecimals;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.github.honoluluhenk.fluentbigdecimals.FloatingPointAdjuster.from;
import static java.math.RoundingMode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
// we want to test runtime null-checks:
@SuppressWarnings("argument.type.incompatible")
class FloatingPointAdjusterTest {

    @Nested
    class From {
        @Test
        void passes_values_to_getters() {
            FloatingPointAdjuster ctx = from(10, 7, HALF_UP);

            assertValues(ctx, 10, 7, HALF_UP);

            assertThat(ctx.getMathContext())
                .isEqualTo(new MathContext(10, HALF_UP));
        }

        @ParameterizedTest
        @CsvSource({
            "0, but was: 0",
            "-99, but was: -99",
        })
        void throws_on_precision_lte_1(int precision, String expectedMessage) {
            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> from(precision, 2, HALF_UP)
            );

            assertThat(ex)
                .hasMessageContaining(expectedMessage);
        }

        @ParameterizedTest
        @CsvSource({
            "1, 1, 2",
            "1, 2, 3",
            "99, 99, 198", // FIXME: re-think: do we really want this behavior?
            "99, 100, 199",
        })
        void computes_precision_if_scale_gt_precision(int precision, int maxScale, int expectedPrecision) {
            FloatingPointAdjuster actual = from(precision, maxScale, HALF_UP);

            assertThat(actual.getPrecision())
                .isEqualTo(expectedPrecision);

            assertThat(actual.getMaxScale())
                .isEqualTo(maxScale);
        }

        @Test
        void throws_on_null_RoundingMode() {
            NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> from(3, 2, null)
            );

            assertThat(ex)
                .hasMessage("roundingMode required");
        }
    }

    @Nested
    class FromBigDecimal {
        @Test
        void throws_on_null_srcValue() {
            NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> from(null, RoundingMode.CEILING)
            );

            assertThat(ex)
                .hasMessage("srcValue required");
        }

        @Test
        void throws_on_null_RoundingMode() {
            NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> from(BigDecimal.ONE, null)
            );

            assertThat(ex)
                .hasMessage("roundingMode required");
        }

        @Test
        void passes_values_to_getters() {
            var actual = from(new BigDecimal("42.123"), FLOOR);

            assertThat(actual.getPrecision())
                .isEqualTo(5);

            assertThat(actual.getMaxScale())
                .isEqualTo(3);

            assertThat(actual.getRoundingMode())
                .isEqualTo(FLOOR);
        }

    }

//    @Nested
//    class FromWithDefault {
//        @Test
//        void sets_HALF_UP() {
//            FloatingPointAdjuster actual = FloatingPointAdjuster.from(12, 3, HALF_UP);
//
//            assertThat(actual.getRoundingMode())
//                .isEqualTo(HALF_UP);
//        }
//    }

    @Test
    void copy_factory_produces_same_properties() {
        FloatingPointAdjuster ctx = from(5, 2, DOWN);

        FloatingPointAdjuster actual = from(ctx);
        assertValues(actual, 5, 2, DOWN);
    }


    @Nested
    class WithPrecision {
        private final FloatingPointAdjuster ctx = from(5, 2, DOWN);
        private final FloatingPointAdjuster actual = ctx.withPrecision(8);

        @Test
        void updates_only_precision() {
            assertValues(actual, 8, 2, DOWN);
        }

        @Test
        void keeps_original_values_unchanged() {
            assertValues(ctx, 5, 2, DOWN);
        }

        @Test
        void creates_new_instance() {
            assertThat(actual)
                .isNotSameAs(ctx);
        }
    }

    @Nested
    class WithMaxScale {
        private final FloatingPointAdjuster ctx = from(5, 2, DOWN);
        private final FloatingPointAdjuster actual = ctx.withMaxScale(3);

        @Test
        void updates_only_maxScale() {
            assertValues(actual, 5, 3, DOWN);
        }

        @Test
        void keeps_original_values_unchanged() {
            assertValues(ctx, 5, 2, DOWN);
        }

        @Test
        void creates_new_instance() {
            assertThat(actual)
                .isNotSameAs(ctx);
        }
    }

    @Nested
    class WitRoundingMode {
        private final FloatingPointAdjuster ctx = from(5, 2, DOWN);
        private final FloatingPointAdjuster actual = ctx.withRoundingMode(HALF_UP);

        @Test
        void updates_only_maxScale() {
            assertValues(actual, 5, 2, HALF_UP);
        }

        @Test
        void keeps_original_values_unchanged() {
            assertValues(ctx, 5, 2, DOWN);
        }

        @Test
        void creates_new_instance() {
            assertThat(actual)
                .isNotSameAs(ctx);
        }
    }

    @Nested
    class WithValue {
        @Test
        void passes_Context_to_Ext_instance() {
            FloatingPointAdjuster ctx = from(10, 7, HALF_UP);

            BigDecimalExt actual = ctx.withValue(BigDecimal.ONE);

            assertThat(actual.getAdjuster())
                .isSameAs(ctx);
        }
    }

    static void assertValues(FloatingPointAdjuster ctx, int precision, int maxScale, RoundingMode roundingMode) {
        assertThat(ctx.getPrecision())
            .isEqualTo(precision);

        assertThat(ctx.getMaxScale())
            .isEqualTo(maxScale);

        assertThat(ctx.getRoundingMode())
            .isEqualTo(roundingMode);
    }

    @Nested
    class HashCodeEquals {

        @Test
        void equals_for_same_values() {
            FloatingPointAdjuster a = from(5, 1, HALF_UP);
            FloatingPointAdjuster b = from(5, 1, HALF_UP);

            assertThat(a)
                .isEqualTo(b);

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_precision() {
            FloatingPointAdjuster a = from(5, 1, HALF_UP);
            FloatingPointAdjuster b = from(9, 1, HALF_UP);

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_scale() {
            FloatingPointAdjuster a = from(5, 1, HALF_UP);
            FloatingPointAdjuster b = from(5, 2, HALF_UP);

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_rounding() {
            FloatingPointAdjuster a = from(5, 1, HALF_UP);
            FloatingPointAdjuster b = from(5, 1, DOWN);

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
            var actual = from(5, 1, HALF_UP).toString();

            assertThat(actual)
                .isEqualTo(FloatingPointAdjuster.class.getSimpleName() + "[5,1,HALF_UP]");
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
            "99999",
            "-99999",
            "0.99",
            "-0.99",
        })
        void keeps_value_unchanged_if_within_bounds(BigDecimal input) {
            FloatingPointAdjuster adjuster = from(5, 2, HALF_UP);
            BigDecimal outcome = adjuster.adjust(input);

            assertThat(outcome)
                .isEqualTo(input);
        }

        @ParameterizedTest
        @CsvSource({
            "12.345, 12.35",
            "0.0000, 0.00",
            "0.99999, 1.00",
            "999999, 1.0000E+6",
        })
        void adjust_value_if_out_of_bounds(BigDecimal input, BigDecimal expected) {
            FloatingPointAdjuster adjuster = from(5, 2, HALF_UP);
            BigDecimal outcome = adjuster.adjust(input);

            assertThat(outcome)
                .isEqualTo(expected);
        }


//        @Test
//        void rounds_to_smaller_scale() {
//            FloatingPointAdjuster smallScale = FIXTURE_ADJUSTER.withMaxScale(1);
//
//            BigDecimalExt actual = FIXTURE.roundTo(smallScale);
//
//            assertThat(actual)
////                .hasPrecision(FIXTURE_CONTEXT.getPrecision())
////                .hasRoundingMode(FIXTURE_CONTEXT.getRoundingMode())
//                .hasValue("123.5");
//        }

//        @ParameterizedTest
//        @CsvSource({
//            "3, 0, HALF_UP, 123, 3, 0",
//            "3, 1, HALF_UP, 123, 3, 0",
//            "2, 1, HALF_UP, 120, 2, -1",
//            "1, 0, HALF_UP, 100, 1, -2",
//        })
//        void truncating_precision_chops_off_digits(
//            int precision,
//            int scale,
//            RoundingMode roundingMode,
//            String expectedValue,
//            int expectedPrecision,
//            int expectedScale
//        ) {
//            BigDecimalExt sut = FIXTURE.roundTo(new FloatingPointAdjuster(precision, scale, roundingMode));
//
//            assertThat(sut)
//                .hasValue(expectedValue)
//                .hasValuePrecision(expectedPrecision)
//                .hasValueScale(expectedScale)
//                .hasSameAdjusterAs(sut)
//            ;
//        }
//
//        @ParameterizedTest
//        @CsvSource("5, 2, HALF_UP, 123.45, 5, 2")
//        void truncating_to_same_values_keeps_same_data(
//            int precision,
//            int scale,
//            RoundingMode roundingMode,
//            String expectedValue,
//            int expectedPrecision,
//            int expectedScale
//        ) {
//            BigDecimalExt sut = FIXTURE.roundTo(new FloatingPointAdjuster(precision, scale, roundingMode));
//
//            assertThat(sut)
//                .hasValue(expectedValue)
//                .hasValuePrecision(expectedPrecision)
//                .hasValueScale(expectedScale)
//                .hasSameAdjusterAs(sut)
//            ;
//        }
//
//        @ParameterizedTest
//        @CsvSource({
//            "11, 7, HALF_UP, 123.45, 5, 2",
//            "10, 8, HALF_UP, 123.45, 5, 2",
//            "11, 8, HALF_UP, 123.45, 5, 2",
//        })
//        void expanding_scale_and_precision_is_limited_by_actual_value(
//            int precision,
//            int scale,
//            RoundingMode roundingMode,
//            String expectedValue,
//            int expectedPrecision,
//            int expectedScale
//        ) {
//            BigDecimalExt sut = FIXTURE.roundTo(new FloatingPointAdjuster(precision, scale, roundingMode));
//
//            assertThat(sut)
//                .hasValue(expectedValue)
//                .hasValuePrecision(expectedPrecision)
//                .hasValueScale(expectedScale)
//                .hasSameAdjusterAs(sut)
//            ;
//        }
//
//        @ParameterizedTest
//        @CsvSource({
//            "5, 1, HALF_UP, 123.5, 4, 1",
//            "5, 1, DOWN, 123.4, 4, 1",
//        })
//        void truncating_scale_rounds_according_to_parameter(
//            int precision,
//            int scale,
//            RoundingMode roundingMode,
//            String expectedValue,
//            int expectedPrecision,
//            int expectedScale
//        ) {
//            BigDecimalExt sut = FIXTURE.roundTo(new FloatingPointAdjuster(precision, scale, roundingMode));
//
//            assertThat(sut)
//                .hasValue(expectedValue)
//                .hasValuePrecision(expectedPrecision)
//                .hasValueScale(expectedScale)
//                .hasSameAdjusterAs(sut)
//            ;
//        }
//
//        @ParameterizedTest
//        @CsvSource({
//            "4, 1, HALF_UP, 123.5, 4, 1",
//            "4, 1, DOWN, 123.4, 4, 1",
//        })
//        void truncating_both_scale_and_precision_rounds_according_to_RoundingMode(
//            int precision,
//            int scale,
//            RoundingMode roundingMode,
//            String expectedValue,
//            int expectedPrecision,
//            int expectedScale
//        ) {
//            BigDecimalExt sut = FIXTURE.roundTo(new FloatingPointAdjuster(precision, scale, roundingMode));
//
//            assertThat(sut)
//                .hasValue(expectedValue)
//                .hasValuePrecision(expectedPrecision)
//                .hasValueScale(expectedScale)
//                .hasSameAdjusterAs(sut)
//            ;
//        }

    }
}
