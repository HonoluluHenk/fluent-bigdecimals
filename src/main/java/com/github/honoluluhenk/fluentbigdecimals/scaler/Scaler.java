package com.github.honoluluhenk.fluentbigdecimals.scaler;

import com.github.honoluluhenk.fluentbigdecimals.ProjectionFunction;

import java.io.Serializable;
import java.math.BigDecimal;

public interface Scaler extends Serializable {

    <Argument> BigDecimal apply(ProjectionFunction<BigDecimal, Argument, BigDecimal> function, BigDecimal value, Argument argument);
}
