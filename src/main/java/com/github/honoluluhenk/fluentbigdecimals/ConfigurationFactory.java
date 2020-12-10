package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.CashRoundingScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxScaleScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;

import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_UP;

/**
 * Factory class for the most common use-cases.
 */
@AllArgsConstructor
@Getter
@With
public class ConfigurationFactory {
    private static final long serialVersionUID = -153378950972296160L;

    public static final Factory<FluentBigDecimal> FLUENT_BIGDECIMAL_FACTORY = FluentBigDecimal::new;

    /**
     * Precision as required by database definitions.
     */
    public static final int JPA_BIGDECIMAL_PRECISION = 16;
    /**
     * Scale as required by database definitions.
     */
    public static final int JPA_BIGDECIMAL_SCALE = 2;
    /**
     * Precision as required by JPA specification.
     */
    public static final int BIGDECIMAL_JPA_PRECISION = JPA_BIGDECIMAL_PRECISION + JPA_BIGDECIMAL_SCALE;


    /**
     * Scale 2: used by most monetary systems.
     */
    public static final int DEFAULT_MONETARY_SCALE = 2;

    /**
     * {@link RoundingMode#HALF_UP}: Used by most monetary systems.
     */
    public static final RoundingMode DEFAULT_MONETARY_ROUNDING = HALF_UP;

    public static Configuration<FluentBigDecimal> create(@NonNull MathContext mathContext, @NonNull Scaler scaler) {
        return new Configuration<>(mathContext, scaler, FLUENT_BIGDECIMAL_FACTORY);
    }

    public static Configuration<FluentBigDecimal> create(int precision, @NonNull RoundingMode roundingMode, @NonNull Scaler scaler) {
        return new Configuration<>(new MathContext(precision, roundingMode), scaler, FLUENT_BIGDECIMAL_FACTORY);
    }

    /**
     * Convenience: some precision, {@link RoundingMode#HALF_UP} rounding and {@link MaxScaleScaler} with a scale.
     */
    public static MonetaryConfiguration<FluentBigDecimal> monetary(@NonNull int precision) {
        return new MonetaryConfiguration<>(
            new MathContext(precision, DEFAULT_MONETARY_ROUNDING),
            new MaxScaleScaler(DEFAULT_MONETARY_SCALE),
            FLUENT_BIGDECIMAL_FACTORY
        );
    }

    /**
     * Compatible to JPA/Hibernate defaults for BigDecimal: @Column(precision = 16, scale = 2) with {@link RoundingMode#HALF_UP}.
     */
    public static Configuration<FluentBigDecimal> jpaBigDecimal() {
        return databaseJavaNotation(JPA_BIGDECIMAL_PRECISION, JPA_BIGDECIMAL_SCALE);
    }

    /**
     * Custom precision/scale with a {@link MaxScaleScaler} (used by most SQL database systems).
     */
    public static Configuration<FluentBigDecimal> databaseJavaNotation(int databasePrecsion, int databaseScale) {
        int javaPrecision = databasePrecsion + databaseScale;
        return create(new MathContext(javaPrecision, HALF_UP), new MaxScaleScaler(databaseScale));
    }

    /**
     * Custom precision/scale with a {@link MaxScaleScaler} (used by most SQL database systems).
     * <p>
     * <strong>This method expects parameters in database notation!</strong>
     * <p>
     * This means: precision is the max. number of integers, scale the max. number of decimals.
     */
    public static Configuration<FluentBigDecimal> databaseDBNotation(int databasePrecsion, int databaseScale, @NonNull RoundingMode roundingMode) {
        int javaPrecision = databasePrecsion + databaseScale;
        return create(new MathContext(javaPrecision, roundingMode), new MaxScaleScaler(databaseScale));
    }

    /**
     * Custom precision and scale of the rounding unit.
     */
    public static Configuration<FluentBigDecimal> cashRounding(int precision, @NonNull CashRoundingUnits units) {
        CashRounding rounding = CashRounding.of(units);
        return create(new MathContext(precision, rounding.getRoundingMode()), new CashRoundingScaler(rounding));
    }

}
