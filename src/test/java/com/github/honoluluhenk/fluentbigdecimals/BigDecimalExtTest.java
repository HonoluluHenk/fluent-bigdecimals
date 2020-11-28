package com.github.honoluluhenk.fluentbigdecimals;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import static com.github.honoluluhenk.fluentbigdecimals.BigDecimalExt.valueOf;
import static com.github.honoluluhenk.fluentbigdecimals.BigDecimalExtAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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

    @Mock
    Adjuster mockAdjuster;

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
        void does_not_call_adjuster() {
            new BigDecimalExt(FIXTURE_VALUE, mockAdjuster);

            verify(mockAdjuster, never())
                .adjust(any());
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
            BigDecimalExt a = valueOf("123", new IdentityAdjuster());
            BigDecimalExt b = valueOf("123", new IdentityAdjuster());

            assertThat(a)
                .isEqualTo(b);

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_for_value_with_differing_precision() {
            BigDecimalExt a = valueOf("123", new IdentityAdjuster());
            BigDecimalExt b = valueOf("123.0", new IdentityAdjuster());

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_values() {
            BigDecimalExt a = valueOf("123", new IdentityAdjuster());
            BigDecimalExt b = valueOf("456", new IdentityAdjuster());

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

        @Test
        void keeps_same_adjuster() {
            keeps_same_adjuster_impl(BigDecimalExt::add);
        }

        @Test
        void treats_null_as_neutral_value() {
            adds_null_as_neutral_value_impl(BigDecimalExt::add);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0, 0",
            "0, 1, 1",
            "0, -1, -1",
            "123.45, 9999.99, 10123.44",
            "123.45, 9999.99999, 10123.44999",
        })
        void adds_and_calls_adjuster(BigDecimal augend, BigDecimal addend, BigDecimal expectedValue) {
            executes_and_calls_adjuster_impl(BigDecimalExt::add, augend, addend, expectedValue);
        }
    }

    @Nested
    class Subtract {

        @Test
        void keeps_same_adjuster() {
            keeps_same_adjuster_impl(BigDecimalExt::subtract);
        }

        @Test
        void treats_null_as_neutral_value() {
            adds_null_as_neutral_value_impl(BigDecimalExt::subtract);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0, 0",
            "0, 1, -1",
            "0, -1, 1",
            "10123.44, 9999.99, 123.45",
            "10123.44999, 9999.99999, 123.45000",
        })
        void subtracts_and_calls_adjuster(BigDecimal minuend, BigDecimal subtrahend, BigDecimal expectedValue) {
            executes_and_calls_adjuster_impl(BigDecimalExt::subtract, minuend, subtrahend, expectedValue);
        }
    }

    void keeps_same_adjuster_impl(BinaryOperator<BigDecimalExt> fnc) {
        BigDecimalExt actual = fnc.apply(FIXTURE, FIXTURE);

        assertThat(actual)
            .hasSameAdjusterAs(FIXTURE);
    }

    void adds_null_as_neutral_value_impl(BinaryOperator<BigDecimalExt> fnc) {
        BigDecimalExt actual = fnc.apply(FIXTURE, null);

        assertThat(actual.getValue())
            .isEqualTo(FIXTURE.getValue());

    }


    void executes_and_calls_adjuster_impl(
        BiFunction<BigDecimalExt, BigDecimal, BigDecimalExt> operation,
        BigDecimal start,
        BigDecimal other,
        BigDecimal expectedValue
    ) {
        // second call after the operation (i.e.: with result of
        given(mockAdjuster.adjust(expectedValue))
            .willReturn(expectedValue);
        BigDecimalExt sut = BigDecimalExt.valueOf(start, mockAdjuster);

        var actual = operation.apply(sut, other);

        assertThat(actual.getValue())
            .isEqualTo(expectedValue);
        verify(mockAdjuster)
            .adjust(expectedValue);
    }

}
