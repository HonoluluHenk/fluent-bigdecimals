package com.github.honoluluhenk.fluentbigdecimals;

import java.math.BigDecimal;

public class FluentBigDecimal extends AbstractFluentBigDecimal<FluentBigDecimal> {
    private static final long serialVersionUID = 4779995034250641739L;

    public FluentBigDecimal(
        BigDecimal value,
        Configuration<FluentBigDecimal> configuration
    ) {
        super(value, configuration);
    }
}
