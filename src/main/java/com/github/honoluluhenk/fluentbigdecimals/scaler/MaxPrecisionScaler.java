package com.github.honoluluhenk.fluentbigdecimals.scaler;

import lombok.*;

import java.math.BigDecimal;
import java.math.MathContext;

@AllArgsConstructor
@Getter
@With
@EqualsAndHashCode(callSuper = false)
public class MaxPrecisionScaler implements Scaler {
    private static final long serialVersionUID = 7043803811609303754L;

    @Override
    public BigDecimal scale(@NonNull BigDecimal value, @NonNull MathContext mathContext) {
        return value;
    }

    @Override
    public String toString() {
        return MaxPrecisionScaler.class.getSimpleName();
    }
}
