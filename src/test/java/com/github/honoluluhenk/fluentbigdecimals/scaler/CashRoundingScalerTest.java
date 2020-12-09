package com.github.honoluluhenk.fluentbigdecimals.scaler;

import com.github.honoluluhenk.fluentbigdecimals.CashRounding;
import com.github.honoluluhenk.fluentbigdecimals.CashRoundingUnits;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class CashRoundingScalerTest {

    @Test
    void calls_calls_CashRounding() {
        CashRounding roundingSpy = spy(CashRounding.of(CashRoundingUnits.ROUND_DOT05));

        new CashRoundingScaler(roundingSpy)
            .scale(BigDecimal.ONE, new MathContext(10, RoundingMode.HALF_UP));

        verify(roundingSpy)
            .round(BigDecimal.ONE);

    }

}
