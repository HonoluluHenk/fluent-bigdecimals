package com.github.honoluluhenk.fluentbigdecimals;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.github.honoluluhenk.fluentbigdecimals.Projection.identity;
import static org.assertj.core.api.Assertions.assertThat;

class ProjectionTest {

    @Test
    void identity_returns_same_value() {
        MathContext ignored = new MathContext(5, RoundingMode.HALF_UP);
        BigDecimal fixture = new BigDecimal("123.45");

        assertThat(identity().project(fixture, ignored))
            .isSameAs(fixture);
    }

}
