package com.github.honoluluhenk.fluentbigdecimals;

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

@AllArgsConstructor
@Getter
@With
public class BigDecimalFactory implements Scaler {
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
        public static final int JPA_BIGDECIMAL_PRECISION = 18;
        public static final int JPA_BIGDECIMAL_SCALE = 2;
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

    public static BigDecimalFactory factory(@NonNull MathContext mathContext, @NonNull Scaler scaler) {
        return new BigDecimalFactory(mathContext, scaler);
    }

    /**
     * Compatible to JPA defaults for BigDecimal: @Column(precision = 16, scale = 2) with {@link RoundingMode#HALF_UP}.
     */
    public static BigDecimalFactory jpaBigDecimal() {
        return jpaBigDecimal(HALF_UP);
    }

    /**
     * Compatible to JPA defaults for BigDecimal: @Column(precision = 16, scale = 2).
     */
    public static BigDecimalFactory jpaBigDecimal(@NonNull RoundingMode roundingMode) {
        return database(Database.JPA_BIGDECIMAL_PRECISION, Database.JPA_BIGDECIMAL_SCALE, roundingMode);
    }

    /**
     * Custom precision/scale with a {@link MaxScaleScaler} (used by most SQL database systems).
     */
    public static BigDecimalFactory database(int databasePrecsion, int databaseScale, @NonNull RoundingMode roundingMode) {
        return factory(new MathContext(databasePrecsion, roundingMode), new MaxScaleScaler(databaseScale));
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
    public static BigDecimalFactory databasePrecision(int databasePrecsion, int databaseScale, @NonNull RoundingMode roundingMode) {
        int javaPrecision = databasePrecsion + databaseScale;
        return factory(new MathContext(javaPrecision, roundingMode), new MaxScaleScaler(databaseScale));
    }

    /**
     * Excel compatible rounding/scaling.
     */
    public static BigDecimalFactory excel() {
        return excel(HALF_UP);
    }

    /**
     * Excel compatible rounding/scaling.
     */
    public static BigDecimalFactory excel(@NonNull RoundingMode roundingMode) {
        return factory(new MathContext(Excel.EXCEL_PRECISION, roundingMode), Excel.EXCEL_SCALER);
    }

    public FluentBigDecimal of(@NonNull BigDecimal value) {
        return new FluentBigDecimal(value, mathContext, scaler);
    }

    public FluentBigDecimal of(@NonNull String bigDecimal) {
        return of(new BigDecimal(bigDecimal));
    }

    //FIXME: implement the others

    @Override
    public @NonNull BigDecimal scale(@NonNull BigDecimal value, @NonNull MathContext mathContext) {
        return scaler.scale(value, mathContext);
    }
}
