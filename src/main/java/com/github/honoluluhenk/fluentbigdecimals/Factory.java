package com.github.honoluluhenk.fluentbigdecimals;

import lombok.NonNull;

import java.math.BigDecimal;

@FunctionalInterface
public interface Factory<T extends AbstractFluentBigDecimal<T>> {
    T create(@NonNull BigDecimal value, @NonNull Configuration<T> configuration);
}
