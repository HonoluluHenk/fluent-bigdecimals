package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.NonNull;
import lombok.var;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
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
@SuppressWarnings({"nullable", "argument.type.incompatible", "return.type.incompatible", "initialization.fields.uninitialized"})
class FluentBigDecimalTest {

    //<editor-fold defaultstate="collapsed" desc="FixedValueScaler">
    private static class FixedValueScaler implements Scaler {
        private static final long serialVersionUID = 5460135929536529147L;
        private final BigDecimal fixedValue;

        public FixedValueScaler(BigDecimal fixedValue) {
            this.fixedValue = fixedValue;
        }

        @Override
        public @NonNull BigDecimal scale(@NonNull BigDecimal value, @NonNull MathContext mathContext) {
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

        @Override
        public String toString() {
            return "DummyScaler";
        }
    }
    //</editor-fold>

    private static final BigDecimal FIXTURE_VALUE = new BigDecimal("123.45");
    private static final MathContext FIXTURE_MATH_CONTEXT = new MathContext(5, HALF_UP);
    private static final Scaler FIXTURE_SCALER = new NopScaler();
    private static final Configuration<FluentBigDecimal> FIXTURE_CONFIG = new Configuration<>(
        FIXTURE_MATH_CONTEXT,
        FIXTURE_SCALER,
        FluentBigDecimal::new
    );
    private static final FluentBigDecimal FIXTURE = FIXTURE_CONFIG.of(FIXTURE_VALUE);

    //<editor-fold defaultstate="collapsed" desc="TestSetup">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructor">
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
            @SuppressWarnings("ConstantConditions")
            var ex = assertThrows(
                NullPointerException.class,
                () -> new FluentBigDecimal(null, FIXTURE_CONFIG)
            );

            assertThat(ex)
                .hasMessageContaining("value");
        }

        @Test
        void throws_for_null_configuration() {
            @SuppressWarnings("ConstantConditions")
            var ex = assertThrows(
                NullPointerException.class,
                () -> new FluentBigDecimal(BigDecimal.ONE, null)
            );

            assertThat(ex)
                .hasMessageContaining("configuration required");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="HashCodeEquals">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CompareTo">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CompareToBigDecimal">
    @Nested
    class CompareToBigDecimal {
        @Test
        void compares_only_using_value() {

            FluentBigDecimal sut = FIXTURE_CONFIG.of("123.45");
            BigDecimal other = new BigDecimal("123.45");

            assertThat(sut.compareTo(other))
                .isEqualTo(0);
        }

        @Test
        void differs_for_a_lt_b() {

            FluentBigDecimal sut = FIXTURE_CONFIG.of("123.45");
            BigDecimal other = new BigDecimal("543.21");

            assertThat(sut.compareTo(other))
                .isLessThan(0);
        }

        @Test
        void differs_for_b_lt_a() {

            FluentBigDecimal sut = FIXTURE_CONFIG.of("543.21");
            BigDecimal other = new BigDecimal("123.45");

            assertThat(sut.compareTo(other))
                .isGreaterThan(0);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="WithValueTest">
    @Nested
    class WithValueTest {

        @Test
        void replaces_value() {
            FluentBigDecimal sut = FIXTURE_CONFIG.of("543.21");

            FluentBigDecimal actual = sut
                .withValue(new BigDecimal("123.45"));

            assertThat(actual.getValue())
                .isEqualTo("123.45");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ToPlainStringTest">
    @Nested
    class ToPlainStringTest {
        @Test
        void returns_the_same_value_as_bigdecimal() {
            BigDecimal bd = new BigDecimal("543.21");
            FluentBigDecimal actual = FIXTURE_CONFIG.of(bd);

            assertThat(actual.toPlainString())
                .isEqualTo(bd.toPlainString());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ToEngineeringStringTest">
    @Nested
    class ToEngineeringStringTest {
        @Test
        void returns_the_same_value_as_bigdecimal() {
            BigDecimal bd = new BigDecimal("543.21");
            FluentBigDecimal actual = FIXTURE_CONFIG.of(bd);

            assertThat(actual.toEngineeringString())
                .isEqualTo(bd.toEngineeringString());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ToBigIntegerExactTest">
    @Nested
    class ToBigIntegerExactTest {
        @Test
        void returns_the_same_value_as_bigdecimal() {
            BigDecimal bd = new BigDecimal("543");
            FluentBigDecimal actual = FIXTURE_CONFIG.of(bd);

            assertThat(actual.toBigIntegerExact())
                .isEqualTo(bd.toBigIntegerExact());
        }

        @Test
        void throws_the_same_as_bigdecimal() {
            BigDecimal bd = new BigDecimal("543.21");
            FluentBigDecimal actual = FIXTURE_CONFIG.of(bd);

            var thrown = assertThrows(
                ArithmeticException.class,
                () -> actual.toBigIntegerExact()
            );

            //noinspection ResultOfMethodCallIgnored
            var thrownBD = assertThrows(
                ArithmeticException.class,
                () -> bd.toBigIntegerExact()
            );

            assertThat(thrown)
                .hasSameClassAs(thrownBD);

            assertThat(thrown.getMessage())
                .isEqualTo(thrownBD.getMessage());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ToBigIntegerTest">
    @Nested
    class ToBigIntegerTest {
        @Test
        void returns_the_same_value_as_bigdecimal() {
            BigDecimal bd = new BigDecimal("543.21");
            FluentBigDecimal actual = FIXTURE_CONFIG.of(bd);

            assertThat(actual.toBigInteger())
                .isEqualTo(bd.toBigInteger());
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ToString">
    @Nested
    class ToString {
        @Test
        void includes_all_parameters() {
            String actual = FIXTURE
                .withScaler(new DummyScaler()) // has a stable toString()
                .toString();

            assertThat(actual)
                .isEqualTo("FluentBigDecimal[123.45,[5,HALF_UP,DummyScaler]]");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Round">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="RoundInto">
    @Nested
    class RoundInto {
        @Test
        void rounds_and_sets_other_configuration() {
            Scaler otherScaler = (value, mc) -> value.setScale(0, DOWN);
            MathContext otherMathContext = new MathContext(32, UP);
            Configuration<FluentBigDecimal> otherConfiguration = ConfigurationFactory.create(
                otherMathContext,
                otherScaler
            );

            FluentBigDecimal actual = FIXTURE.roundInto(otherConfiguration);

            assertThat(actual.getValue())
                .isEqualTo("123");
            assertThat(actual.getConfiguration())
                .isEqualTo(otherConfiguration);
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Add">
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

        @Test
        void all_overloads_add() {
            var expected = new BigDecimal("146.90");

            assertThat(FIXTURE.add("23.45")).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.add(FIXTURE_CONFIG.of("23.45"))).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.add(new BigDecimal("23.45"))).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.add(23.45d)).isEqualTo(FIXTURE_CONFIG.of(expected));

            assertThat(FIXTURE.add(23L)).isEqualTo(FIXTURE_CONFIG.of("146.45"));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Subtract">
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

        @Test
        void all_overloads_subtract() {
            var expected = new BigDecimal("100.00");

            assertThat(FIXTURE.subtract("23.45")).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.subtract(FIXTURE_CONFIG.of("23.45"))).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.subtract(new BigDecimal("23.45"))).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.subtract(23.45d)).isEqualTo(FIXTURE_CONFIG.of(expected));

            assertThat(FIXTURE.subtract(23L)).isEqualTo(FIXTURE_CONFIG.of("100.45"));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Multiply">
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
            executes_biProjection_and_calls_scaler_impl(
                FluentBigDecimal::multiply,
                multiplicand,
                multiplicator,
                expectedValue
            );
        }

        @Test
        void all_overloads_multiply() {
            var expected = new BigDecimal("15239.9025");

            assertThat(FIXTURE.multiply("123.45")).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.multiply(FIXTURE_CONFIG.of("123.45"))).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.multiply(new BigDecimal("123.45"))).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.multiply(123.45d)).isEqualTo(FIXTURE_CONFIG.of(expected));

            assertThat(FIXTURE.multiply(123L)).isEqualTo(FIXTURE_CONFIG.of("15184"));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Divide">
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

        @Test
        void all_overloads_divide() {
            var expected = new BigDecimal("61.725");

            assertThat(FIXTURE.divide("2.00")).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.divide(FIXTURE_CONFIG.of("2.00"))).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.divide(new BigDecimal("2.00"))).isEqualTo(FIXTURE_CONFIG.of(expected));
            assertThat(FIXTURE.divide(2.00d)).isEqualTo(FIXTURE_CONFIG.of(expected));

            assertThat(FIXTURE.divide(2L)).isEqualTo(FIXTURE_CONFIG.of("61.725"));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PctToFraction">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="FractionToPct">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Map">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Apply">
    @Nested
    class Apply {
        @SuppressWarnings("ConstantConditions")
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
                @SuppressWarnings("ConstantConditions")
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
                @SuppressWarnings("ConstantConditions")
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ComparesTo">
    @Nested
    class ComparesTo {
        @Nested
        class OtherFluentBigDecimal {
            @Test
            void returns_true_if_equal() {
                boolean actual = FIXTURE_CONFIG.of("123.45")
                    .comparesTo(FIXTURE_CONFIG.of("123.45"));

                assertThat(actual)
                    .isTrue();
            }

            @Test
            void returns_true_for_equal_value_but_different_scale() {
                boolean actual = FIXTURE_CONFIG.of("123.45")
                    .comparesTo(FIXTURE_CONFIG.of("123.4500"));

                assertThat(actual)
                    .isTrue();
            }

            @Test
            void returns_false_for_different_value_with_same_scale() {
                boolean actual = FIXTURE_CONFIG.of("123.45")
                    .comparesTo(FIXTURE_CONFIG.of("999.99"));

                assertThat(actual)
                    .isFalse();
            }
        }

        @Nested
        class OtherBigDecimal {
            @Test
            void returns_true_if_equal() {
                boolean actual = FIXTURE_CONFIG.of("123.45")
                    .comparesTo(new BigDecimal("123.45"));

                assertThat(actual)
                    .isTrue();
            }

            @Test
            void returns_true_for_equal_value_but_different_scale() {
                boolean actual = FIXTURE_CONFIG.of("123.45")
                    .comparesTo(new BigDecimal("123.4500"));

                assertThat(actual)
                    .isTrue();
            }

            @Test
            void returns_false_for_different_value_with_same_scale() {
                boolean actual = FIXTURE_CONFIG.of("123.45")
                    .comparesTo(new BigDecimal("999.99"));

                assertThat(actual)
                    .isFalse();
            }
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IsZero">
    @Nested
    class IsZero {
        @Test
        void returns_true_if_BigDecimalZERO() {
            boolean actual = FIXTURE_CONFIG.of(BigDecimal.ZERO)
                .isZero();

            assertThat(actual)
                .isTrue();
        }

        @Test
        void returns_true_if_ConfigurationZERO() {
            boolean actual = FIXTURE_CONFIG.ZERO()
                .isZero();

            assertThat(actual)
                .isTrue();
        }

        @Test
        void returns_true_if_0() {
            boolean actual = FIXTURE_CONFIG.of("0")
                .isZero();

            assertThat(actual)
                .isTrue();
        }

        @Test
        void returns_true_for_zeroes_with_scale() {
            boolean actual = FIXTURE_CONFIG.of("000.00")
                .isZero();

            assertThat(actual)
                .isTrue();
        }

        @Test
        void returns_false_for_different_value_with_same_scale() {
            boolean actual = FIXTURE_CONFIG.of("123.45")
                .isZero();

            assertThat(actual)
                .isFalse();
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ComparesTo">
    @Nested
    class PseudoConstants {

        @Test
        void ZERO_returns_ZERO() {
            assertThat(FIXTURE_CONFIG.ZERO())
                .isEqualByComparingTo(FIXTURE_CONFIG.of(BigDecimal.ZERO));
        }

        @Test
        void ZERO_returns_same_instance_every_time() {
            assertThat(FIXTURE_CONFIG.ZERO())
                .isSameAs(FIXTURE_CONFIG.ZERO());
        }

        @Test
        void ONE_returns_ONE() {
            assertThat(FIXTURE_CONFIG.ONE())
                .isEqualByComparingTo(FIXTURE_CONFIG.of(BigDecimal.ONE));
        }

        @Test
        void ONE_returns_same_instance_every_time() {
            assertThat(FIXTURE_CONFIG.ONE())
                .isSameAs(FIXTURE_CONFIG.ONE());
        }

        @Test
        void TEN_returns_TEN() {
            assertThat(FIXTURE_CONFIG.TEN())
                .isEqualByComparingTo(FIXTURE_CONFIG.of(BigDecimal.TEN));
        }

        @Test
        void TEN_returns_same_instance_every_time() {
            assertThat(FIXTURE_CONFIG.TEN())
                .isSameAs(FIXTURE_CONFIG.TEN());
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ExactGetters">
    @Nested
    class ExactGetters {

        @Nested
        class longValueExact {
            @Test
            void gets_desired_value() {
                long value = FIXTURE_CONFIG.of(123)
                    .longValueExact();

                assertThat(value)
                    .isEqualTo(123);
            }

            @Test
            void throws_if_rounding_necessary() {
                assertThrowsArithmeticException(() -> FIXTURE_CONFIG.of(123.45).longValueExact());
            }

        }

        @Nested
        class intValueExact {
            @Test
            void gets_desired_value() {
                int value = FIXTURE_CONFIG.of(123)
                    .intValueExact();

                assertThat(value)
                    .isEqualTo(123);
            }

            @Test
            void throws_if_rounding_necessary() {
                assertThrowsArithmeticException(() -> FIXTURE_CONFIG.of(123.45).intValueExact());
            }

        }

        @Nested
        class shortValueExact {
            @Test
            void gets_desired_value() {
                short value = FIXTURE_CONFIG.of(123)
                    .shortValueExact();

                assertThat(value)
                    .isEqualTo(Short.valueOf((short) 123).shortValue());
            }

            @Test
            void throws_if_rounding_necessary() {
                assertThrowsArithmeticException(() -> FIXTURE_CONFIG.of(123.45).shortValueExact());
            }

        }

        @Nested
        class byteValueExact {
            @Test
            void gets_desired_value() {
                byte value = FIXTURE_CONFIG.of(123)
                    .byteValueExact();

                assertThat(value)
                    .isEqualTo((byte) 123);
            }

            @Test
            void throws_if_rounding_necessary() {
                assertThrowsArithmeticException(() -> FIXTURE_CONFIG.of(123.45).byteValueExact());
            }

        }

        private void assertThrowsArithmeticException(Executable e) {
            ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                e
            );

            assertThat(ex)
                .hasMessage("Rounding necessary");
        }
    }

    //</editor-fold>

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
