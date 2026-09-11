package com.github.honoluluhenk.fluentbigdecimals.tests;

import com.github.honoluluhenk.fluentbigdecimals.Configuration;
import com.github.honoluluhenk.fluentbigdecimals.Factory;
import com.github.honoluluhenk.fluentbigdecimals.FluentBigDecimal;
import com.github.honoluluhenk.fluentbigdecimals.ScalingConfiguration;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxScaleScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.MathContext;

import static com.github.honoluluhenk.fluentbigdecimals.ConfigurationFactory.FLUENT_BIGDECIMAL_FACTORY;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScalingConfigurationTest {

    private static final MathContext MATH_CONTEXT = new MathContext(10, HALF_UP);
    private static final MaxScaleScaler SCALER = MaxScaleScaler.of(2);

    private final ScalingConfiguration<FluentBigDecimal> config = new ScalingConfiguration<>(
        MATH_CONTEXT,
        SCALER,
        FLUENT_BIGDECIMAL_FACTORY
    );

    @Nested
    class WithMathContext {

        @Test
        void returns_new_instance_with_only_mathContext_changed() {
            MathContext newMathContext = new MathContext(5, UP);

            ScalingConfiguration<FluentBigDecimal> actual = config.withMathContext(newMathContext);

            assertThat(actual)
                .isNotSameAs(config)
                .isInstanceOf(ScalingConfiguration.class);
            assertProperties(actual, newMathContext, SCALER, FLUENT_BIGDECIMAL_FACTORY);
        }
    }

    @Nested
    class WithScaler {

        @Test
        void returns_new_instance_with_only_scaler_changed() {
            Scaler newScaler = MaxScaleScaler.of(7);

            ScalingConfiguration<FluentBigDecimal> actual = config.withScaler(newScaler);

            assertThat(actual)
                .isNotSameAs(config)
                .isInstanceOf(ScalingConfiguration.class);
            assertProperties(actual, MATH_CONTEXT, newScaler, FLUENT_BIGDECIMAL_FACTORY);
        }
    }

    @Nested
    class WithScalingScaler {

        @Test
        void returns_new_instance_with_only_scaler_changed() {
            MaxScaleScaler newScaler = MaxScaleScaler.of(7);

            ScalingConfiguration<FluentBigDecimal> actual = config.withScalingScaler(newScaler);

            assertThat(actual)
                .isNotSameAs(config)
                .isInstanceOf(ScalingConfiguration.class);
            assertProperties(actual, MATH_CONTEXT, newScaler, FLUENT_BIGDECIMAL_FACTORY);
        }
    }

    @Nested
    class WithFactory {

        @Test
        void returns_new_instance_with_only_factory_changed() {
            Factory<FluentBigDecimal> newFactory = FluentBigDecimal::new;

            ScalingConfiguration<FluentBigDecimal> actual = config.withFactory(newFactory);

            assertThat(actual)
                .isNotSameAs(config)
                .isInstanceOf(ScalingConfiguration.class);
            assertProperties(actual, MATH_CONTEXT, SCALER, newFactory);
        }
    }

    @Nested
    class WithScale {

        @Test
        void replaces_scaler_using_the_WithScale_contract() {
            ScalingConfiguration<FluentBigDecimal> actual = config.withScale(4);

            assertThat(actual)
                .isNotSameAs(config)
                .isInstanceOf(ScalingConfiguration.class);
            assertProperties(actual, MATH_CONTEXT, MaxScaleScaler.of(4), FLUENT_BIGDECIMAL_FACTORY);
        }

        @Test
        void keeps_original_configuration_untouched() {
            config.withScale(4);

            assertThat(config.getScaler())
                .isSameAs(SCALER);
        }

        @Test
        void throws_if_current_scaler_does_not_support_scaling() {
            ScalingConfiguration<FluentBigDecimal> nonScaling = config.withScaler(new NopScaler());

            assertThat(nonScaling.getScaler())
                .isInstanceOf(NopScaler.class);

            assertThrows(
                ClassCastException.class,
                () -> nonScaling.withScale(4)
            );
        }
    }

    private static void assertProperties(
        Configuration<FluentBigDecimal> actual,
        MathContext mathContext,
        Scaler scaler,
        Factory<FluentBigDecimal> factory
    ) {
        assertThat(actual.getMathContext())
            .describedAs("MathContext")
            .isEqualTo(mathContext);

        assertThat(actual.getScaler())
            .describedAs("Scaler")
            .isEqualTo(scaler);

        assertThat(actual.getFactory())
            .describedAs("Factory")
            .isSameAs(factory);
    }
}
