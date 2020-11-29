package com.github.honoluluhenk.fluentbigdecimals.adjuster;

import java.io.Serializable;
import java.math.BigDecimal;

public interface Adjuster extends Serializable {
    BigDecimal adjust(BigDecimal value);
}
