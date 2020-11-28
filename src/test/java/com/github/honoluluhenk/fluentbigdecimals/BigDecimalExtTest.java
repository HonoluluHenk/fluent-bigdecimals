package com.github.honoluluhenk.fluentbigdecimals;

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
import static org.assertj.core.api.Assertions.assertThat;
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
            assertThat(FIXTURE_VALUE.precision())
                .isEqualTo(5);

            assertThat(FIXTURE_VALUE.scale())
                .isEqualTo(2);

            assertThat(FIXTURE_VALUE.intValue())
                .isEqualTo(123);

            BigDecimal remainder = FIXTURE_VALUE.remainder(BigDecimal.ONE);
            assertThat(remainder.toPlainString())
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

            assertThat(ref.get())
                .isSameAs(FIXTURE_VALUE);

        }

        @Test
        void sets_fields() {
            assertThat(FIXTURE.getValue())
                .isSameAs(FIXTURE_VALUE);
            assertThat(FIXTURE.getAdjuster())
                .isSameAs(FIXTURE_ADJUSTER);
        }

        @Test
        void throws_for_null_value() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new BigDecimalExt(null, FIXTURE_ADJUSTER)
            );

            assertThat(ex)
                .hasMessageContaining("value");
        }

        @Test
        void throws_for_null_adjuster() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new BigDecimalExt(BigDecimal.ONE, null)
            );

            assertThat(ex)
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

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_for_value_with_differing_precision() {
            BigDecimalExt a = of("123", new IdentityAdjuster());
            BigDecimalExt b = of("123.0", new IdentityAdjuster());

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_values() {
            BigDecimalExt a = of("123", new IdentityAdjuster());
            BigDecimalExt b = of("456", new IdentityAdjuster());

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

    }

    @Nested
    class ToString {
        @Test
        void includes_all_parameters() {
            String actual = FIXTURE.toString();

            assertThat(actual)
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
    }

}
