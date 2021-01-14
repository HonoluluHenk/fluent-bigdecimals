package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
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

        @Nested
        class Behaviour {
            final BigDecimal inputValue = new BigDecimal("123.45");
            final BigDecimal roundedValue = new BigDecimal("999.999");
            final Scaler mockScaler = mock(Scaler.class);

            final Configuration<FluentBigDecimal> MOCK_CONFIG = ConfigurationFactory
                .create(3, HALF_UP, mockScaler);


            @BeforeEach
            void beforeEach() {
                given(mockScaler.scale(any(), any()))
                    .willReturn(roundedValue);
            }

            @Test
            void of_rounds_and_calls_scaler() {
                var actual = MOCK_CONFIG
                    .of(inputValue);

                verify(mockScaler)
                    .scale(eq(new BigDecimal("123")), any(MathContext.class));

                assertThat(actual.getValue())
                    .isSameAs(roundedValue);
            }

            @Test
            void ofRaw_does_neither_round_nor_call_scaler() {
                FluentBigDecimal actual = MOCK_CONFIG
                    .ofRaw(inputValue);

                verify(mockScaler, never())
                    .scale(any(), any());

                assertThat(actual.getValue())
                    .isSameAs(inputValue);
            }
        }

        @Nested
        class OfAPIs {
            private final Configuration<FluentBigDecimal> CONFIG = ConfigurationFactory
                .create(10, HALF_UP, new NopScaler());

            @Test
            void using_string() {
                FluentBigDecimal actual = CONFIG.of("123.45");

                assertThat(actual.getValue())
                    .isEqualTo("123.45");
            }

            @Test
            void using_char_array() {
                FluentBigDecimal actual = CONFIG.of("123.45".toCharArray());

                assertThat(actual.getValue())
                    .isEqualTo("123.45");
            }

            @Test
            void using_char_array_with_offset_len() {
                FluentBigDecimal actual = CONFIG.of("xxx123.45yyy".toCharArray(), 3, 6);

                assertThat(actual.getValue())
                    .isEqualTo("123.45");
            }

            @Test
            void using_int() {
                FluentBigDecimal actual = CONFIG.of(123);

                assertThat(actual.getValue())
                    .isEqualTo("123");
            }

            @Test
            void using_BigInteger() {
                FluentBigDecimal actual = CONFIG.of(BigInteger.ONE);

                assertThat(actual.getValue())
                    .isEqualTo("1");
            }

            @Test
            void using_unsvcaledVale_and_scale() {
                FluentBigDecimal actual = CONFIG.of(BigInteger.valueOf(123), 2);

                assertThat(actual.getValue())
                    .isEqualTo("1.23");
            }

            @Test
            void using_long() {
                FluentBigDecimal actual = CONFIG.of(123L);

                assertThat(actual.getValue())
                    .isEqualTo("123");
            }

            @Test
            void using_double() {
                FluentBigDecimal actual = CONFIG.of(123.45);

                assertThat(actual.getValue())
                    .isEqualTo("123.4500000");
            }

        }

        @Nested
        class ValueOf {
            private final Configuration<FluentBigDecimal> CONFIG = ConfigurationFactory
                .create(10, HALF_UP, new NopScaler());

            @Test
            void using_long() {
                FluentBigDecimal actual = CONFIG.valueOf(123L);

                assertThat(actual.getValue())
                    .isEqualTo("123");
            }

            @Test
            void using_double() {
                FluentBigDecimal actual = CONFIG.valueOf(123.45);

                assertThat(actual.getValue())
                    .isEqualTo("123.45");
            }

            @Test
            void using_unscaledVal_and_scale() {
                FluentBigDecimal actual = CONFIG.valueOf(123, 2);

                assertThat(actual.getValue())
                    .isEqualTo("1.23");
            }
        }

    }

}
