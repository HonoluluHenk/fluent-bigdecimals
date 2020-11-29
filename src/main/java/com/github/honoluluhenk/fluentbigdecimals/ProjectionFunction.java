package com.github.honoluluhenk.fluentbigdecimals;

import java.math.MathContext;

@FunctionalInterface
public interface ProjectionFunction<T, U, R> {

    R apply(T t, U u, MathContext mathContext);
}
