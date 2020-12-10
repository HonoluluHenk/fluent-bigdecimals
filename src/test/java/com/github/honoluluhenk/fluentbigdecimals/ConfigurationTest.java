package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class ConfigurationTest {


    @Nested
    class Factories {
        final BigDecimal inputValue = new BigDecimal("123.45");
        final BigDecimal roundedValue = new BigDecimal("999.999");
        final Scaler mockScaler = mock(Scaler.class);

        final Configuration<FluentBigDecimal> CONFIG = ConfigurationFactory
            .create(3, HALF_UP, mockScaler);


        @BeforeEach
        void beforeEach() {
            given(mockScaler.scale(any(), any()))
                .willReturn(roundedValue);
        }

        @Nested
        class Of {
            @Test
            void rounds_and_calls_scaler() {
                var actual = CONFIG
                    .of(inputValue);

                verify(mockScaler)
                    .scale(eq(new BigDecimal("123")), any(MathContext.class));

                assertThat(actual.getValue())
                    .isSameAs(roundedValue);
            }
        }

        @Nested
        class OfRaw {
            @Test
            void does_neither_round_nor_call_scaler() {
                FluentBigDecimal actual = CONFIG
                    .ofRaw(inputValue);

                verify(mockScaler, never())
                    .scale(any(), any());

                assertThat(actual.getValue())
                    .isSameAs(inputValue);
            }
        }
    }

}
