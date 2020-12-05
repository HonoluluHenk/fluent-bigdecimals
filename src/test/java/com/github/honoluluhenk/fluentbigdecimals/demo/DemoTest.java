package com.github.honoluluhenk.fluentbigdecimals.demo;

import com.github.honoluluhenk.fluentbigdecimals.BigDecimalFactory;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxPrecisionScaler;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DemoTest {

    public static MathContext DEFAULT_MATH_CONTEXT = new MathContext(7, RoundingMode.HALF_UP);
    public static BigDecimalFactory DEFAULT = BigDecimalFactory.factory(DEFAULT_MATH_CONTEXT, new MaxPrecisionScaler());
    public static BigDecimalFactory DATABASE = BigDecimalFactory.jpaBigDecimal();

    @Nested
    class ApiDemo {
        @Test
        void step_by_step() {

            // start from factory
            var a = DEFAULT.of("12.3456789");
            // please note: no implicit rounding on creation
            assertEquals(new BigDecimal("12.3456789"), a.getValue());

            // explicit rounding
            var b = a.round();
            assertEquals(new BigDecimal("12.34568"), b.getValue());

            // some math operation
            var c = b.add(new BigDecimal("54.555555"));
            // intermediate result: 66.901235 and then rounded
            assertEquals(new BigDecimal("66.90124"), c.getValue());

            // continue with different scaler
            var x = c.roundInto(DATABASE); // = 592.54 (database only allows two decimals
            assertEquals(new BigDecimal("66.90"), x.getValue());

            // still on the other scaler
            var y = x.multiply(new BigDecimal("123.99999"));
            // intermediate result: 8295.599331
            assertEquals(new BigDecimal("8295.60"), y.getValue());

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
