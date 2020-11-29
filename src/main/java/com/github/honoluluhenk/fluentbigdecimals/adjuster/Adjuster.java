package com.github.honoluluhenk.fluentbigdecimals.adjuster;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;

// FIXME: might this just be a Scaler?
public interface Adjuster extends Serializable {
    BigDecimal adjust(BigDecimal value);

    MathContext getMathContext();
}
