package com.github.honoluluhenk.fluentbigdecimals;

import java.math.BigDecimal;
import java.math.MathContext;

@FunctionalInterface
public interface ProjectionFunction<T, U, R> {
    static ProjectionFunction<BigDecimal, BigDecimal, BigDecimal> identity() {
        return (a, b, c) -> a;
    }

    R apply(T t, U u, MathContext mathContext);
}
