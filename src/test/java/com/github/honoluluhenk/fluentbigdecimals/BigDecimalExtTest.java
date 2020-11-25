package com.github.honoluluhenk.fluentbigdecimals;

import org.assertj.core.api.Assertions;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.github.honoluluhenk.fluentbigdecimals.BigDecimalExtAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(generator = DisplayNameGenerator.ReplaceUnderscores.class)
// FIXME: create checker annotations for AssertJ
@SuppressWarnings({"nullable", "argument.type.incompatible"})
class BigDecimalExtTest {

    private static final BigDecimal ORIGINAL = new BigDecimal("123.4567890");
    private static final BigDecimalContext ORIGINAL_CONTEXT = BigDecimalContext.from(10, 7);
    private static final BigDecimalExt FIXTURE = ORIGINAL_CONTEXT.withValue(ORIGINAL);

    @Nested
    class TestSetup {
        /**
         * just in case you cannot remember how precision/scale looks like
         */
        @Test
        void is_as_expected() {
            Assertions.assertThat(ORIGINAL.precision())
                .isEqualTo(10);

            Assertions.assertThat(ORIGINAL.scale())
                .isEqualTo(7);

            Assertions.assertThat(ORIGINAL.intValue())
                .isEqualTo(123);

            BigDecimal remainder = ORIGINAL.remainder(BigDecimal.ONE);
            Assertions.assertThat(remainder.toPlainString())
                .isEqualTo("0.4567890");
        }
    }

    @Nested
    class Constructor {

        @Test
        void calls_roundTo() {
            var sut = new BigDecimalExtRecorder(BigDecimalExtTest.ORIGINAL, BigDecimalExtTest.ORIGINAL_CONTEXT);

            Assertions.assertThat(sut.actualContext)
                .isSameAs(ORIGINAL_CONTEXT);

            Assertions.assertThat(sut.actualValue)
                .isSameAs(ORIGINAL);
        }

        @Test
        void sets_fields() {
            Assertions.assertThat(FIXTURE.getValue())
                .isSameAs(ORIGINAL);
            Assertions.assertThat(FIXTURE.getContext())
                .isSameAs(ORIGINAL_CONTEXT);
        }

        @Test
        void throws_for_null_value() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new BigDecimalExt(null, ORIGINAL_CONTEXT)
            );

            Assertions.assertThat(ex)
                .hasMessageContaining("value");
        }

        @Test
        void throws_for_null_context() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new BigDecimalExt(BigDecimal.ONE, null)
            );

            Assertions.assertThat(ex)
                .hasMessageContaining("context");
        }

        @SuppressWarnings("PublicField")
        private class BigDecimalExtRecorder extends BigDecimalExt {
            private static final long serialVersionUID = 1851946259286645525L;
            public @Nullable BigDecimal actualValue;
            public @Nullable BigDecimalContext actualContext;

            public BigDecimalExtRecorder(BigDecimal value, BigDecimalContext context) {
                super(value, context);
            }

            @Override
            public BigDecimal roundTo(@UnderInitialization BigDecimalExtRecorder this, BigDecimal value,
                                      BigDecimalContext context) {
                actualValue = value;
                actualContext = context;
                return super.roundTo(value, context);
            }
        }
    }

    @Nested
    class HashCodeEquals {
        @Test
        void equals_for_same_params_and_value() {
            BigDecimalExt a = ORIGINAL_CONTEXT.withValue(BigDecimal.valueOf(123));
            BigDecimalExt b = ORIGINAL_CONTEXT.withValue(BigDecimal.valueOf(123));

            assertThat(a)
                .isEqualTo(b);

            Assertions.assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_for_same_params_and_value_with_differing_precision() {
            BigDecimalExt a = ORIGINAL_CONTEXT.withValue(new BigDecimal("123"));
            BigDecimalExt b = ORIGINAL_CONTEXT.withValue(new BigDecimal("123.0"));

            assertThat(a)
                .isNotEqualTo(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_contexts() {
            BigDecimalExt a = BigDecimalContext.from(10, 7).withValue(BigDecimal.valueOf(123));
            BigDecimalExt b = BigDecimalContext.from(10, 5).withValue(BigDecimal.valueOf(123));

            assertThat(a)
                .isNotEqualTo(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }
    }

    @Nested
    class EqualsComparingValue {
        @Test
        void equals_for_same_params_and_value() {
            BigDecimalExt a = ORIGINAL_CONTEXT.withValue(BigDecimal.valueOf(123));
            BigDecimalExt b = ORIGINAL_CONTEXT.withValue(BigDecimal.valueOf(123));

            assertThat(a)
                .isEqualComparingValue(b);

            Assertions.assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void equals_for_same_params_and_value_with_differing_precision() {
            BigDecimalExt a = ORIGINAL_CONTEXT.withValue(new BigDecimal("123"));
            BigDecimalExt b = ORIGINAL_CONTEXT.withValue(new BigDecimal("123.0"));

            assertThat(a)
                .isEqualComparingValue(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_contexts() {
            BigDecimalExt a = BigDecimalContext.from(10, 7).withValue(BigDecimal.valueOf(123));
            BigDecimalExt b = BigDecimalContext.from(10, 5).withValue(BigDecimal.valueOf(123));

            assertThat(a)
                .isNotEqualComparingValue(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }
    }

    @Nested
    class RoundToTest {

        @Test
        void does_nothing_for_same_params() {
            BigDecimalExt actual = FIXTURE.roundTo(ORIGINAL_CONTEXT);

            assertThat(actual)
                .isEqualTo(FIXTURE);
        }

        @Test
        void rounds_to_smaller_scale() {
            BigDecimalContext smallScale = ORIGINAL_CONTEXT.withMaxScale(3);

            BigDecimalExt actual = FIXTURE.roundTo(smallScale);

            assertThat(actual)
                .hasPrecision(ORIGINAL_CONTEXT.getPrecision())
                .hasRoundingMode(ORIGINAL_CONTEXT.getRoundingMode())
                .hasValue("123.457");
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
            BigDecimalExt sut = FIXTURE.roundTo(new BigDecimalContext(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameParamsAs(sut)
            ;
        }

        @ParameterizedTest
        @CsvSource("10, 7, HALF_UP, 123.4567890, 10, 7")
        void truncating_to_same_values_keeps_same_data(
            int precision,
            int scale,
            RoundingMode roundingMode,
            String expectedValue,
            int expectedPrecision,
            int expectedScale
        ) {
            BigDecimalExt sut = FIXTURE.roundTo(new BigDecimalContext(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameParamsAs(sut)
            ;
        }

        @ParameterizedTest
        @CsvSource({
            "11, 7, HALF_UP, 123.4567890, 10, 7",
            "10, 8, HALF_UP, 123.4567890, 10, 7",
            "11, 8, HALF_UP, 123.4567890, 10, 7",
        })
        void expanding_scale_and_precision_is_limited_by_actual_value(
            int precision,
            int scale,
            RoundingMode roundingMode,
            String expectedValue,
            int expectedPrecision,
            int expectedScale
        ) {
            BigDecimalExt sut = FIXTURE.roundTo(new BigDecimalContext(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameParamsAs(sut)
            ;
        }

        @ParameterizedTest
        @CsvSource({
            "10, 2, HALF_UP, 123.46, 5, 2",
            "10, 2, DOWN, 123.45, 5, 2",
        })
        void truncating_scale_rounds_according_to_parameter(
            int precision,
            int scale,
            RoundingMode roundingMode,
            String expectedValue,
            int expectedPrecision,
            int expectedScale
        ) {
            BigDecimalExt sut = FIXTURE.roundTo(new BigDecimalContext(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameParamsAs(sut)
            ;
        }

        @ParameterizedTest
        @CsvSource({
            "5, 2, HALF_UP, 123.46, 5, 2",
            "5, 2, DOWN, 123.45, 5, 2",
        })
        void truncating_both_scale_and_precision_rounds_according_to_parameter(
            int precision,
            int scale,
            RoundingMode roundingMode,
            String expectedValue,
            int expectedPrecision,
            int expectedScale
        ) {
            BigDecimalExt sut = FIXTURE.roundTo(new BigDecimalContext(precision, scale, roundingMode));

            assertThat(sut)
                .hasValue(expectedValue)
                .hasValuePrecision(expectedPrecision)
                .hasValueScale(expectedScale)
                .hasSameParamsAs(sut)
            ;
        }

    }

    @Nested
    class ToString {
        @Test
        void includes_all_parameters() {
            //BigDecimalExt[%s, context=%s]
            String actual = FIXTURE.toString();

            Assertions.assertThat(actual)
                .isEqualTo("BigDecimalExt[123.4567890, context=[10,7,HALF_UP]]");
        }
    }

}
