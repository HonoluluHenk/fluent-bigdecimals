package com.github.honoluluhenk.fluentbigdecimals;

import lombok.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;

@FunctionalInterface
public interface Factory<T extends AbstractFluentBigDecimal<T>> extends Serializable {
    T create(@NonNull BigDecimal value, @NonNull Configuration<T> configuration);
}
