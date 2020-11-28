package com.github.honoluluhenk.fluentbigdecimals;

import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
class Helpers {

    static <R> @NonNull R castNonNull(@Nullable R value) {
        @SuppressWarnings({"assignment.type.incompatible", "ConstantConditions"})
        @NonNull R result = value;

        return Objects.requireNonNull(result);
    }

    /**
     * Like currying foo(a, b, c) -> foo(a)(b) but inverse: foo(a, b, c) -> foo(b)(a)
     */
    static <X, Y, R> Function<X, R> curryReverse(BiFunction<X, Y, R> function, Y argument) {
        return x -> function.apply(x, argument);
    }
}
