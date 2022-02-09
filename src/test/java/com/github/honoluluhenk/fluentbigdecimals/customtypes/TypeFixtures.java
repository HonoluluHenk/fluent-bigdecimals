package com.github.honoluluhenk.fluentbigdecimals.customtypes;

import java.math.BigDecimal;
import java.math.MathContext;

import com.github.honoluluhenk.fluentbigdecimals.AbstractFluentBigDecimal;
import com.github.honoluluhenk.fluentbigdecimals.CashRounding;
import com.github.honoluluhenk.fluentbigdecimals.CashRoundingUnits;
import com.github.honoluluhenk.fluentbigdecimals.Configuration;
import com.github.honoluluhenk.fluentbigdecimals.scaler.CashRoundingScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.NopScaler;
import lombok.experimental.UtilityClass;

import static java.math.RoundingMode.HALF_UP;

@UtilityClass
public class TypeFixtures {
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
}
