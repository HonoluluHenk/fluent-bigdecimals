package com.github.honoluluhenk.fluentbigdecimals;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.github.honoluluhenk.fluentbigdecimals.BigDecimalExtAssert.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(generator = DisplayNameGenerator.ReplaceUnderscores.class)
class BigDecimalExtTest {

    private final BigDecimal ORIGINAL = new BigDecimal("123.4567890");


    /**
     * just in case you cannot remember how precision/scale looks like
     */
    @Test
    void testSetupIsAsExpected() {
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


    @Nested
    class ConstructorTest {

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
            BigDecimalExt sut = new BigDecimalExt(ORIGINAL, new BigDecimalContext(precision, scale, roundingMode));

            assertThat(sut)
                    .hasValue(expectedValue)
                    .hasValuePrecision(expectedPrecision)
                    .hasValueScale(expectedScale)
                    .hasSameParamsAs(sut)
            ;
        }

        @ParameterizedTest
        @CsvSource({
                "10, 7, HALF_UP, 123.4567890, 10, 7",
        })
        void truncatingToSameValuesKeepsSameData(
                int precision,
                int scale,
                RoundingMode roundingMode,
                String expectedValue,
                int expectedPrecision,
                int expectedScale
        ) {
            BigDecimalExt sut = new BigDecimalExt(ORIGINAL, new BigDecimalContext(precision, scale, roundingMode));

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
        void expandingScaleAndPrecisionIsLimitedByActualValue(
                int precision,
                int scale,
                RoundingMode roundingMode,
                String expectedValue,
                int expectedPrecision,
                int expectedScale
        ) {
            BigDecimalExt sut = new BigDecimalExt(ORIGINAL, new BigDecimalContext(precision, scale, roundingMode));

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
        void truncatingScaleRoundsAccordingToParameter(
                int precision,
                int scale,
                RoundingMode roundingMode,
                String expectedValue,
                int expectedPrecision,
                int expectedScale
        ) {
            BigDecimalExt sut = new BigDecimalExt(ORIGINAL, new BigDecimalContext(precision, scale, roundingMode));

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
        void truncatingBothScaleAndPrecisionRoundsAccordingToParmeter(
                int precision,
                int scale,
                RoundingMode roundingMode,
                String expectedValue,
                int expectedPrecision,
                int expectedScale
        ) {
            BigDecimalExt sut = new BigDecimalExt(ORIGINAL, new BigDecimalContext(precision, scale, roundingMode));

            assertThat(sut)
                    .hasValue(expectedValue)
                    .hasValuePrecision(expectedPrecision)
                    .hasValueScale(expectedScale)
                    .hasSameParamsAs(sut)
            ;
        }

    }

//    @Nested
//    class AddTest {
//        BigDecimalExt actual = ORIGINAL.add()
//    }
}
