package com.github.honoluluhenk.fluentbigdecimals;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.honoluluhenk.fluentbigdecimals.BigDecimalExtAssert.assertThat;
import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(generator = DisplayNameGenerator.ReplaceUnderscores.class)
// FIXME: create checker annotations for AssertJ
@SuppressWarnings({"nullable", "argument.type.incompatible"})
class BigDecimalExtTest {

    private static final BigDecimal FIXTURE_VALUE = new BigDecimal("123.45");
    private static final FixedPrecisionAdjuster FIXTURE_CONTEXT = FixedPrecisionAdjuster.from(5, 2, HALF_UP);
    private static final BigDecimalExt FIXTURE = FIXTURE_CONTEXT.withValue(FIXTURE_VALUE);

    static BigDecimalExt bde(String bigDecimal) {
        BigDecimal bd = new BigDecimal(bigDecimal);
        return FixedPrecisionAdjuster.from(bd, HALF_UP)
            .withValue(bigDecimal);
    }

    @Nested
    class TestSetup {
        /**
         * just in case you cannot remember how precision/scale looks like
         */
        @Test
        void is_as_expected() {
            Assertions.assertThat(FIXTURE_VALUE.precision())
                .isEqualTo(5);

            Assertions.assertThat(FIXTURE_VALUE.scale())
                .isEqualTo(2);

            Assertions.assertThat(FIXTURE_VALUE.intValue())
                .isEqualTo(123);

            BigDecimal remainder = FIXTURE_VALUE.remainder(BigDecimal.ONE);
            Assertions.assertThat(remainder.toPlainString())
                .isEqualTo("0.45");
        }
    }

    @Nested
    class Constructor {

        @Test
        void calls_roundTo() {
            AtomicReference<BigDecimal> ref = new AtomicReference<>();

            class Recorder implements Adjuster {
                private static final long serialVersionUID = -1999257916541686047L;

                @Override
                public BigDecimal adjust(BigDecimal value) {
                    ref.set(value);
                    return value;
                }
            }

            new BigDecimalExt(FIXTURE_VALUE, new Recorder());

            Assertions.assertThat(ref.get())
                .isSameAs(FIXTURE_VALUE);

        }

        @Test
        void sets_fields() {
            Assertions.assertThat(FIXTURE.getValue())
                .isSameAs(FIXTURE_VALUE);
            Assertions.assertThat(FIXTURE.getAdjuster())
                .isSameAs(FIXTURE_CONTEXT);
        }

        @Test
        void throws_for_null_value() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new BigDecimalExt(null, FIXTURE_CONTEXT)
            );

            Assertions.assertThat(ex)
                .hasMessageContaining("value");
        }

        @Test
        void throws_for_null_adjuster() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new BigDecimalExt(BigDecimal.ONE, null)
            );

            Assertions.assertThat(ex)
                .hasMessageContaining("adjuster");
        }
    }

    @Nested
    class HashCodeEquals {
        @Test
        void equals_for_same_adjuster_and_value() {
            BigDecimalExt a = FIXTURE_CONTEXT.withValue(BigDecimal.valueOf(123));
            BigDecimalExt b = FIXTURE_CONTEXT.withValue(BigDecimal.valueOf(123));

            assertThat(a)
                .isEqualTo(b);

            Assertions.assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_for_same_adjuster_and_value_with_differing_precision() {
            BigDecimalExt a = FIXTURE_CONTEXT.withValue(new BigDecimal("123"));
            BigDecimalExt b = FIXTURE_CONTEXT.withValue(new BigDecimal("123.0"));

            assertThat(a)
                .isNotEqualTo(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_adjusters() {
            BigDecimalExt a = FixedPrecisionAdjuster.from(10, 7).withValue(BigDecimal.valueOf(123));
            BigDecimalExt b = FixedPrecisionAdjuster.from(10, 5).withValue(BigDecimal.valueOf(123));

            assertThat(a)
                .isNotEqualTo(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }
    }

    @Nested
    class EqualsComparingValue {
        @Test
        void equals_for_same_adjuster_and_value() {
            BigDecimalExt a = FIXTURE_CONTEXT.withValue(BigDecimal.valueOf(123));
            BigDecimalExt b = FIXTURE_CONTEXT.withValue(BigDecimal.valueOf(123));

            assertThat(a)
                .isEqualComparingValue(b);

            Assertions.assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void equals_for_same_adjuster_and_value_with_differing_precision() {
            BigDecimalExt a = FIXTURE_CONTEXT.withValue(new BigDecimal("123"));
            BigDecimalExt b = FIXTURE_CONTEXT.withValue(new BigDecimal("123.0"));

            assertThat(a)
                .isEqualComparingValue(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_adjusters() {
            BigDecimalExt a = FixedPrecisionAdjuster.from(10, 7).withValue(BigDecimal.valueOf(123));
            BigDecimalExt b = FixedPrecisionAdjuster.from(10, 5).withValue(BigDecimal.valueOf(123));

            assertThat(a)
                .isNotEqualComparingValue(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }
    }

    @Nested
    class RoundToTest {

        @Test
        void does_nothing_for_same_adjuster() {
            BigDecimalExt actual = FIXTURE.roundTo(FIXTURE_CONTEXT);

            assertThat(actual)
                .isEqualTo(FIXTURE);
        }

        @Test
        void rounds_to_smaller_scale() {
            FixedPrecisionAdjuster smallScale = FIXTURE_CONTEXT.withMaxScale(1);

            BigDecimalExt actual = FIXTURE.roundTo(smallScale);

            assertThat(actual)
//                .hasPrecision(FIXTURE_CONTEXT.getPrecision())
//                .hasRoundingMode(FIXTURE_CONTEXT.getRoundingMode())
                .hasValue("123.5");
        }

        @ParameterizedTest
        @CsvSource({
            "3, 0, HALF_UP, 123, 3, 0",
            "3, 1, HALF_UP, 123, 3, 0",
            "2, 1, HALF_UP, 120, 2, -1",
            "1, 0, HALF_UP, 100, 1, -2",
        })
        void truncating_precision_chops_off_digits(
            int precision,
            int scale,
            RoundingMode roundingMode,
            String expectedValue,
            int expectedPrecision,
            int expectedScale
        ) {
            BigDecimalExt sut = FIXTURE.roundTo(new FixedPrecisionAdjuster(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameAdjusterAs(sut)
            ;
        }

        @ParameterizedTest
        @CsvSource("5, 2, HALF_UP, 123.45, 5, 2")
        void truncating_to_same_values_keeps_same_data(
            int precision,
            int scale,
            RoundingMode roundingMode,
            String expectedValue,
            int expectedPrecision,
            int expectedScale
        ) {
            BigDecimalExt sut = FIXTURE.roundTo(new FixedPrecisionAdjuster(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameAdjusterAs(sut)
            ;
        }

        @ParameterizedTest
        @CsvSource({
            "11, 7, HALF_UP, 123.45, 5, 2",
            "10, 8, HALF_UP, 123.45, 5, 2",
            "11, 8, HALF_UP, 123.45, 5, 2",
        })
        void expanding_scale_and_precision_is_limited_by_actual_value(
            int precision,
            int scale,
            RoundingMode roundingMode,
            String expectedValue,
            int expectedPrecision,
            int expectedScale
        ) {
            BigDecimalExt sut = FIXTURE.roundTo(new FixedPrecisionAdjuster(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameAdjusterAs(sut)
            ;
        }

        @ParameterizedTest
        @CsvSource({
            "5, 1, HALF_UP, 123.5, 4, 1",
            "5, 1, DOWN, 123.4, 4, 1",
        })
        void truncating_scale_rounds_according_to_parameter(
            int precision,
            int scale,
            RoundingMode roundingMode,
            String expectedValue,
            int expectedPrecision,
            int expectedScale
        ) {
            BigDecimalExt sut = FIXTURE.roundTo(new FixedPrecisionAdjuster(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameAdjusterAs(sut)
            ;
        }

        @ParameterizedTest
        @CsvSource({
            "4, 1, HALF_UP, 123.5, 4, 1",
            "4, 1, DOWN, 123.4, 4, 1",
        })
        void truncating_both_scale_and_precision_rounds_according_to_RoundingMode(
            int precision,
            int scale,
            RoundingMode roundingMode,
            String expectedValue,
            int expectedPrecision,
            int expectedScale
        ) {
            BigDecimalExt sut = FIXTURE.roundTo(new FixedPrecisionAdjuster(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameAdjusterAs(sut)
            ;
        }

    }

    @Nested
    class ToString {
        @Test
        void includes_all_parameters() {
            String actual = FIXTURE.toString();

            Assertions.assertThat(actual)
                .isEqualTo("BigDecimalExt[123.45, FixedPrecisionAdjuster[5,2,HALF_UP]]");
        }
    }


//    @Nested
//    class Add {
//        @Test
//        void keeps_same_adjuster_for_same_input_params() {
//            BigDecimalExt actual = FIXTURE.add(FIXTURE);
//
//            assertThat(actual)
//                .hasValueMatchingAdjuster("246.90", FIXTURE_CONTEXT);
//        }
//
//        @ParameterizedTest
//        @CsvSource({
//            "9999.99, 10123",
//            "9.99999, 133.45",
//        })
//        void adds_and_keeps_adjuster_when_adding_value_with_larger_precision(BigDecimal augend, String expectedValue) {
//            BigDecimalExt actual = FIXTURE.add(augend);
//
//            assertThat(actual)
//                .hasValueMatchingAdjuster(expectedValue, FIXTURE_CONTEXT);
//        }
//
//        @Test
//        void adds_and_keeps_adjuster_when_adding_value_with_larger_scale() {
//            BigDecimalExt actual = FIXTURE.add("99.999");
//
//            assertThat(actual)
//                .hasValueMatchingAdjuster("223.45", FIXTURE_CONTEXT);
//        }
//
//        @Test
//        void adds_and_keeps_adjuster_when_adding_value_with_smaller_precision() {
//            BigDecimalExt actual = FIXTURE.add("12");
//
//            assertThat(actual)
//                .hasValueMatchingAdjuster("135.45", FIXTURE_CONTEXT);
//        }
//
//        @Test
//        void adds_and_keeps_adjuster_when_adding_value_with_smaller_scale() {
//            BigDecimalExt actual = FIXTURE.add("99");
//
//            assertThat(actual)
//                .hasValueMatchingAdjuster("222.45", FIXTURE_CONTEXT);
//        }
//
//        @ParameterizedTest
//        @CsvSource({
//            " 99, 2, 0, 1, 2, 0, 100, 2, 0",
//            "100, 2, 0, 1, 2, 0, 100, 2, 0",
//        })
//        void edge_cases(
//            String firstValue,
//            int firstPrecision,
//            int firstScale,
//            String secondValue,
//            int secondPrecision,
//            int secondScale,
//            String expectedValue,
//            int expectedPrecision,
//            int expectedScale
//        ) {
//            BigDecimalExt first = FixedPrecisionAdjuster.from(firstPrecision, firstScale).withValue(firstValue);
//            BigDecimalExt second = FixedPrecisionAdjuster.from(secondPrecision, secondScale).withValue(secondValue);
//            BigDecimalExt expected = FixedPrecisionAdjuster.from(expectedPrecision, expectedScale).withValue(expectedValue);
//
//            BigDecimalExt actual = first.add(second);
//
//            assertThat(actual)
//                .isEqualTo(expected);
//        }
//    }

//    @Nested
//    class AddAll {
//        @Test
//        void adds_all() {
//            BigDecimalExt actual = FIXTURE.addAll(
//                bde("123.45"),
//                null,
//                bde("54.321")
//            );
//
//            assertThat(actual)
//                .hasValueMatchingAdjuster("301.22", FIXTURE_CONTEXT);
//        }
//
//        @Test
//        void rounds_to_original_adjuster_after_each_step() {
//            BigDecimalExt actual = FIXTURE
//                .addAll(
//                    bde("0.006"),  // = 123.456 => rounded: 123.46
//                    bde("50000") // = 50123.46 => rounded: 50123
//                );
//
//            assertThat(actual)
//                .hasValueMatchingAdjuster("50123", FIXTURE_CONTEXT);
//        }
//    }
}
