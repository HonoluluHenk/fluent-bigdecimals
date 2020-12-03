package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FactoryTest {
    @Test
    void creates_instance_with_given_values() {
        MathContext mathContext = new MathContext(5, RoundingMode.HALF_UP);
        Scaler scaler = mock(Scaler.class);
        Factory factory = Factory.factory(mathContext, scaler);
        BigDecimal value = new BigDecimal("123.45");

        FluentBigDecimal actual = factory.fromValue(value);

        assertThat(actual.getValue())
            .isEqualTo(value);
        assertThat(actual.getScaler())
            .isEqualTo(scaler);
        assertThat(actual.getMathContext())
            .isEqualTo(mathContext);
    }


}
