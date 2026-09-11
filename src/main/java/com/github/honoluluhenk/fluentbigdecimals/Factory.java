package com.github.honoluluhenk.fluentbigdecimals;

import java.io.Serializable;
import java.math.BigDecimal;

@FunctionalInterface
public interface Factory<T extends AbstractFluentBigDecimal<T>> extends Serializable {
    T create(BigDecimal value, Configuration<T> configuration);
}
