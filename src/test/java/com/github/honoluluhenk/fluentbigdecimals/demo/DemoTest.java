package com.github.honoluluhenk.fluentbigdecimals.demo;

import com.github.honoluluhenk.fluentbigdecimals.BigDecimalConfiguration;
import com.github.honoluluhenk.fluentbigdecimals.FluentBigDecimal;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxPrecisionScaler;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;

public class DemoTest {

    public static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(7, HALF_UP);
    // some custom configuration
    public static final BigDecimalConfiguration DEFAULT = BigDecimalConfiguration.create(DEFAULT_MATH_CONTEXT, new MaxPrecisionScaler());

    public static final MathContext DATABASE_MATH_CONTEXT = new MathContext(18, HALF_UP);
    public static final int DATABASE_MAX_SCALE = 2;
    public static final BigDecimalConfiguration DATABASE = BigDecimalConfiguration.jpaBigDecimal();

    public static final BigDecimalConfiguration DEMO_CONFIG = BigDecimalConfiguration.currency(10, 2);

    @Nested
    class OldSchool {
        @Test
        public void usingBigDecimalsStepByStep() {

            BigDecimal a = new BigDecimal("12.3456789");
            assertThat(a).isEqualTo("12.3456789");

            // explicit rounding
            BigDecimal b = a.round(DEFAULT_MATH_CONTEXT);
            assertThat(b).isEqualTo("12.34568");

            // some math operation
            BigDecimal c = b.add(new BigDecimal("54.555555"), DEFAULT_MATH_CONTEXT);
            // intermediate result: 66.901235 and then rounded
            assertThat(c).isEqualTo("66.90124");

            // continue with different scaler
            BigDecimal x = c.round(DATABASE_MATH_CONTEXT) // remember: needs scaling
                .setScale(DATABASE_MAX_SCALE, DATABASE_MATH_CONTEXT.getRoundingMode());

            assertThat(x).isEqualTo("66.90");

            BigDecimal y = x.multiply(new BigDecimal("123.99999"), DATABASE_MATH_CONTEXT)
                .setScale(DATABASE_MAX_SCALE, DATABASE_MATH_CONTEXT.getRoundingMode());
            // intermediate result: 8295.599331
            assertThat(y).isEqualTo("8295.60");

            // finally...
            BigDecimal result = y;
            assertThat(result).isEqualTo("8295.60");

            // return result;
        }

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
        public void fluentStepByStep() {

            // start from factory
            FluentBigDecimal a = DEFAULT.of("12.3456789");
            // please note: implicit rounding on creation
            assertThat(a.getValue())
                .isEqualTo("12.34568");

            // explicit rounding not necessary
            FluentBigDecimal b = a;

            // some math operation
            FluentBigDecimal c = b.add(new BigDecimal("54.555555"));
            // intermediate result: 66.901235 and then rounded
            assertThat(c.getValue())
                .isEqualTo("66.90124");

            // continue with different scaler
            FluentBigDecimal x = c.roundInto(DATABASE); // = 592.54 (database only allows two decimals
            assertThat(x.getValue())
                .isEqualTo("66.90");

            // still on the other scaler
            FluentBigDecimal y = x.multiply(new BigDecimal("123.99999"));
            // intermediate result: 8295.599331
            assertThat(y.getValue())
                .isEqualTo("8295.60");

            // finally...
            BigDecimal result = y.getValue();
            assertThat(result).isEqualTo("8295.60");

            // return result;
        }

        @Test
        public void fluentCompact() {

            // after each step: round/scale according to currrent configuration
            BigDecimal result = DEFAULT.of("12.3456789")
                .add(new BigDecimal("54.555555"))
                // continue with other configuration
                .roundInto(DATABASE)
                .multiply(new BigDecimal("123.99999"))
                .getValue();

            // return result;
            assertThat(result).isEqualTo("8295.60");
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

        private void roundIntoDemo() {
            FluentBigDecimal result = DEFAULT.of("12345678.90")
                .withConfiguration(DATABASE);
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
