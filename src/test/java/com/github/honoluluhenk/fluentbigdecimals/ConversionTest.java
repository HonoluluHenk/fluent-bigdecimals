package com.github.honoluluhenk.fluentbigdecimals;

import java.math.BigDecimal;
import java.math.MathContext;

import com.github.honoluluhenk.fluentbigdecimals.scaler.CashRoundingScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import org.junit.jupiter.api.Test;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;

public class ConversionTest {
    static class Target extends AbstractFluentBigDecimal<Target> {

        private static final long serialVersionUID = 6461737803039223285L;

        Target(BigDecimal value, Configuration<Target> configuration) {
            super(value, configuration);
        }
    }

    static class Source extends AbstractFluentBigDecimal<Source> {
        private static final long serialVersionUID = 1136059771822861030L;

        Source(BigDecimal value, Configuration<Source> configuration) {
            super(value, configuration);
        }
    }

    public static final Configuration<Source> SOURCE =
        new Configuration<>(
            new MathContext(15, HALF_UP),
            new NopScaler(),
            Source::new
        );

    public static final Configuration<Target> TARGET =
        new Configuration<>(
            new MathContext(15, HALF_UP),
            new CashRoundingScaler(CashRounding.of(CashRoundingUnits.ROUND_DOT05)),
            Target::new
        );

    @Test
    void creates_a_rounded_instance_with_the_target_configuration() {
        Source src = SOURCE.of("123.980");
        Target target = src.roundInto(TARGET);

        assertThat(target)
            .isInstanceOf(Target.class);

        assertThat(target.getValue())
            .isEqualTo("124.00");

        assertThat(target.getConfiguration())
            .isSameAs(TARGET);

    }

}
