package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.FixedScaleScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.var;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SuppressWarnings("NewClassNamingConvention")
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
        class OfExactAPIs {
            private final Configuration<FluentBigDecimal> config = ConfigurationFactory.create(
                4,
                HALF_UP,
                new FixedScaleScaler(2)
            );

            @Test
            void using_BigDecimal_works_as_expected() {
                assertAll(
                    () -> assertThat(config.ofExact(new BigDecimal("12.34")).getValue())
                        .isEqualByComparingTo(new BigDecimal("12.34")),
                    () -> assertThrows(
                        NotExactException.class,
                        () -> config.ofExact(new BigDecimal("0.3456"))
                    ),
                    () -> assertThrows(
                        NotExactException.class,
                        () -> config.ofExact(new BigDecimal("0.0001"))
                    )
                );
            }

            @Test
            void using_String_works_as_expected() {
                assertAll(
                    () -> assertThat(config.ofExact("12.34").getValue())
                        .isEqualByComparingTo("12.34"),
                    () -> assertThrows(
                        NotExactException.class,
                        () -> config.ofExact("0.3456")
                    ),
                    () -> assertThrows(
                        NotExactException.class,
                        () -> config.ofExact("0.0001")
                    )
                );
            }

            @Test
            void using_CharArray_works_as_expected() {
                assertAll(
                    () -> assertThat(config.ofExact("12.34".toCharArray()).getValue())
                        .isEqualByComparingTo("12.34"),
                    () -> assertThrows(
                        NotExactException.class,
                        () -> config.ofExact("0.3456".toCharArray())
                    ),
                    () -> assertThrows(
                        NotExactException.class,
                        () -> config.ofExact("0.0001".toCharArray())
                    )
                );
            }

            @Test
            void using_CharArray_Range_works_as_expected() {
                assertAll(
                    () -> assertThat(config.ofExact("12.34".toCharArray(), 0, 5).getValue())
                        .isEqualByComparingTo("12.34"),
                    () -> assertThrows(
                        NotExactException.class,
                        () -> config.ofExact("0.3456".toCharArray(), 0, 6)
                    ),
                    () -> assertThrows(
                        NotExactException.class,
                        () -> config.ofExact("0.0001".toCharArray(), 0, 6)
                    )
                );
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
