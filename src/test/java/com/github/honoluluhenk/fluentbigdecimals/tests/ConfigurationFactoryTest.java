package com.github.honoluluhenk.fluentbigdecimals.tests;

import com.github.honoluluhenk.fluentbigdecimals.CashRoundingUnits;
import com.github.honoluluhenk.fluentbigdecimals.Configuration;
import com.github.honoluluhenk.fluentbigdecimals.ConfigurationFactory;
import com.github.honoluluhenk.fluentbigdecimals.FluentBigDecimal;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxScaleScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static com.github.honoluluhenk.fluentbigdecimals.ConfigurationFactory.FLUENT_BIGDECIMAL_FACTORY;
import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ConfigurationFactoryTest {
    @Test
    void is_a_plain_instantiable_factory_holder() {
        assertThat(new ConfigurationFactory())
            .isNotNull();
    }

    @Test
    void create_with_precision_and_roundingMode_builds_mathContext() {
        Scaler scaler = mock(Scaler.class);

        var actual = ConfigurationFactory.create(5, HALF_UP, scaler);

        assertConfiguration(actual, new MathContext(5, HALF_UP), scaler);
    }

    @Test
    void monetary_uses_MaxScaleScaler_with_default_monetary_scale() {
        var actual = ConfigurationFactory.monetary(10);

        assertConfiguration(actual, new MathContext(10, HALF_UP), new MaxScaleScaler(2));
    }

    @Test
    void cashRounding_uses_CashRoundingScaler() {
        var actual = ConfigurationFactory.cashRounding(10, CashRoundingUnits.ROUND_DOT05);

        assertThat(actual.getMathContext())
            .isEqualTo(new MathContext(10, HALF_UP));

        assertThat(actual.getScaler())
            .hasToString("CashRoundingScaler[CashRounding(0.05, HALF_UP)]");
    }

    @Test
    void creates_instance_with_given_values() {
        BigDecimal value = new BigDecimal("123.45");
        Scaler scaler = mock(Scaler.class);
        given(scaler.scale(any(), any()))
            .willReturn(value);
        MathContext mathContext = new MathContext(5, HALF_UP);
        Configuration<FluentBigDecimal> configuration = ConfigurationFactory.create(mathContext, scaler);

        FluentBigDecimal actual = configuration.of(value);

        assertThat(actual.getValue())
            .isEqualTo(value);
        assertConfiguration(actual.getConfiguration(), mathContext, scaler);
    }

    @Test
    void jpaBigDecimal_creates_JPA_compatible_factory() {
        var actual = ConfigurationFactory.jpaBigDecimal()
            .of(new BigDecimal("42"));

        assertConfiguration(actual.getConfiguration(), new MathContext(18, HALF_UP), new MaxScaleScaler(2));
    }

    @Test
    void database_adds_scale_and_precision() {
        var actual = ConfigurationFactory.databaseJavaNotation(5, 1)
            .of(new BigDecimal("42"));

        assertConfiguration(actual.getConfiguration(), new MathContext(6, HALF_UP), new MaxScaleScaler(1));
    }

    @Test
    void calculates_java_precision_from_database_precision_notation() {
        var actual = ConfigurationFactory.databaseDBNotation(5, 2, HALF_UP)
            .of(new BigDecimal("42"));

        assertConfiguration(actual.getConfiguration(), new MathContext(7, HALF_UP), new MaxScaleScaler(2));
    }

    void assertConfiguration(Configuration<FluentBigDecimal> config, MathContext mathContext, Scaler scaler) {
        assertThat(config.getMathContext())
            .describedAs("MathContext")
            .isEqualTo(mathContext);

        assertThat(config.getScaler())
            .describedAs("Scaler")
            .isEqualTo(scaler);

        assertThat(config.getFactory())
            .describedAs("FluentBigDecimalFactory")
            .isSameAs(FLUENT_BIGDECIMAL_FACTORY);
    }
}
