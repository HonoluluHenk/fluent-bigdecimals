package com.github.honoluluhenk.fluentbigdecimals;

import org.assertj.core.api.AbstractAssert;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.RoundingMode;
import java.util.Objects;

@SuppressWarnings({"nullable", "argument.type.incompatible", "UnusedReturnValue"})
public class BigDecimalExtAssert extends AbstractAssert<BigDecimalExtAssert, @Nullable BigDecimalExt> {
    public BigDecimalExtAssert(@Nullable BigDecimalExt actual) {

        super(actual, BigDecimalExtAssert.class);
    }

    public static BigDecimalExtAssert assertThat(BigDecimalExt actual) {
        return new BigDecimalExtAssert(actual);
    }

    public BigDecimalExtAssert hasValuePrecision(int precision) {
        isNotNull();
        Objects.requireNonNull(actual);

        int valuePrecision = actual.getValue().precision();
        if (valuePrecision != precision) {
            failWithActualExpectedAndMessage(
                valuePrecision, precision, "Expected %s to have precision:", actual);
        }
        return this;
    }

    public BigDecimalExtAssert hasValueScale(int scale) {
        isNotNull();
        Objects.requireNonNull(actual);

        int valueScale = actual.getValue().scale();
        if (valueScale != scale) {
            failWithActualExpectedAndMessage(valueScale, scale, "Expected %s to have scale:", actual);
        }
        return this;
    }

    public BigDecimalExtAssert hasValue(String textRepresentation) {
        isNotNull();
        Objects.requireNonNull(actual);

        String plainTextValue = actual.getValue().toPlainString();
        if (!textRepresentation.equals(plainTextValue)) {
            failWithActualExpectedAndMessage(plainTextValue, textRepresentation, "Expected %s to have value:", actual);
        }
        return this;
    }

    public BigDecimalExtAssert hasPrecision(int maxPrecision) {
        isNotNull();
        Objects.requireNonNull(actual);

        int actualPrecision = actual.getContext().getPrecision();
        if (actualPrecision != maxPrecision) {
            failWithActualExpectedAndMessage(actualPrecision, maxPrecision, "Expected %s to have max precision:", actual);
        }

        return this;
    }

    public BigDecimalExtAssert hasMaxScale(int maxScale) {
        isNotNull();
        Objects.requireNonNull(actual);

        int actualMaxScale = actual.getContext().getMaxScale();
        if (actualMaxScale != maxScale) {
            failWithActualExpectedAndMessage(actualMaxScale, maxScale, "Expected %s to have max scale:", actual);
        }

        return this;
    }

    public BigDecimalExtAssert hasRoundingMode(RoundingMode roundingMode) {
        isNotNull();
        Objects.requireNonNull(actual);

        RoundingMode actualRoundingMode = actual.getContext().getRoundingMode();
        if (actualRoundingMode != roundingMode) {
            failWithActualExpectedAndMessage(actualRoundingMode, roundingMode, "Expected %s to have RoundingMode:", actual);
        }
        return this;
    }

    public BigDecimalExtAssert hasSameContextAs(BigDecimalExt other) {
        isNotNull();

        hasPrecision(other.getContext().getPrecision());
        hasMaxScale(other.getContext().getMaxScale());
        hasRoundingMode(other.getContext().getRoundingMode());

        return this;
    }

    public BigDecimalExtAssert isEqualComparingValue(BigDecimalExt other) {
        isNotNull();
        Objects.requireNonNull(actual);

        if (!actual.equalsComparingValue(other)) {
            failWithActualExpectedAndMessage(actual.getValue(), other.getValue(), "Expected values to be equal:");
        }

        return this;
    }

    public BigDecimalExtAssert isNotEqualComparingValue(BigDecimalExt other) {
        isNotNull();
        Objects.requireNonNull(actual);

        if (actual.equalsComparingValue(other)) {
            failWithActualExpectedAndMessage(actual.getValue(), other.getValue(), "Expected values not to be equal:");
        }

        return this;
    }
}
