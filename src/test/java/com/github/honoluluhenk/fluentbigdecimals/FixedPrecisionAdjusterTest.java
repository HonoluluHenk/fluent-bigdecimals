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

import static java.math.RoundingMode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
// we want to test runtime null-checks:
@SuppressWarnings("argument.type.incompatible")
class FixedPrecisionAdjusterTest {

    @Nested
    class From {
        @Test
        void passes_values_to_getters() {
            FixedPrecisionAdjuster ctx = FixedPrecisionAdjuster.from(10, 7, HALF_UP);

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
                () -> FixedPrecisionAdjuster.from(precision, 2, HALF_UP)
            );

            assertThat(ex)
                .hasMessageContaining(expectedMessage);
        }

        @ParameterizedTest
        @CsvSource({
            "1, 1, 2",
            "1, 2, 3",
            "99, 99, 198",
            "99, 100, 199",
        })
        void computes_precision_if_scale_gt_precision(int precision, int maxScale, int expectedPrecision) {
            FixedPrecisionAdjuster actual = FixedPrecisionAdjuster.from(precision, maxScale, HALF_UP);

            assertThat(actual.getPrecision())
                .isEqualTo(expectedPrecision);

            assertThat(actual.getMaxScale())
                .isEqualTo(maxScale);
        }

        @Test
        void throws_on_null_RoundingMode() {
            NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> FixedPrecisionAdjuster.from(3, 2, null)
            );

            assertThat(ex)
                .hasMessage("roundingMode required");
        }
    }

    @Nested
    class FromBigDecimal {
        @Test
        void throws_on_null_srcValue() {
            NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> FixedPrecisionAdjuster.from(null, RoundingMode.CEILING)
            );

            assertThat(ex)
                .hasMessage("srcValue required");
        }

        @Test
        void throws_on_null_RoundingMode() {
            NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> FixedPrecisionAdjuster.from(BigDecimal.ONE, null)
            );

            assertThat(ex)
                .hasMessage("roundingMode required");
        }

        @Test
        void passes_values_to_getters() {
            var actual = FixedPrecisionAdjuster.from(new BigDecimal("42.123"), RoundingMode.FLOOR);

            assertThat(actual.getPrecision())
                .isEqualTo(5);

            assertThat(actual.getMaxScale())
                .isEqualTo(3);

            assertThat(actual.getRoundingMode())
                .isEqualTo(FLOOR);
        }

    }

    @Nested
    class FromWithDefault {
        @Test
        void sets_HALF_UP() {
            FixedPrecisionAdjuster actual = FixedPrecisionAdjuster.from(12, 3);

            assertThat(actual.getRoundingMode())
                .isEqualTo(HALF_UP);
        }
    }

    @Test
    void copy_factory_produces_same_properties() {
        FixedPrecisionAdjuster ctx = FixedPrecisionAdjuster.from(5, 2, DOWN);

        FixedPrecisionAdjuster actual = FixedPrecisionAdjuster.from(ctx);
        assertValues(actual, 5, 2, DOWN);
    }


    @Nested
    class WithPrecision {
        private final FixedPrecisionAdjuster ctx = FixedPrecisionAdjuster.from(5, 2, DOWN);
        private final FixedPrecisionAdjuster actual = ctx.withPrecision(8);

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
        private final FixedPrecisionAdjuster ctx = FixedPrecisionAdjuster.from(5, 2, DOWN);
        private final FixedPrecisionAdjuster actual = ctx.withMaxScale(3);

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
        private final FixedPrecisionAdjuster ctx = FixedPrecisionAdjuster.from(5, 2, DOWN);
        private final FixedPrecisionAdjuster actual = ctx.withRoundingMode(HALF_UP);

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
            FixedPrecisionAdjuster ctx = FixedPrecisionAdjuster.from(10, 7, HALF_UP);

            BigDecimalExt actual = ctx.withValue(BigDecimal.ONE);

            assertThat(actual.getAdjuster())
                .isSameAs(ctx);
        }
    }

    static void assertValues(FixedPrecisionAdjuster ctx, int precision, int maxScale, RoundingMode roundingMode) {
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
            FixedPrecisionAdjuster a = FixedPrecisionAdjuster.from(5, 1, HALF_UP);
            FixedPrecisionAdjuster b = FixedPrecisionAdjuster.from(5, 1, HALF_UP);

            assertThat(a)
                .isEqualTo(b);

            assertThat(a.hashCode())
                .isEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_precision() {
            FixedPrecisionAdjuster a = FixedPrecisionAdjuster.from(5, 1, HALF_UP);
            FixedPrecisionAdjuster b = FixedPrecisionAdjuster.from(9, 1, HALF_UP);

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_scale() {
            FixedPrecisionAdjuster a = FixedPrecisionAdjuster.from(5, 1, HALF_UP);
            FixedPrecisionAdjuster b = FixedPrecisionAdjuster.from(5, 2, HALF_UP);

            assertThat(a)
                .isNotEqualTo(b);

            assertThat(a.hashCode())
                .isNotEqualTo(b.hashCode());
        }

        @Test
        void differs_on_different_rounding() {
            FixedPrecisionAdjuster a = FixedPrecisionAdjuster.from(5, 1, HALF_UP);
            FixedPrecisionAdjuster b = FixedPrecisionAdjuster.from(5, 1, DOWN);

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
            var actual = FixedPrecisionAdjuster.from(5, 1, HALF_UP).toString();

            assertThat(actual)
                .isEqualTo("FixedPrecisionAdjuster[5,1,HALF_UP]");
        }
    }
}
