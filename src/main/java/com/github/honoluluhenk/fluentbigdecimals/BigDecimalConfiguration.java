package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.CashRoundingScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxPrecisionScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxScaleScaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.RoundingMode.HALF_UP;

/**
 * Factory class for the most common use-cases.
 */
@AllArgsConstructor
@Getter
@With
public class BigDecimalConfiguration implements Configuration {
    private static final long serialVersionUID = -153378950972296160L;

    /**
     * Values taken from <a href="https://forcedotcom.github.io/java-sdk/java-db-com-datatypes-map">some random dude on the internet</a>.
     * Remember: databases treat "precision" as the max. number of integers and scale as the max. number of decimals, independently of each other.
     * Java treats "precision" as the total counter of digits (i.e.: precision + scale).
     * This means:
     * |             | precision | scale |
     * | DB notation | 16        | 2     |
     * | BigDecimal  | 18        | 2     |
     */
    public static class Database {
        /**
         * Precision as required by database definitions.
         */
        public static final int BIGDECIMAL_PRECISION = 16;
        /**
         * Scale as required by database definitions.
         */
        public static final int BIGDECIMAL_SCALE = 2;
        /**
         * Precision as required by JPA specification.
         */
        public static final int BIGDECIMAL_JPA_PRECISION = BIGDECIMAL_PRECISION + BIGDECIMAL_SCALE;
    }

    /**
     * Taken from: <a href="https://en.wikipedia.org/wiki/Numeric_precision_in_Microsoft_Excel">Wikipedia on Numeric precision in Microsoft Excel</a>
     */
    public static class Excel {
        public static final int EXCEL_PRECISION = 15;
        public static final Scaler EXCEL_SCALER = new MaxPrecisionScaler();
    }

    private final @NonNull MathContext mathContext;
    private final @NonNull Scaler scaler;

    public static BigDecimalConfiguration create(@NonNull MathContext mathContext, @NonNull Scaler scaler) {
        return new BigDecimalConfiguration(mathContext, scaler);
    }

    public static BigDecimalConfiguration create(int precision, @NonNull RoundingMode roundingMode, @NonNull Scaler scaler) {
        return new BigDecimalConfiguration(new MathContext(precision, roundingMode), scaler);
    }

    /**
     * Convenience: some precision, {@link RoundingMode#HALF_UP} rounding and {@link MaxScaleScaler} with a scale.
     */
    public static BigDecimalConfiguration monetary(@NonNull int precision, int scale) {
        return create(new MathContext(precision, HALF_UP), new MaxScaleScaler(scale));
    }

    /**
     * Compatible to JPA defaults for BigDecimal: @Column(precision = 16, scale = 2) with {@link RoundingMode#HALF_UP}.
     */
    public static BigDecimalConfiguration jpaBigDecimal() {
        return jpaBigDecimal(HALF_UP);
    }

    /**
     * Compatible to JPA defaults for BigDecimal: @Column(precision = 16, scale = 2).
     */
    public static BigDecimalConfiguration jpaBigDecimal(@NonNull RoundingMode roundingMode) {
        return database(Database.BIGDECIMAL_PRECISION, Database.BIGDECIMAL_SCALE, roundingMode);
    }

    /**
     * Custom precision/scale with a {@link MaxScaleScaler} (used by most SQL database systems), uses HALF_UP rounding.
     */
    public static BigDecimalConfiguration database(int databasePrecsion, int databaseScale) {
        return database(databasePrecsion, databaseScale, HALF_UP);
    }

    /**
     * Custom precision/scale with a {@link MaxScaleScaler} (used by most SQL database systems).
     */
    public static BigDecimalConfiguration database(int databasePrecsion, int databaseScale, @NonNull RoundingMode roundingMode) {
        int javaPrecision = databasePrecsion + databaseScale;
        return create(new MathContext(javaPrecision, roundingMode), new MaxScaleScaler(databaseScale));
    }

    /**
     * Custom precision/scale with a {@link MaxScaleScaler} (used by most SQL database systems).
     * <p>
     * <strong>This method expects parameters in database notation!</strong>
     * <p>
     * This means: precision is the max. number of integers, scale the max. number of decimals.
     * <p>
     * See {@link Database} for more details.
     */
    public static BigDecimalConfiguration databasePrecision(int databasePrecsion, int databaseScale, @NonNull RoundingMode roundingMode) {
        int javaPrecision = databasePrecsion + databaseScale;
        return create(new MathContext(javaPrecision, roundingMode), new MaxScaleScaler(databaseScale));
    }

    public static BigDecimalConfiguration cashRounding(int precision, @NonNull CashRoundingUnits units) {
        CashRounding rounding = CashRounding.of(units);
        return create(new MathContext(precision, rounding.getRoundingMode()), new CashRoundingScaler(rounding));
    }

    /**
     * Excel compatible rounding/scaling.
     */
    public static BigDecimalConfiguration excel() {
        return excel(HALF_UP);
    }

    /**
     * Excel compatible rounding/scaling.
     */
    public static BigDecimalConfiguration excel(@NonNull RoundingMode roundingMode) {
        return create(new MathContext(Excel.EXCEL_PRECISION, roundingMode), Excel.EXCEL_SCALER);
    }

    public @NonNull FluentBigDecimal of(@NonNull BigDecimal value) {
        return FluentBigDecimal.of(value, mathContext, scaler);
    }

    public @NonNull FluentBigDecimal of(@NonNull String bigDecimal) {
        return of(new BigDecimal(bigDecimal));
    }

    public @NonNull FluentBigDecimal ofRaw(@NonNull BigDecimal value) {
        return FluentBigDecimal.ofRaw(value, mathContext, scaler);
    }

    public @NonNull FluentBigDecimal ofRaw(@NonNull String bigDecimal) {
        return ofRaw(new BigDecimal(bigDecimal));
    }

    //FIXME: implement the others

    @Override
    public String toString() {
        return String.format("%s[%s,%s]",
            getClass().getSimpleName(),
            getMathContext(),
            getScaler());
    }
}
