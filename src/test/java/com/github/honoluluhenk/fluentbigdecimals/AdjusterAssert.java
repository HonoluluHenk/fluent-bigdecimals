package com.github.honoluluhenk.fluentbigdecimals;

import org.assertj.core.api.AbstractAssert;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

@SuppressWarnings({"nullable", "argument.type.incompatible", "UnusedReturnValue"})
public class AdjusterAssert extends AbstractAssert<AdjusterAssert, @Nullable BigDecimalExt> {
    public AdjusterAssert(@Nullable BigDecimalExt actual) {

        super(actual, AdjusterAssert.class);
    }

    public static AdjusterAssert assertThat(BigDecimalExt actual) {
        return new AdjusterAssert(actual);
    }

    public AdjusterAssert hasPrecision(int precision) {
        isNotNull();
        Objects.requireNonNull(actual);

        int valuePrecision = actual.getValue().precision();
        if (valuePrecision != precision) {
            failWithActualExpectedAndMessage(
                valuePrecision, precision, "Expected %s to have precision:", actual);
        }
        return this;
    }

    public AdjusterAssert hasScale(int scale) {
        isNotNull();
        Objects.requireNonNull(actual);

        int valueScale = actual.getValue().scale();
        if (valueScale != scale) {
            failWithActualExpectedAndMessage(valueScale, scale, "Expected %s to have scale:", actual);
        }
        return this;
    }

    public AdjusterAssert hasValue(String textRepresentation) {
        isNotNull();
        Objects.requireNonNull(actual);

        String plainTextValue = actual.getValue().toPlainString();
        if (!textRepresentation.equals(plainTextValue)) {
            failWithActualExpectedAndMessage(plainTextValue, textRepresentation, "Expected %s to have value:", actual);
        }
        return this;
    }

//    public BigDecimalExtAssert hasSameAdjuster(Adjuster adjuster) {
//        isNotNull();
//        Objects.requireNonNull(actual);
//
//        if (actual.getAdjuster() != adjuster) {
//            failWithActualExpectedAndMessage(actual.getAdjuster(), adjuster, "Adjuster not the same:");
//        }
//
//        return this;
//    }
//
//    public BigDecimalExtAssert hasSameAdjusterAs(BigDecimalExt other) {
//        isNotNull();
//        Objects.requireNonNull(actual);
//
//        hasSameAdjuster(other.getAdjuster());
//
//        return this;
//    }

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


//    public static <T extends Comparable<T>> @Nullable T comparesTo(T value) {
//        reportMatcher(new CompareEqual<>(value));
//
//        if (value == null) {
//            return null;
//        } else {
//            Object defaultValue = Primitives.defaultValue((Class<?>) value.getClass());
//
//            //noinspection unchecked
//            return (T) defaultValue;
//        }
//    }
//
//    private static void reportMatcher(ArgumentMatcher<?> matcher) {
//        ThreadSafeMockingProgress.mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
//    }
}
