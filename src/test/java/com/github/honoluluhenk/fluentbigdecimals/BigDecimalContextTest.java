package com.github.honoluluhenk.fluentbigdecimals;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
// we want to test runtime null-checks:
@SuppressWarnings("argument.type.incompatible")
class BigDecimalContextTest {

    @Nested
    class From {
        @Test
        void passes_values_to_getters() {
            BigDecimalContext ctx = BigDecimalContext.from(10, 7, HALF_UP);

            assertValues(ctx, 10, 7, HALF_UP);

            assertThat(ctx.getMathContext())
                .isEqualTo(new MathContext(10, HALF_UP));
        }

        @ParameterizedTest
        @CsvSource({
            "0, but was: 0",
            "-99, but was: -99",
        })
        void throws_on_precision_lte_1(int precision, String expectedMessage) {
            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> BigDecimalContext.from(precision, 2, HALF_UP)
            );

            assertThat(ex)
                .hasMessageContaining(expectedMessage);
        }

        @ParameterizedTest
        @CsvSource({
            "1, 1, but was: 1 < 1",
            "1, 2, but was: 2 < 1",
            "99, 99, but was: 99 < 99",
            "99, 100, but was: 100 < 99",
        })
        void throws_on_precision_lte_scale(int precision, int maxScale, String expectedMessage) {
            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> BigDecimalContext.from(precision, maxScale, HALF_UP)
            );

            assertThat(ex)
                .hasMessageContaining("Scale")
                .hasMessageContaining(expectedMessage);
        }

        @Test
        void throws_on_null_RoundingMode() {
            NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> BigDecimalContext.from(3, 2, null)
            );

            assertThat(ex)
                .hasMessage("RoundingMode required");
        }
    }

    @Nested
    class FromWithDefault {
        @Test
        void sets_HALF_UP() {
            BigDecimalContext actual = BigDecimalContext.from(12, 3);

            assertThat(actual.getRoundingMode())
                .isEqualTo(HALF_UP);
        }
    }

    @Test
    void copy_factory_produces_same_properties() {
        BigDecimalContext ctx = BigDecimalContext.from(5, 2, DOWN);

        BigDecimalContext actual = BigDecimalContext.from(ctx);
        assertValues(actual, 5, 2, DOWN);
    }


    @Nested
    class WithPrecision {
        private final BigDecimalContext ctx = BigDecimalContext.from(5, 2, DOWN);
        private final BigDecimalContext actual = ctx.withPrecision(8);

        @Test
        void updates_only_precision() {
            assertValues(actual, 8, 2, DOWN);
        }

        @Test
        void keeps_original_values_unchanged() {
            assertValues(ctx, 5, 2, DOWN);
        }

        @Test
        void creates_new_instance() {
            assertThat(actual)
                .isNotSameAs(ctx);
        }
    }

    @Nested
    class WithMaxScale {
        private final BigDecimalContext ctx = BigDecimalContext.from(5, 2, DOWN);
        private final BigDecimalContext actual = ctx.withMaxScale(3);

        @Test
        void updates_only_maxScale() {
            assertValues(actual, 5, 3, DOWN);
        }

        @Test
        void keeps_original_values_unchanged() {
            assertValues(ctx, 5, 2, DOWN);
        }

        @Test
        void creates_new_instance() {
            assertThat(actual)
                .isNotSameAs(ctx);
        }
    }

    @Nested
    class WitRoundingMode {
        private final BigDecimalContext ctx = BigDecimalContext.from(5, 2, DOWN);
        private final BigDecimalContext actual = ctx.withRoundingMode(HALF_UP);

        @Test
        void updates_only_maxScale() {
            assertValues(actual, 5, 2, HALF_UP);
        }

        @Test
        void keeps_original_values_unchanged() {
            assertValues(ctx, 5, 2, DOWN);
        }

        @Test
        void creates_new_instance() {
            assertThat(actual)
                .isNotSameAs(ctx);
        }
    }

    @Nested
    class WithValue {
        @Test
        void passes_Context_to_Ext_instance() {
            BigDecimalContext ctx = BigDecimalContext.from(10, 7, HALF_UP);

            BigDecimalExt actual = ctx.withValue(BigDecimal.ONE);

            assertThat(actual.getContext())
                .isSameAs(ctx);
        }
    }

    static void assertValues(BigDecimalContext ctx, int precision, int maxScale, RoundingMode roundingMode) {
        assertThat(ctx.getPrecision())
            .isEqualTo(precision);

        assertThat(ctx.getMaxScale())
            .isEqualTo(maxScale);

        assertThat(ctx.getRoundingMode())
            .isEqualTo(roundingMode);
    }

    @Nested
    class HashCodeEquals {

        @Test
        void equals_for_same_values() {
            BigDecimalContext a = BigDecimalContext.from(5, 1, HALF_UP);
            BigDecimalContext b = BigDecimalContext.from(5, 1, HALF_UP);

            assertThat(a)
                .isEqualTo(b);

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_precision() {
            BigDecimalContext a = BigDecimalContext.from(5, 1, HALF_UP);
            BigDecimalContext b = BigDecimalContext.from(9, 1, HALF_UP);

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_scale() {
            BigDecimalContext a = BigDecimalContext.from(5, 1, HALF_UP);
            BigDecimalContext b = BigDecimalContext.from(5, 2, HALF_UP);

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_rounding() {
            BigDecimalContext a = BigDecimalContext.from(5, 1, HALF_UP);
            BigDecimalContext b = BigDecimalContext.from(5, 1, DOWN);

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }
    }

    @Nested
    class ToString {
        @Test
        void includes_all_params() {
            var actual = BigDecimalContext.from(5, 1, HALF_UP).toString();

            assertThat(actual)
                .isEqualTo("[5,1,HALF_UP]");
        }
    }
}
