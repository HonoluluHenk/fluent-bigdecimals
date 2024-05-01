package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.*;
import org.checkerframework.checker.nullness.qual.NonNull;

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
    public @NonNull BigDecimal scale(@NonNull BigDecimal value, @NonNull MathContext mathContext) {
        var result = value.setScale(scale, mathContext.getRoundingMode());

        return result;
    }

    @Override
    public String toString() {
        return String.format("FixedScaleScaler[%d]", scale);
    }
}
