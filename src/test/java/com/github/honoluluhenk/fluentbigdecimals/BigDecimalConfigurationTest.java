package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxPrecisionScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxScaleScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class BigDecimalConfigurationTest {
    @Test
    void creates_instance_with_given_values() {
        BigDecimal value = new BigDecimal("123.45");
        MathContext mathContext = new MathContext(5, HALF_UP);
        Scaler scaler = mock(Scaler.class);
        given(scaler.scale(any(), any()))
            .willReturn(value);

        BigDecimalConfiguration factory = BigDecimalConfiguration.create(mathContext, scaler);

        FluentBigDecimal actual = factory.of(value);

        assertThat(actual.getValue())
            .isEqualTo(value);
        assertThat(actual.getConfiguration())
            .isEqualTo(new SimpleConfiguration(mathContext, scaler));
    }

    @Test
    void excel_creates_excel_compatible_factory() {
        var actual = BigDecimalConfiguration.excel()
            .of(new BigDecimal("42"));

        assertThat(actual.getConfiguration())
            .isEqualTo(new SimpleConfiguration(new MathContext(15, HALF_UP), new MaxPrecisionScaler()));

    }

    @Test
    void jpaBigDecimal_creates_JPA_compatible_factory() {
        var actual = BigDecimalConfiguration.jpaBigDecimal()
            .of(new BigDecimal("42"));

        assertThat(actual.getConfiguration())
            .isEqualTo(new SimpleConfiguration(new MathContext(18, HALF_UP), new MaxScaleScaler(2)));
    }

    @Test
    void database_adds_scale_and_precision() {
        var actual = BigDecimalConfiguration.database(5, 1)
            .of(new BigDecimal("42"));

        assertThat(actual.getConfiguration())
            .isEqualTo(new SimpleConfiguration(new MathContext(6, HALF_UP), new MaxScaleScaler(1)));
    }

    @Test
    void calculates_java_precision_from_database_precision_notation() {
        var actual = BigDecimalConfiguration.databasePrecision(5, 2, HALF_UP)
            .of(new BigDecimal("42"));

        assertThat(actual.getConfiguration())
            .isEqualTo(new SimpleConfiguration(new MathContext(7, HALF_UP), new MaxScaleScaler(2)));
    }

}
