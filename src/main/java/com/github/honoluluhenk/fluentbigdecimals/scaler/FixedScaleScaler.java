package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;

import java.math.BigDecimal;
import java.math.MathContext;

@AllArgsConstructor
@Getter
@With
@EqualsAndHashCode(callSuper = false)
public class FixedScaleScaler implements Scaler, WithScale<FixedScaleScaler> {
    private static final long serialVersionUID = -8114082394199420674L;

    private final int scale;

    @Override
    public BigDecimal scale(BigDecimal value, MathContext mathContext) {
        return value.setScale(scale, mathContext.getRoundingMode());
    }

    @Override
    public String toString() {
        return String.format("FixedScaleScaler[%d]", scale);
    }
}
