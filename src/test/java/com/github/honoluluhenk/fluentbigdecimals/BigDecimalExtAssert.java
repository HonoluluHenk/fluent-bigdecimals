package com.github.honoluluhenk.fluentbigdecimals;

import org.assertj.core.api.AbstractAssert;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

@SuppressWarnings({"nullable", "argument.type.incompatible", "UnusedReturnValue"})
public class BigDecimalExtAssert extends AbstractAssert<BigDecimalExtAssert, @Nullable BigDecimalExt> {
    public BigDecimalExtAssert(@Nullable BigDecimalExt actual) {

        super(actual, BigDecimalExtAssert.class);
    }

    public static BigDecimalExtAssert assertThat(BigDecimalExt actual) {
        return new BigDecimalExtAssert(actual);
    }

    public BigDecimalExtAssert hasPrecision(int precision) {
        isNotNull();
        Objects.requireNonNull(actual);

        int valuePrecision = actual.getValue().precision();
        if (valuePrecision != precision) {
            failWithActualExpectedAndMessage(
                valuePrecision, precision, "Expected %s to have precision:", actual);
        }
        return this;
    }

    public BigDecimalExtAssert hasScale(int scale) {
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

    public BigDecimalExtAssert hasSameAdjuster(Adjuster adjuster) {
        isNotNull();
        Objects.requireNonNull(actual);

        if (actual.getAdjuster() != adjuster) {
            failWithActualExpectedAndMessage(actual.getAdjuster(), adjuster, "Adjuster not the same:");
        }

        return this;
    }

    public BigDecimalExtAssert hasSameAdjusterAs(BigDecimalExt other) {
        isNotNull();
        Objects.requireNonNull(actual);

        hasSameAdjuster(other.getAdjuster());

        return this;
    }

//    public BigDecimalExtAssert hasValueMatchingAdjuster() {
//        isNotNull();
//        Objects.requireNonNull(actual);
//
//        if (actual.getValue().precision() > actual.getAdjuster().getPrecision()) {
//            failWithActualExpectedAndMessage(
//                actual.getValue().precision(),
//                actual.getAdjuster().getPrecision(),
//                "Value precision exceeds adjuster precision:"
//            );
//        }
//
//        if (actual.getValue().scale() > actual.getAdjuster().getMaxScale()) {
//            failWithActualExpectedAndMessage(
//                actual.getValue().scale(),
//                actual.getAdjuster().getMaxScale(),
//                "Value scale exceeds adjuster maxScale:"
//            );
//        }
//
//        return this;
//    }
//
//    public BigDecimalExtAssert hasValueMatchingAdjuster(String value, Adjuster adjuster) {
//        isNotNull();
//        Objects.requireNonNull(actual);
//
//        hasValue(value);
//        hasSameAdjuster(adjuster);
//        hasValueMatchingAdjuster();
//
//        return this;
//    }
}
