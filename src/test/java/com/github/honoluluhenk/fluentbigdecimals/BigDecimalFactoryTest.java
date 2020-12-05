package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.FixedPointScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxPrecisionScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BigDecimalFactoryTest {
    @Test
    void creates_instance_with_given_values() {
        MathContext mathContext = new MathContext(5, HALF_UP);
        Scaler scaler = mock(Scaler.class);
        BigDecimalFactory factory = BigDecimalFactory.factory(mathContext, scaler);
        BigDecimal value = new BigDecimal("123.45");

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
            .isExactlyInstanceOf(FixedPointScaler.class);
        assertThat(((FixedPointScaler) actual.getScaler()).getMaxScale())
            .isEqualTo(2);
    }

    @Test
    void calculates_java_precision_from_database_precision_notation() {
        var actual = BigDecimalFactory.databasePrecision(5, 2, HALF_UP)
            .of(new BigDecimal("42"));

        assertThat(actual.getMathContext())
            .isEqualTo(new MathContext(7, HALF_UP));

        assertThat(actual.getScaler())
            .isExactlyInstanceOf(FixedPointScaler.class);
        assertThat(((FixedPointScaler) actual.getScaler()).getMaxScale())
            .isEqualTo(2);
    }

}
