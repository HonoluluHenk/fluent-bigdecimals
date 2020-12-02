package com.github.honoluluhenk.fluentbigdecimals.scaler;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;

public interface Scaler extends Serializable {
    BigDecimal scale(BigDecimal value, MathContext mathContext);
}
