package com.github.honoluluhenk.fluentbigdecimals.tests.customtypes;

import com.github.honoluluhenk.fluentbigdecimals.*;
import com.github.honoluluhenk.fluentbigdecimals.scaler.CashRoundingScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.math.RoundingMode.HALF_UP;

@UtilityClass
public class TypeFixtures {
    public static final Configuration<Source> SOURCE =
        ConfigurationFactory
            .create(
                new MathContext(15, HALF_UP),
                new NopScaler()
            )
            .withFactory(Source::new);

    public static final Configuration<Target> TARGET =
        ConfigurationFactory
            .create(
                new MathContext(15, HALF_UP),
                new CashRoundingScaler(CashRounding.of(CashRoundingUnits.ROUND_DOT05))
            )
            .withFactory(Target::new);

    public static class Target extends AbstractFluentBigDecimal<Target> {
        private static final long serialVersionUID = 6461737803039223285L;

        Target(BigDecimal value, Configuration<Target> configuration) {
            super(value, configuration);
        }
    }

    public static class Source extends AbstractFluentBigDecimal<Source> {
        private static final long serialVersionUID = 1136059771822861030L;

        Source(BigDecimal value, Configuration<Source> configuration) {
            super(value, configuration);
        }
    }
}
