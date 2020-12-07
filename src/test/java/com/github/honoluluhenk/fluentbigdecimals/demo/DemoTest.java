package com.github.honoluluhenk.fluentbigdecimals.demo;

import com.github.honoluluhenk.fluentbigdecimals.BigDecimalFactory;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxPrecisionScaler;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DemoTest {

    public static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(7, HALF_UP);
    public static final BigDecimalFactory DEFAULT = BigDecimalFactory.factory(DEFAULT_MATH_CONTEXT, new MaxPrecisionScaler());

    public static final MathContext DATABASE_MATH_CONTEXT = new MathContext(18, HALF_UP);
    public static final int DATABASE_MAX_SCALE = 2;
    public static final BigDecimalFactory DATABASE = BigDecimalFactory.jpaBigDecimal();

    @Nested
    class OldSchool {
        @Test
        void using_bigdcimals() {

            var a = new BigDecimal("12.3456789");
            assertThat(a).isEqualTo("12.3456789");

            // explicit rounding
            var b = a.round(DEFAULT_MATH_CONTEXT);
            assertThat(b).isEqualTo("12.34568");

            // some math operation
            var c = b.add(new BigDecimal("54.555555"), DEFAULT_MATH_CONTEXT);
            // intermediate result: 66.901235 and then rounded
            assertThat(c).isEqualTo("66.90124");

            // continue with different scaler
            var x = c.round(DATABASE_MATH_CONTEXT) // remember: needs scaling
                .setScale(DATABASE_MAX_SCALE, DATABASE_MATH_CONTEXT.getRoundingMode());

            assertThat(x).isEqualTo("66.90");

            var y = x.multiply(new BigDecimal("123.99999"), DATABASE_MATH_CONTEXT)
                .setScale(DATABASE_MAX_SCALE, DATABASE_MATH_CONTEXT.getRoundingMode());
            // intermediate result: 8295.599331
            assertThat(y).isEqualTo("8295.60");

            // finally...
            var result = y;
            assertEquals("8295.60", result.toPlainString());
        }

    }

    @Nested
    class ApiDemo {
        @Test
        void step_by_step() {

            // start from factory
            var a = DEFAULT.of("12.3456789");
            // please note: no implicit rounding on creation
            assertThat(a.getValue())
                .isEqualTo("12.3456789");

            // explicit rounding
            var b = a.round();
            assertThat(b.getValue())
                .isEqualTo("12.34568");

            // some math operation
            var c = b.add(new BigDecimal("54.555555"));
            // intermediate result: 66.901235 and then rounded
            assertThat(c.getValue())
                .isEqualTo("66.90124");

            // continue with different scaler
            var x = c.roundInto(DATABASE); // = 592.54 (database only allows two decimals
            assertThat(x.getValue())
                .isEqualTo("66.90");

            // still on the other scaler
            var y = x.multiply(new BigDecimal("123.99999"));
            // intermediate result: 8295.599331
            assertThat(y.getValue())
                .isEqualTo("8295.60");

            // finally get the actual BigDecimal
            var result = y.getValue();

            assertEquals("8295.60", result.toPlainString());
        }

        @Test
        void using_the_nice_fluent_api() {

            var result = DEFAULT.of("12.3456789")
                .round()
                .add(new BigDecimal("54.555555"))
                .roundInto(DATABASE)
                .multiply(new BigDecimal("123.99999"))
                .getValue();

            assertEquals("8295.60", result.toPlainString());
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
