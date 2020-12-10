package com.github.honoluluhenk.fluentbigdecimals;

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
