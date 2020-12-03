package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.RoundingScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.github.honoluluhenk.fluentbigdecimals.FluentBigDecimal.HUNDRED;
import static com.github.honoluluhenk.fluentbigdecimals.FluentBigDecimal.valueOf;
import static com.github.honoluluhenk.fluentbigdecimals.Helpers.curryReverse;
import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(generator = DisplayNameGenerator.ReplaceUnderscores.class)
// FIXME: create checker annotations for AssertJ
@SuppressWarnings({"nullable", "argument.type.incompatible", "initialization.fields.uninitialized"})
class FluentBigDecimalTest {

    private static class DummyScaler implements Scaler {
        private static final long serialVersionUID = 7707531701229950642L;

        @Override
        public <Argument> BigDecimal apply(ProjectionFunction<BigDecimal, Argument, BigDecimal> function, BigDecimal value, Argument argument) {
            throw new IllegalStateException("Should not be needed!");
        }
    }

    private static class FixedValueScaler implements Scaler {
        private static final long serialVersionUID = 5460135929536529147L;
        private final BigDecimal fixedValue;

        public FixedValueScaler(BigDecimal fixedValue) {
            this.fixedValue = fixedValue;
        }

        @Override
        public <Argument> BigDecimal apply(ProjectionFunction<BigDecimal, Argument, BigDecimal> function, BigDecimal value, Argument argument) {
            return fixedValue;
        }
    }

    private static final BigDecimal FIXTURE_VALUE = new BigDecimal("123.45");
    private static final MathContext FIXTURE_MATH_CONTEXT = new MathContext(5, HALF_UP);
    private static final Scaler FIXTURE_SCALER = new RoundingScaler(FIXTURE_MATH_CONTEXT);
    private static final FluentBigDecimal FIXTURE = new FluentBigDecimal(FIXTURE_VALUE, FIXTURE_MATH_CONTEXT, FIXTURE_SCALER);

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
        void does_not_call_scaler() {
            var scalerSpy = spy(new DummyScaler());
            new FluentBigDecimal(FIXTURE_VALUE, FIXTURE_MATH_CONTEXT, scalerSpy);

            verify(scalerSpy, never())
                .apply(any(ProjectionFunction.class), any(BigDecimal.class), any(BigDecimal.class));
        }

        @Test
        void sets_fields() {
            assertThat(FIXTURE.getValue())
                .isSameAs(FIXTURE_VALUE);
            assertThat(FIXTURE.getScaler())
                .isSameAs(FIXTURE_SCALER);
        }

        @Test
        void throws_for_null_value() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new FluentBigDecimal(null, FIXTURE_MATH_CONTEXT, FIXTURE_SCALER)
            );

            assertThat(ex)
                .hasMessageContaining("value");
        }

        @Test
        void throws_for_null_scaler() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new FluentBigDecimal(BigDecimal.ONE, FIXTURE_MATH_CONTEXT, null)
            );

            assertThat(ex)
                .hasMessageContaining("scaler");
        }
    }

    @Nested
    class HashCodeEquals {
        @Test
        void equals_for_equal_value_and_any_scaler() {
            FluentBigDecimal a = valueOf("123", FIXTURE_MATH_CONTEXT, FIXTURE_SCALER);
            FluentBigDecimal b = valueOf("123", FIXTURE_MATH_CONTEXT, FIXTURE_SCALER);

            assertThat(a)
                .isEqualTo(b);

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_for_value_with_differing_precision() {
            FluentBigDecimal a = valueOf("123", FIXTURE_MATH_CONTEXT, FIXTURE_SCALER);
            FluentBigDecimal b = valueOf("123.0", FIXTURE_MATH_CONTEXT, FIXTURE_SCALER);

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_values() {
            FluentBigDecimal a = valueOf("123", FIXTURE_MATH_CONTEXT, FIXTURE_SCALER);
            FluentBigDecimal b = valueOf("456", FIXTURE_MATH_CONTEXT, FIXTURE_SCALER);

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
                .isEqualTo("BigDecimalExt[123.45, RoundingScaler]");
        }
    }

    @Nested
    class Add {

        @Test
        void keeps_same_scaler() {
            keeps_same_scaler_impl(FluentBigDecimal::add);
        }

        @Test
        void treats_null_as_neutral_value() {
            adds_null_as_neutral_value_impl(FluentBigDecimal::add);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0, 0",
            "0, 1, 1",
            "0, -1, -1",
            "123.45, 543.21, 666.66",
            "123.45, 9999.99, 10123",
            "123.45, 9999.99999, 10123",
        })
        void adds_and_calls_scaler(BigDecimal augend, BigDecimal addend, BigDecimal expectedValue) {
            executes_and_calls_scaler_impl(FluentBigDecimal::add, augend, addend, expectedValue);
        }
    }

    @Nested
    class Subtract {

        @Test
        void keeps_same_scaler() {
            keeps_same_scaler_impl(FluentBigDecimal::subtract);
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
            "10123.44999, 9999.99999, 123.45",
        })
        void subtracts_and_calls_scaler(BigDecimal minuend, BigDecimal subtrahend, BigDecimal expectedValue) {
            executes_and_calls_scaler_impl(FluentBigDecimal::subtract, minuend, subtrahend, expectedValue);
        }
    }


    @Nested
    class Multiply {

        @Test
        void keeps_same_scaler() {
            keeps_same_scaler_impl(FluentBigDecimal::multiply);
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
            "123.45, 9999.99, 1.2345E+6", // remember: precision = 5
            "123.45, 9999.99999, 1.2345E+6", // remember: precision = 5
        })
        void multiplys_and_calls_scaler(BigDecimal multiplicand, BigDecimal multiplicator, BigDecimal expectedValue) {
            executes_and_calls_scaler_impl(FluentBigDecimal::multiply, multiplicand, multiplicator, expectedValue);
        }
    }

    @Nested
    class Divide {

        @Test
        void keeps_same_scaler() {
            keeps_same_scaler_impl(FluentBigDecimal::divide);
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
            // please note: using the IdentityScaler requires input parameters that to a terminating division
            // or else an ArithmethcException is thrown.
            // Example for invalid input: 1/3
        void divides_and_calls_scaler(BigDecimal dividend, BigDecimal divisor, BigDecimal expectedValue) {
            executes_and_calls_scaler_impl(FluentBigDecimal::divide, dividend, divisor, expectedValue);
        }
    }

    @Nested
    class PctToFraction {

        @Test
        void keeps_same_scaler() {
            keeps_same_scaler_impl(FluentBigDecimal::pctToFraction);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0",
            "100, 1",
            "50, 0.5",
            "1, 0.01",
            "0.001, 0.00001",
        })
        void divides_by_HUNDRED_and_calls_scaler(BigDecimal multiplicand, BigDecimal expectedValue) {
            executes_and_calls_scaler_impl(FluentBigDecimal::pctToFraction, multiplicand, HUNDRED, expectedValue);
        }
    }

    @Nested
    class FractionToPct {

        @Test
        void keeps_same_scaler() {
            keeps_same_scaler_impl(FluentBigDecimal::fractionToPct);
        }

        @ParameterizedTest
        @CsvSource({
            "0, 0",
            "1, 100",
            "0.5, 50.0",
            "0.01, 1.00",
            "0.00001, 0.00100",
        })
        void multiplies_by_HUNDRED_and_calls_scaler(BigDecimal multiplicand, BigDecimal expectedValue) {
            executes_and_calls_scaler_impl(FluentBigDecimal::fractionToPct, multiplicand, HUNDRED, expectedValue);
        }
    }

    @Nested
    class AdjustInto {

        @Test
        void creates_adjusted_value_using_other_scaler() {
            FluentBigDecimal sut = valueOf("123.456", FIXTURE_MATH_CONTEXT, FIXTURE_SCALER);
            FixedValueScaler stubScaler = new FixedValueScaler(new BigDecimal("42"));

            FluentBigDecimal result = sut
                .adjustInto(stubScaler);

            assertThat(result.getScaler())
                .isEqualTo(stubScaler);
            assertThat(result.getValue())
                .isEqualTo(stubScaler.fixedValue);
        }

    }


    void keeps_same_scaler_impl(BinaryOperator<FluentBigDecimal> fnc) {
        FluentBigDecimal actual = fnc.apply(FIXTURE, FIXTURE);

        assertThat(actual.getScaler())
            .isEqualTo(FIXTURE.getScaler());
    }

    void keeps_same_scaler_impl(UnaryOperator<FluentBigDecimal> fnc) {
        FluentBigDecimal actual = fnc.apply(FIXTURE);

        assertThat(actual.getScaler())
            .isEqualTo(FIXTURE.getScaler());
    }

    void adds_null_as_neutral_value_impl(BinaryOperator<FluentBigDecimal> fnc) {
        FluentBigDecimal actual = fnc.apply(FIXTURE, null);

        assertThat(actual.getValue())
            .isEqualTo(FIXTURE.getValue());
    }


    void executes_and_calls_scaler_impl(
        BiFunction<FluentBigDecimal, FluentBigDecimal, FluentBigDecimal> operation,
        BigDecimal operandA,
        BigDecimal operandB,
        BigDecimal expectedValue
    ) {
        FluentBigDecimal fluentOperandB = valueOf(operandB, FIXTURE_MATH_CONTEXT, FIXTURE_SCALER);
        Function<FluentBigDecimal, FluentBigDecimal> curried = curryReverse(operation, fluentOperandB);

        executes_and_calls_scaler_impl(curried, operandA, operandB, expectedValue);
    }

    void executes_and_calls_scaler_impl(
        Function<FluentBigDecimal, FluentBigDecimal> operation,
        BigDecimal operandA,
        BigDecimal operandB,
        BigDecimal expectedValue) {
        var scaler = spy(new RoundingScaler(FIXTURE_MATH_CONTEXT));
        FluentBigDecimal sut = valueOf(operandA, FIXTURE_MATH_CONTEXT, scaler);

        var actual = operation.apply(sut);

        assertThat(actual.getValue())
            .isEqualTo(expectedValue);
        verify(scaler)
            .apply(any(ProjectionFunction.class), eq(operandA), eq(operandB));
    }

}
