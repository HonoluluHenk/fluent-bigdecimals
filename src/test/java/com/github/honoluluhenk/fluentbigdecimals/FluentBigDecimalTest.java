package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.NonNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.math.RoundingMode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@IndicativeSentencesGeneration(generator = DisplayNameGenerator.ReplaceUnderscores.class)
// FIXME: create checker annotations for AssertJ
@SuppressWarnings({"nullable", "argument.type.incompatible", "initialization.fields.uninitialized"})
class FluentBigDecimalTest {

    //<editor-fold defaultstate="collapsed" desc="FixedValueScaler">
    private static class FixedValueScaler implements Scaler {
        private static final long serialVersionUID = 5460135929536529147L;
        private final BigDecimal fixedValue;

        public FixedValueScaler(BigDecimal fixedValue) {
            this.fixedValue = fixedValue;
        }

        @Override
        public BigDecimal scale(BigDecimal value, @NonNull MathContext mathContext) {
            return fixedValue;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="DummyScaler">
    private static class DummyScaler implements Scaler {
        private static final long serialVersionUID = 4432500901893639859L;

        @Override
        public @NonNull BigDecimal scale(@NonNull BigDecimal value, @NonNull MathContext mathContext) {
            throw new IllegalStateException("should not be needed");
        }
    }
    //</editor-fold>

    private static final BigDecimal FIXTURE_VALUE = new BigDecimal("123.45");
    private static final MathContext FIXTURE_MATH_CONTEXT = new MathContext(5, HALF_UP);
    private static final Scaler FIXTURE_SCALER = new NopScaler();
    private static final Configuration<FluentBigDecimal> FIXTURE_CONFIG = new Configuration<>(FIXTURE_MATH_CONTEXT, FIXTURE_SCALER, FluentBigDecimal::new);
    private static final FluentBigDecimal FIXTURE = FIXTURE_CONFIG.of(FIXTURE_VALUE);

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
            var scalerSpy = spy(Scaler.class);
            var config = FIXTURE_CONFIG.withScaler(scalerSpy);
            new FluentBigDecimal(FIXTURE_VALUE, config);

            verify(scalerSpy, never())
                .scale(any(BigDecimal.class), eq(FIXTURE_MATH_CONTEXT));
        }

        @Test
        void sets_fields() {
            assertThat(FIXTURE.getValue())
                .isSameAs(FIXTURE_VALUE);
            assertThat(FIXTURE.getConfiguration().getScaler())
                .isSameAs(FIXTURE_SCALER);
        }

        @Test
        void throws_for_null_value() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new FluentBigDecimal(null, FIXTURE_CONFIG)
            );

            assertThat(ex)
                .hasMessageContaining("value");
        }

        @Test
        void throws_for_null_configuration() {
            var ex = assertThrows(
                NullPointerException.class,
                () -> new FluentBigDecimal(BigDecimal.ONE, null)
            );

            assertThat(ex)
                .hasMessageContaining("configuration required");
        }
    }

    @Nested
    class HashCodeEquals {
        @Test
        void equals_for_equal_value_and_configuration() {
            FluentBigDecimal a = FIXTURE_CONFIG.of("123");
            FluentBigDecimal b = FIXTURE_CONFIG.of("123");

            assertThat(a)
                .isEqualTo(b);

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void equals_for_equal_value_and_differing_configuration() {
            FluentBigDecimal a = FIXTURE_CONFIG.of("123");
            FluentBigDecimal b = FIXTURE_CONFIG.of("123")
                .withMathContext(new MathContext(10, DOWN));

            assertThat(a)
                .isEqualTo(b);

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_for_value_with_differing_precision() {
            FluentBigDecimal a = FIXTURE_CONFIG.of("123");
            FluentBigDecimal b = FIXTURE_CONFIG.of("123.0");

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_for_different_values() {
            FluentBigDecimal a = FIXTURE_CONFIG.of("123");
            FluentBigDecimal b = FIXTURE_CONFIG.of("456");

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

    }

    @Nested
    class CompareTo {
        @Test
        void compares_only_using_value() {

            FluentBigDecimal sut = FIXTURE_CONFIG.of("123.45");
            FluentBigDecimal other = FIXTURE_CONFIG
                .withScaler(new DummyScaler())
                .ofRaw("123.45");

            assertThat(sut)
                .isEqualByComparingTo(other);
        }

        @Test
        void differs_for_a_lt_b() {

            FluentBigDecimal sut = FIXTURE_CONFIG.of("123.45");
            FluentBigDecimal other = FIXTURE_CONFIG.of("543.21");

            assertThat(sut.compareTo(other))
                .isLessThan(0);
        }


        @Test
        void differs_for_b_lt_a() {

            FluentBigDecimal sut = FIXTURE_CONFIG.of("543.21");
            FluentBigDecimal other = FIXTURE_CONFIG.of("123.45");

            assertThat(sut.compareTo(other))
                .isGreaterThan(0);
        }


    }

    @Nested
    class ToString {
        @Test
        void includes_all_parameters() {
            String actual = FIXTURE.toString();

            assertThat(actual)
                .isEqualTo("FluentBigDecimal[123.45,[5,HALF_UP,MaxPrecisionScaler]]");
        }
    }

    @Nested
    class Round {
        @Test
        void rounds_and_keeps_same_configuration() {
            FluentBigDecimal sut = FIXTURE_CONFIG.of("123.456789");

            var actual = sut.round();

            assertThat(actual.getValue())
                .isEqualTo("123.46");
            assertThat(actual.getConfiguration())
                .isSameAs(FIXTURE_CONFIG);
        }
    }

    @Nested
    class RoundInto {
        @Test
        void rounds_and_sets_other_configuration() {
            Scaler otherScaler = (value, mc) -> value.setScale(0, DOWN);
            MathContext otherMathContext = new MathContext(32, UP);
            Configuration<FluentBigDecimal> otherConfiguration = ConfigurationFactory.create(otherMathContext, otherScaler);

            FluentBigDecimal actual = FIXTURE.roundInto(otherConfiguration);

            assertThat(actual.getValue())
                .isEqualTo("123");
            assertThat(actual.getConfiguration())
                .isEqualTo(otherConfiguration);
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
            executes_biProjection_and_calls_scaler_impl(FluentBigDecimal::add, augend, addend, expectedValue);
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
            executes_biProjection_and_calls_scaler_impl(FluentBigDecimal::subtract, minuend, subtrahend, expectedValue);
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
            executes_biProjection_and_calls_scaler_impl(FluentBigDecimal::multiply, multiplicand, multiplicator, expectedValue);
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
            executes_biProjection_and_calls_scaler_impl(FluentBigDecimal::divide, dividend, divisor, expectedValue);
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
            executes_monoProjection_and_calls_scaler_impl(FluentBigDecimal::pctToFraction, multiplicand, expectedValue);
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
            executes_monoProjection_and_calls_scaler_impl(FluentBigDecimal::fractionToPct, multiplicand, expectedValue);
        }
    }

    @Nested
    class Map {
        @Test
        void maps() {
            FluentBigDecimal sut = FIXTURE_CONFIG.of("123.45");

            int actual = sut.map(BigDecimal::intValue);

            assertThat(actual)
                .isEqualTo(123);
        }
    }

    @Nested
    class Apply {
        final Scaler NULL_RETURNING_SCALER = (value, mathContext) -> null;

        @Nested
        class ApplyBiProjection {
            @Test
            void checks_for_null_return_from_Scaler() {
                // explicitly do not add any @NonNull annotation so we can see the behavior
                // of FluentBigDecimals and not this mock scaler!
                FluentBigDecimal sut = FIXTURE_CONFIG
                    .of("123.45")
                    .withScaler(NULL_RETURNING_SCALER);

                NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> sut.apply((value, argument, mc) -> BigDecimal.ONE, FIXTURE_VALUE)
                );

                assertThat(ex)
                    .hasMessage("Scaler must not return null");
            }

            @Test
            void checks_for_null_return_from_projection() {
                // explicitly do not add any @NonNull annotation so we can see the behavior
                // of FluentBigDecimals and not this mock scaler!
                FluentBigDecimal sut = FIXTURE_CONFIG
                    .withScaler(new FixedValueScaler(BigDecimal.ONE))
                    .of("123.45");
                BiProjection<Object> nullProjection = (value, argument, mc) -> null;

                NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> sut.apply(nullProjection, FIXTURE_VALUE)
                );

                assertThat(ex)
                    .hasMessage("Result of projection must not be null");
            }
        }

        @Nested
        class ApplyProjection {
            @Test
            void checks_for_null_return_from_Scaler() {
                // explicitly do not add any @NonNull annotation so we can see the behavior
                // of FluentBigDecimals and not this mock scaler!
                FluentBigDecimal sut = FIXTURE_CONFIG.of("123.45")
                    .withScaler(NULL_RETURNING_SCALER);

                NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> sut.apply((value, mc) -> BigDecimal.ONE)
                );

                assertThat(ex)
                    .hasMessage("Scaler must not return null");

            }

            @Test
            void checks_for_null_return_from_projection() {
                // explicitly do not add any @NonNull annotation so we can see the behavior
                // of FluentBigDecimals and not this mock scaler!
                FluentBigDecimal sut = FIXTURE_CONFIG
                    .withScaler(new FixedValueScaler(BigDecimal.ONE))
                    .of("123.45");
                Projection nullProjection = (value, mc) -> null;

                NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> sut.apply(nullProjection)
                );

                assertThat(ex)
                    .hasMessage("Result of projection must not be null");
            }

        }

    }

    void keeps_same_scaler_impl(BinaryOperator<FluentBigDecimal> fnc) {
        FluentBigDecimal actual = fnc.apply(FIXTURE, FIXTURE);

        assertThat(actual.getConfiguration().getScaler())
            .isEqualTo(FIXTURE.getConfiguration().getScaler());
    }

    void keeps_same_scaler_impl(UnaryOperator<FluentBigDecimal> fnc) {
        FluentBigDecimal actual = fnc.apply(FIXTURE);

        assertThat(actual.getConfiguration().getScaler())
            .isEqualTo(FIXTURE.getConfiguration().getScaler());
    }

    void adds_null_as_neutral_value_impl(BinaryOperator<FluentBigDecimal> fnc) {
        FluentBigDecimal actual = fnc.apply(FIXTURE, null);

        assertThat(actual.getValue())
            .isEqualTo(FIXTURE.getValue());
    }


    void executes_biProjection_and_calls_scaler_impl(
        BiFunction<FluentBigDecimal, FluentBigDecimal, FluentBigDecimal> operation,
        BigDecimal srcValue,
        BigDecimal argument,
        BigDecimal expectedValue
    ) {
        // use ofRaw() because we do not want to test scaling on instantiation

        FluentBigDecimal fluentArgument = FIXTURE_CONFIG
            .withScaler(new DummyScaler())
            .ofRaw(argument);

        var scalerSpy = spy(FIXTURE_SCALER);
        FluentBigDecimal sut = FIXTURE_CONFIG
            .withScaler(scalerSpy)
            .ofRaw(srcValue);

        var actual = operation.apply(sut, fluentArgument);

        // cannot assert on any exact value since this value is intermediate and never shown anywhere.
        verify(scalerSpy)
            .scale(eq(expectedValue), eq(FIXTURE_MATH_CONTEXT));

        assertThat(actual.getValue())
            .isEqualTo(expectedValue);
    }

    void executes_monoProjection_and_calls_scaler_impl(
        Function<FluentBigDecimal, FluentBigDecimal> operation,
        BigDecimal srcValue,
        BigDecimal expectedValue
    ) {
        // use ofRaw() because we do not want to test scaling on instantiation

        var scaler = spy(FIXTURE_SCALER);
        FluentBigDecimal sut = FIXTURE_CONFIG
            .withScaler(scaler)
            .ofRaw(srcValue);

        var actual = operation.apply(sut);

        verify(scaler)
            .scale(eq(expectedValue), eq(FIXTURE_MATH_CONTEXT));

        assertThat(actual.getValue())
            .isEqualTo(expectedValue);
    }

}
