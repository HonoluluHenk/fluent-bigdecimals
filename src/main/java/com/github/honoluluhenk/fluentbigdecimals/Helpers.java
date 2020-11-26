package com.github.honoluluhenk.fluentbigdecimals;

import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

@UtilityClass
class Helpers {

    static <R> @NonNull R castNonNull(@Nullable R value) {
        @SuppressWarnings({"assignment.type.incompatible", "ConstantConditions"})
        @NonNull R result = value;

        return Objects.requireNonNull(result);
    }
}
