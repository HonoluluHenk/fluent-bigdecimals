package com.github.honoluluhenk.fluentbigdecimals;

import java.math.BigDecimal;
import java.math.MathContext;

@FunctionalInterface
public interface BiProjection<Arg> {
    BigDecimal project(BigDecimal value, Arg argument, MathContext mathContext);
}
