package com.github.honoluluhenk.fluentbigdecimals;

import org.assertj.core.api.AbstractAssert;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.RoundingMode;

@SuppressWarnings("nullable")
public class BigDecimalExtAssert extends AbstractAssert<BigDecimalExtAssert, @Nullable BigDecimalExt> {
    public BigDecimalExtAssert(@Nullable BigDecimalExt actual) {

        super(actual, BigDecimalExtAssert.class);
    }

    public static BigDecimalExtAssert assertThat(BigDecimalExt actual) {
        return new BigDecimalExtAssert(actual);
    }

    public BigDecimalExtAssert hasValuePrecision(int precision) {
        isNotNull();
        int valuePrecision = actual.getValue().precision();
        if (valuePrecision != precision) {
            failWithMessage("Expected value to have precision %s but was %s",
                    precision, valuePrecision
            );
        }
        return this;
    }

    public BigDecimalExtAssert hasValueScale(int scale) {
        isNotNull();
        int valueScale = actual.getValue().scale();
        if (valueScale != scale) {
            failWithMessage("Expected value to have scale %s but was %s",
                    scale, valueScale
            );
        }
        return this;
    }

    public BigDecimalExtAssert hasValue(String textRepresentation) {
        isNotNull();
        String plainTextValue = actual.getValue().toPlainString();
        if (!textRepresentation.equals(plainTextValue)) {
            failWithMessage("Expected value to be %s but was %s",
                    textRepresentation, plainTextValue
            );
        }
        return this;
    }

    public BigDecimalExtAssert hasPrecision(int maxPrecision) {
        isNotNull();
        if (actual.getContext().getPrecision() != maxPrecision) {
            failWithMessage("Expected max precision to be %s but was %s",
                    maxPrecision, actual.getContext().getPrecision()
            );
        }

        return this;
    }

    public BigDecimalExtAssert hasMaxScale(int maxScale) {
        isNotNull();
        if (actual.getContext().getMaxScale() != maxScale) {
            failWithMessage("Expected max scale to be %s but was %s",
                    maxScale, actual.getContext().getMaxScale()
            );
        }

        return this;
    }

    public BigDecimalExtAssert hasRoundingMode(RoundingMode roundingMode) {
        isNotNull();
        if (actual.getContext().getRoundingMode() != roundingMode) {
            failWithMessage("Expected RoundingMode to be %s but was %s",
                    roundingMode, actual.getContext().getRoundingMode()
            );
        }
        return this;
    }

    public BigDecimalExtAssert hasSameParamsAs(BigDecimalExt other) {
        isNotNull();

        hasPrecision(other.getContext().getPrecision());
        hasMaxScale(other.getContext().getMaxScale());
        hasRoundingMode(other.getContext().getRoundingMode());

        return this;
    }
}
