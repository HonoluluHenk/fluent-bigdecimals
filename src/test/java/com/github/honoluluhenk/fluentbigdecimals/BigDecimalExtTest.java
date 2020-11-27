package com.github.honoluluhenk.fluentbigdecimals;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.honoluluhenk.fluentbigdecimals.BigDecimalExt.of;
import static com.github.honoluluhenk.fluentbigdecimals.BigDecimalExtAssert.assertThat;
import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(generator = DisplayNameGenerator.ReplaceUnderscores.class)
// FIXME: create checker annotations for AssertJ
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"nullable", "argument.type.incompatible", "initialization.fields.uninitialized"})
class BigDecimalExtTest {

    private static final BigDecimal FIXTURE_VALUE = new BigDecimal("123.45");
    private static final Adjuster FIXTURE_ADJUSTER = new IdentityAdjuster();
    private static final BigDecimalExt FIXTURE = new BigDecimalExt(FIXTURE_VALUE, FIXTURE_ADJUSTER);

    static BigDecimalExt bde(String bigDecimal) {
        BigDecimal bd = new BigDecimal(bigDecimal);
        return FloatingPointAdjuster.from(bd, HALF_UP)
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
        void calls_adjuster() {
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
                .isSameAs(FIXTURE_ADJUSTER);
        }

        @Test
        void throws_for_null_value() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new BigDecimalExt(null, FIXTURE_ADJUSTER)
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
        void equals_for_equal_value_and_any_adjuster() {
            BigDecimalExt a = of("123", new IdentityAdjuster());
            BigDecimalExt b = of("123", new IdentityAdjuster());

            assertThat(a)
                .isEqualTo(b);

            Assertions.assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_for_value_with_differing_precision() {
            BigDecimalExt a = of("123", new IdentityAdjuster());
            BigDecimalExt b = of("123.0", new IdentityAdjuster());

            assertThat(a)
                .isNotEqualTo(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_values() {
            BigDecimalExt a = of("123", new IdentityAdjuster());
            BigDecimalExt b = of("456", new IdentityAdjuster());

            assertThat(a)
                .isNotEqualTo(b);

            Assertions.assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

    }

    @Nested
    class ToString {
        @Test
        void includes_all_parameters() {
            String actual = FIXTURE.toString();

            Assertions.assertThat(actual)
                .isEqualTo("BigDecimalExt[123.45, IdentityAdjuster]");
        }
    }

    @Nested
    class Add {
        @Mock
        IdentityAdjuster mockAdjuster;


        @Test
        void keeps_same_adjuster() {
            BigDecimalExt actual = FIXTURE.add(FIXTURE);

            assertThat(actual)
                .hasSameAdjusterAs(FIXTURE)
                .hasValue("246.90");
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0, 0",
            "0, 1, 1",
            "0, -1, -1",
            "123.45, 9999.99, 10123.44",
            "123.45, 9999.99999, 10123.44999", // no rounding, expand precision/scale
        })
        void adds_and_calls_adjuster(BigDecimal augend, BigDecimal addend, String expectedValue) {
            given(mockAdjuster.needsAdjusting(any()))
                .willReturn(true);
            given(mockAdjuster.adjust(any()))
                .willAnswer(Answers.CALLS_REAL_METHODS);

            BigDecimalExt sut = of(augend, mockAdjuster);

            var actual = sut.add(addend);

            assertThat(actual)
                .hasValue(expectedValue);
            verify(mockAdjuster, times(2))
                .adjust(any(BigDecimal.class));
        }

        @Test
        void keeps_adjuster() {
            BigDecimalExt actual = FIXTURE.add("99.999");

            assertThat(actual)
                .hasSameAdjuster(FIXTURE_ADJUSTER);
        }

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
//            BigDecimalExt first = BigDecimalExt.from(firstPrecision, firstScale).withValue(firstValue);
//            BigDecimalExt second = FixedPrecisionAdjuster.from(secondPrecision, secondScale).withValue(secondValue);
//            BigDecimalExt expected = FixedPrecisionAdjuster.from(expectedPrecision, expectedScale).withValue(expectedValue);
//
//            BigDecimalExt actual = first.add(second);
//
//            assertThat(actual)
//                .isEqualTo(expected);
//        }
    }

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
