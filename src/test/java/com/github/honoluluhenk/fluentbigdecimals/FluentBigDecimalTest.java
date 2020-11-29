package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.adjuster.Adjuster;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.github.honoluluhenk.fluentbigdecimals.FluentBigDecimal.valueOf;
import static com.github.honoluluhenk.fluentbigdecimals.Helpers.curryReverse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(generator = DisplayNameGenerator.ReplaceUnderscores.class)
// FIXME: create checker annotations for AssertJ
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"nullable", "argument.type.incompatible", "initialization.fields.uninitialized"})
class FluentBigDecimalTest {

    private static class DummyAdjuster implements Adjuster {
        private static final long serialVersionUID = 7707531701229950642L;

        @Override
        public BigDecimal adjust(BigDecimal value) {
            throw new IllegalStateException("Should not be needed!");
        }

        @Override
        public MathContext getMathContext() {
            throw new IllegalStateException("Not Needed");
        }
    }

    private static class FakeAdjuster implements Adjuster {
        private static final long serialVersionUID = 7707531701229950642L;
        // FIXME: implement some test that actually probe the limit (e.g. by division)
        public static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);

        @Override
        public BigDecimal adjust(BigDecimal value) {
            return value.round(MATH_CONTEXT);
        }

        @Override
        public MathContext getMathContext() {
            return MATH_CONTEXT;
        }

        @Override
        public String toString() {
            return "FakeAdjuster";
        }
    }

    private static final BigDecimal FIXTURE_VALUE = new BigDecimal("123.45");
    private static final Adjuster FIXTURE_ADJUSTER = new FakeAdjuster();
    private static final FluentBigDecimal FIXTURE = new FluentBigDecimal(FIXTURE_VALUE, FIXTURE_ADJUSTER);


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
            var adjusterSpy = spy(new DummyAdjuster());
            new FluentBigDecimal(FIXTURE_VALUE, adjusterSpy);

            verify(adjusterSpy, never())
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
                () -> new FluentBigDecimal(null, FIXTURE_ADJUSTER)
            );

            assertThat(ex)
                .hasMessageContaining("value");
        }

        @Test
        void throws_for_null_adjuster() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new FluentBigDecimal(BigDecimal.ONE, null)
            );

            assertThat(ex)
                .hasMessageContaining("adjuster");
        }
    }

    @Nested
    class HashCodeEquals {
        @Test
        void equals_for_equal_value_and_any_adjuster() {
            FluentBigDecimal a = valueOf("123", new FakeAdjuster());
            FluentBigDecimal b = valueOf("123", new FakeAdjuster());

            assertThat(a)
                .isEqualTo(b);

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_for_value_with_differing_precision() {
            FluentBigDecimal a = valueOf("123", new FakeAdjuster());
            FluentBigDecimal b = valueOf("123.0", new FakeAdjuster());

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_values() {
            FluentBigDecimal a = valueOf("123", new FakeAdjuster());
            FluentBigDecimal b = valueOf("456", new FakeAdjuster());

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
                .isEqualTo("BigDecimalExt[123.45, FakeAdjuster]");
        }
    }

    @Nested
    class Add {

        @Test
        void keeps_same_adjuster() {
            keeps_same_adjuster_impl(FluentBigDecimal::add);
        }

        @Test
        void treats_null_as_neutral_value() {
            adds_null_as_neutral_value_impl(FluentBigDecimal::add);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0, 0",
//            "0, 1, 1",
//            "0, -1, -1",
//            "123.45, 9999.99, 10123.44",
//            "123.45, 9999.99999, 10123.44999",
        })
        void adds_and_calls_adjuster(BigDecimal augend, BigDecimal addend, BigDecimal expectedValue) {
            executes_and_calls_adjuster_impl(FluentBigDecimal::add, augend, addend, expectedValue);
        }
    }

    @Nested
    class Subtract {

        @Test
        void keeps_same_adjuster() {
            keeps_same_adjuster_impl(FluentBigDecimal::subtract);
        }

        @Test
        void treats_null_as_neutral_value() {
            adds_null_as_neutral_value_impl(FluentBigDecimal::subtract);
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
            executes_and_calls_adjuster_impl(FluentBigDecimal::subtract, minuend, subtrahend, expectedValue);
        }
    }


    @Nested
    class Multiply {

        @Test
        void keeps_same_adjuster() {
            keeps_same_adjuster_impl(FluentBigDecimal::multiply);
        }

        @Test
        void treats_null_as_neutral_value() {
            adds_null_as_neutral_value_impl(FluentBigDecimal::multiply);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0, 0",
            "0, 1, 0",
            "0, -1, 0",
            "123.45, 9999.99, 1234498.766",
            "123.45, 9999.99999, 1234499.999",
        })
        void multiplys_and_calls_adjuster(BigDecimal multiplicand, BigDecimal multiplicator, BigDecimal expectedValue) {
            executes_and_calls_adjuster_impl(FluentBigDecimal::multiply, multiplicand, multiplicator, expectedValue);
        }
    }

    @Nested
    class Divide {

        @Test
        void keeps_same_adjuster() {
            keeps_same_adjuster_impl(FluentBigDecimal::divide);
        }

        @Test
        void treats_null_as_neutral_value() {
            adds_null_as_neutral_value_impl(FluentBigDecimal::divide);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 1, 0",
            "0, -1, 0",
            "1, 2, 0.5",
        })
            // please note: using the IdentityAdjuster requires input parameters that to a terminating division
            // or else an ArithmethcException is thrown.
            // Example for invalid input: 1/3
        void divides_and_calls_adjuster(BigDecimal dividend, BigDecimal divisor, BigDecimal expectedValue) {
            executes_and_calls_adjuster_impl(FluentBigDecimal::divide, dividend, divisor, expectedValue);
        }
    }

    @Nested
    class PctToFraction {

        @Test
        void keeps_same_adjuster() {
            keeps_same_adjuster_impl(FluentBigDecimal::pctToFraction);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0",
            "100, 1",
            "50, 0.5",
            "1, 0.01",
            "0.001, 0.00001",
        })
        void calculates_and_calls_adjuster(BigDecimal multiplicand, BigDecimal expectedValue) {
            executes_and_calls_adjuster_impl(FluentBigDecimal::pctToFraction, multiplicand, expectedValue);
        }
    }

    @Nested
    class FractionToPct {

        @Test
        void keeps_same_adjuster() {
            keeps_same_adjuster_impl(FluentBigDecimal::fractionToPct);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0",
            "1, 100",
            "0.5, 50.0",
            "0.01, 1.00",
            "0.00001, 0.00100",
        })
        void calculates_and_calls_adjuster(BigDecimal multiplicand, BigDecimal expectedValue) {
            executes_and_calls_adjuster_impl(FluentBigDecimal::fractionToPct, multiplicand, expectedValue);
        }
    }

    @Nested
    class AdjustInto {

        @Test
        void creates_adjusted_value_using_other_adjuster() {
            String initialValue = "123.456";
            BigDecimal adjustedValue = new BigDecimal("42");
            FluentBigDecimal sut = valueOf(initialValue, FIXTURE_ADJUSTER);
            Adjuster otherAdjuster = new Adjuster() {
                private static final long serialVersionUID = 5460135929536529147L;

                @Override
                public BigDecimal adjust(BigDecimal value) {
                    return adjustedValue;
                }

                @Override
                public MathContext getMathContext() {
                    throw new IllegalStateException("not needed");
                }
            };

            FluentBigDecimal result = sut
                .adjustInto(otherAdjuster);

            assertThat(result.getAdjuster())
                .isEqualTo(otherAdjuster);
            assertThat(result.getValue())
                .isEqualTo(adjustedValue);
        }
    }


    void keeps_same_adjuster_impl(BinaryOperator<FluentBigDecimal> fnc) {
        FluentBigDecimal actual = fnc.apply(FIXTURE, FIXTURE);

        assertThat(actual.getAdjuster())
            .isEqualTo(FIXTURE.getAdjuster());
    }

    void keeps_same_adjuster_impl(UnaryOperator<FluentBigDecimal> fnc) {
        FluentBigDecimal actual = fnc.apply(FIXTURE);

        assertThat(actual.getAdjuster())
            .isEqualTo(FIXTURE.getAdjuster());
    }

    void adds_null_as_neutral_value_impl(BinaryOperator<FluentBigDecimal> fnc) {
        FluentBigDecimal actual = fnc.apply(FIXTURE, null);

        assertThat(actual.getValue())
            .isEqualTo(FIXTURE.getValue());
    }


    void executes_and_calls_adjuster_impl(
        BiFunction<FluentBigDecimal, FluentBigDecimal, FluentBigDecimal> operation,
        BigDecimal start,
        BigDecimal other,
        BigDecimal expectedValue
    ) {
        FluentBigDecimal otherExt = FluentBigDecimal.valueOf(other, new DummyAdjuster());
        Function<FluentBigDecimal, FluentBigDecimal> curried = curryReverse(operation, otherExt);

        executes_and_calls_adjuster_impl(curried, start, expectedValue);
    }

    void executes_and_calls_adjuster_impl(
        Function<FluentBigDecimal, FluentBigDecimal> operation,
        BigDecimal start,
        BigDecimal expectedValue
    ) {
        var adjuster = spy(new FakeAdjuster());
        FluentBigDecimal sut = FluentBigDecimal.valueOf(start, adjuster);

        var actual = operation.apply(sut);

        assertThat(actual.getValue())
            .isEqualTo(expectedValue);
        verify(adjuster)
            .adjust(expectedValue);
        //noinspection ResultOfMethodCallIgnored
        verify(adjuster)
            .getMathContext();
    }

}
