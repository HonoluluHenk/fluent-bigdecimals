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

class BigDecimalFactoryTest {
    @Test
    void creates_instance_with_given_values() {
        BigDecimal value = new BigDecimal("123.45");
        MathContext mathContext = new MathContext(5, HALF_UP);
        Scaler scaler = mock(Scaler.class);
        given(scaler.scale(any(), any()))
            .willReturn(value);

        BigDecimalFactory factory = BigDecimalFactory.factory(mathContext, scaler);

        FluentBigDecimal actual = factory.of(value);

        assertThat(actual.getValue())
            .isEqualTo(value);
        assertThat(actual.getScaler())
            .isEqualTo(scaler);
        assertThat(actual.getMathContext())
            .isEqualTo(mathContext);
    }

    @Test
    void excel_creates_excel_compatible_factory() {
        var actual = BigDecimalFactory.excel()
            .of(new BigDecimal("42"));

        assertThat(actual.getMathContext())
            .isEqualTo(new MathContext(15, HALF_UP));
        assertThat(actual.getScaler())
            .isExactlyInstanceOf(MaxPrecisionScaler.class);
    }

    @Test
    void jpaBigDecimal_creates_JPA_compatible_factory() {
        var actual = BigDecimalFactory.jpaBigDecimal()
            .of(new BigDecimal("42"));

        assertThat(actual.getMathContext())
            .isEqualTo(new MathContext(18, HALF_UP));

        assertThat(actual.getScaler())
            .isExactlyInstanceOf(MaxScaleScaler.class);
        assertThat(((MaxScaleScaler) actual.getScaler()).getMaxScale())
            .isEqualTo(2);
    }

    @Test
    void database_adds_scale_and_precision() {
        var actual = BigDecimalFactory.database(5, 1)
            .of(new BigDecimal("42"));

        assertThat(actual.getMathContext())
            .isEqualTo(new MathContext(6, HALF_UP));

        assertThat(actual.getScaler())
            .isExactlyInstanceOf(MaxScaleScaler.class);
        assertThat(((MaxScaleScaler) actual.getScaler()).getMaxScale())
            .isEqualTo(1);
    }

    @Test
    void calculates_java_precision_from_database_precision_notation() {
        var actual = BigDecimalFactory.databasePrecision(5, 2, HALF_UP)
            .of(new BigDecimal("42"));

        assertThat(actual.getMathContext())
            .isEqualTo(new MathContext(7, HALF_UP));

        assertThat(actual.getScaler())
            .isExactlyInstanceOf(MaxScaleScaler.class);
        assertThat(((MaxScaleScaler) actual.getScaler()).getMaxScale())
            .isEqualTo(2);
    }

}
