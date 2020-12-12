package com.github.honoluluhenk.fluentbigdecimals.demo;

import com.github.honoluluhenk.fluentbigdecimals.*;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxScaleScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({"unused"})
public class DemoTest {

    public static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(7, HALF_UP);
    // some custom configuration to your liking (here: just rounding when calling the operation but no scaling)
    public static final Configuration<FluentBigDecimal> DEFAULT = ConfigurationFactory
        .create(DEFAULT_MATH_CONTEXT, new NopScaler());

    public static final MathContext DATABASE_MATH_CONTEXT = new MathContext(18, HALF_UP);
    public static final int DATABASE_MAX_SCALE = 2;
    // predefined: round/scale in a database compatible way.
    public static final Configuration<FluentBigDecimal> DATABASE = ConfigurationFactory.jpaBigDecimal();

    @Nested
    class OldSchool {
        @Test
        public void usingBigDecimalsCompact() {

            BigDecimal result = new BigDecimal("12.3456789")
                .round(DEFAULT_MATH_CONTEXT)
                .add(new BigDecimal("54.555555"), DEFAULT_MATH_CONTEXT)
                .round(DATABASE_MATH_CONTEXT)
                .setScale(DATABASE_MAX_SCALE, DATABASE_MATH_CONTEXT.getRoundingMode())
                .multiply(new BigDecimal("123.99999"), DATABASE_MATH_CONTEXT)
                .setScale(DATABASE_MAX_SCALE, DATABASE_MATH_CONTEXT.getRoundingMode());

            assertThat(result).isEqualTo("8295.60");

            // return result;
        }

    }

    @Nested
    class ApiDemo {

        @Test
        public void showcase() {

            // after each step: automatic rounding/scaling according to current configuration
            BigDecimal result = DEFAULT.of("12.3456789")
                .add("54.555555")
                // currently supports BigDecimal, String and other FluentBigDecimals on all implemented operations
                .multiply("42.23")
                .divide(new BigDecimal("555.5"))
                .subtract(DEFAULT.of("99"))
                // continue with other configuration
                .roundInto(DATABASE)
                .multiply("123.99999")
                .getValue();

            // return result;
            assertThat(result)
                .isEqualTo("-11644.84");
        }

        @Test
        public void usingFancyOperators() {
            // call your own operators (or BigDecimal operations not yet directly implemented by fluent-bigdecimals)
            FluentBigDecimal result = DEFAULT.of("12.3456789")
                .apply(this::myFancyOperation, 42);

            // operators returning other values thant BigDecimal
            int signum = DEFAULT.of("12.3456789")
                .map(BigDecimal::signum);
        }

        public void customOperationsWithOneArgument() {
            FluentBigDecimal result = DATABASE.of("12345678.90")
                .apply(BigDecimal::divideToIntegralValue, new BigDecimal("42"))
                .apply(this::myFancyOperation, 42);
        }

        public BigDecimal myFancyOperation(BigDecimal value, int argument, MathContext mc) {
            // just a simple simulation
            BigDecimal result = value.add(BigDecimal.valueOf(argument));

            return result;
        }

        public void customOperationsWithAnyArgument(Object someParam) {
            FluentBigDecimal result = DATABASE.of("12345678.90")
                .apply((value, mathContext) -> doStuffWith(value, someParam));

            // return result;
        }

        private BigDecimal doStuffWith(BigDecimal value, Object whatever) {
            return value.add(new BigDecimal(whatever.hashCode()));
        }

        public void mapping() {
            double result = DATABASE.of("12345678.90")
                .map(BigDecimal::doubleValue);
        }

        @Test
        public void roundIntoDemo() {
            FluentBigDecimal result = DEFAULT.of("12345678.90")
                .add("999.999999")
                .roundInto(DATABASE) // exact result: 124456789.899999, gets rounded to (124456789.90).
                // now we continue with DATABASE precision/scaling
                .multiply("12.3456");

            assertThat(result.getValue())
                .isEqualTo("152427172.61");
        }

        @Test
        public void withConfiguration() {
            FluentBigDecimal result = DEFAULT.of("12345678.90")
                .add("999.999999")
                .withConfiguration(DATABASE) // exact result: 124456789.899999, does *not* get rounded here
                // ... but on the next step:
                .multiply("12.3456");

            assertThat(result.getValue())
                .isEqualTo("152427172.61");
        }
    }

    @Nested
    class WithDemo {
        private final ScalingConfiguration<FluentBigDecimal> SWISS_CASH = ConfigurationFactory
            .monetary(20)
            .withScale(10);

    }

    @Nested
    class CashRoundingDemo {
        private final Configuration<FluentBigDecimal> SWISS_CASH = ConfigurationFactory
            .cashRounding(20, CashRoundingUnits.ROUND_DOT05);

        private final Configuration<FluentBigDecimal> HIGH_PRECISION = ConfigurationFactory
            .create(20, HALF_UP, MaxScaleScaler.of(10));

        @Test
        void roundIntoCash() {
            // start off with some high precision calculations
            FluentBigDecimal cash = HIGH_PRECISION.of("12345.67890")
                .multiply("3")
                // intermediate result: 37037.03670
                .roundInto(SWISS_CASH);

            assertThat(cash.getValue())
                .isEqualTo("37037.05");
        }
    }

    @Nested
    class MonetaryDemo {
        private final ScalingConfiguration<FluentBigDecimal> STOCK_DEPOT = ConfigurationFactory
            .monetary(20);

        @Test
        void myLife() {
            var currentStockBalance = STOCK_DEPOT.of("0.00")
                .add("42.23")
                .multiply("5.333333") // = 225.22665259 rounded to 225.23
                .getValue();

            assertThat(currentStockBalance)
                .isEqualTo("225.23");
        }
    }

    //    @Nested
//    @Disabled
//        // FIXME: implement excel behavior correctly
//    class Excel {
//
//        @Test
//        void excel_works_with_precision_15_by_default() {
//            var value = EXCEL.of("1234567890.1234567890")
//                .round();
//
//            assertEquals("1234567890.12346", value.getValue().toPlainString());
//        }
//
//        @Nested
//        class Excel_specific_rounding_errors {
//            @Test
//            void fail1() {
//
//                BigDecimal x = BigDecimal.valueOf(1.0d + (1.0d / 9000.0d) - 1.0d);
//                System.out.println(x);
//                int newScale = 15 - x.precision() + x.scale();
//                BigDecimal significant = x.setScale(newScale, RoundingMode.HALF_UP);
//                System.out.println(significant);
//
//
//                var one = EXCEL.of(BigDecimal.ONE);
//                var ninek = EXCEL.of("9000");
//                var oneBy9k = one
//                    .divide(ninek);
//                var actual = one.add(oneBy9k)
//                    .subtract(one);
//
//
//                assertThat(actual.getValue().toPlainString())
//                    .isEqualTo("0.000111111111111173");
//
//            }
//        }
//    }

}
