package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.CashRoundingScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxPrecisionScaler;
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
     * Taken from: <a href="https://en.wikipedia.org/wiki/Numeric_precision_in_Microsoft_Excel">Wikipedia on Numeric precision in Microsoft Excel</a>
     */
    public static class Excel {
        public static final int EXCEL_PRECISION = 15;
        public static final Scaler EXCEL_SCALER = new MaxPrecisionScaler();
    }

    public static Configuration create(@NonNull MathContext mathContext, @NonNull Scaler scaler) {
        return new Configuration(mathContext, scaler);
    }

    public static Configuration create(int precision, @NonNull RoundingMode roundingMode, @NonNull Scaler scaler) {
        return new Configuration(new MathContext(precision, roundingMode), scaler);
    }

    /**
     * Convenience: some precision, {@link RoundingMode#HALF_UP} rounding and {@link MaxScaleScaler} with a scale.
     */
    public static Configuration monetary(@NonNull int precision, int scale) {
        return create(new MathContext(precision, HALF_UP), new MaxScaleScaler(scale));
    }

    /**
     * Compatible to JPA/Hibernate defaults for BigDecimal: @Column(precision = 16, scale = 2) with {@link RoundingMode#HALF_UP}.
     */
    public static Configuration jpaBigDecimal() {
        return jpaBigDecimal(HALF_UP);
    }

    /**
     * Compatible to JPA defaults for BigDecimal: @Column(precision = 16, scale = 2).
     */
    public static Configuration jpaBigDecimal(@NonNull RoundingMode roundingMode) {
        return databaseJavaNotation(JPA_BIGDECIMAL_PRECISION, JPA_BIGDECIMAL_SCALE, roundingMode);
    }

    /**
     * Custom precision/scale with a {@link MaxScaleScaler} (used by most SQL database systems), uses HALF_UP rounding.
     */
    public static Configuration databaseJavaNotation(int databasePrecsion, int databaseScale) {
        return databaseJavaNotation(databasePrecsion, databaseScale, HALF_UP);
    }

    /**
     * Custom precision/scale with a {@link MaxScaleScaler} (used by most SQL database systems).
     */
    public static Configuration databaseJavaNotation(int databasePrecsion, int databaseScale, @NonNull RoundingMode roundingMode) {
        int javaPrecision = databasePrecsion + databaseScale;
        return create(new MathContext(javaPrecision, roundingMode), new MaxScaleScaler(databaseScale));
    }

    /**
     * Custom precision/scale with a {@link MaxScaleScaler} (used by most SQL database systems).
     * <p>
     * <strong>This method expects parameters in database notation!</strong>
     * <p>
     * This means: precision is the max. number of integers, scale the max. number of decimals.
     */
    public static Configuration databaseDBNotation(int databasePrecsion, int databaseScale, @NonNull RoundingMode roundingMode) {
        int javaPrecision = databasePrecsion + databaseScale;
        return create(new MathContext(javaPrecision, roundingMode), new MaxScaleScaler(databaseScale));
    }

    public static Configuration cashRounding(int precision, @NonNull CashRoundingUnits units) {
        CashRounding rounding = CashRounding.of(units);
        return create(new MathContext(precision, rounding.getRoundingMode()), new CashRoundingScaler(rounding));
    }

    /**
     * Excel compatible rounding/scaling.
     */
    public static Configuration excel() {
        return excel(HALF_UP);
    }

    /**
     * Excel compatible rounding/scaling.
     */
    public static Configuration excel(@NonNull RoundingMode roundingMode) {
        return create(new MathContext(Excel.EXCEL_PRECISION, roundingMode), Excel.EXCEL_SCALER);
    }
}
