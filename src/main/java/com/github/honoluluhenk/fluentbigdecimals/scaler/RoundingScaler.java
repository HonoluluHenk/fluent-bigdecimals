package com.github.honoluluhenk.fluentbigdecimals.scaler;

import com.github.honoluluhenk.fluentbigdecimals.ProjectionFunction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.util.Objects.requireNonNull;

@Getter
@With
@EqualsAndHashCode
public class RoundingScaler implements Scaler {
    private static final long serialVersionUID = 7043803811609303754L;

    private final MathContext mathContext;

    public RoundingScaler(MathContext mathContext) {
        this.mathContext = requireNonNull(mathContext, "mathContext required");
    }

    @Override
    public <Argument> BigDecimal apply(ProjectionFunction<BigDecimal, Argument, BigDecimal> function, BigDecimal value, Argument argument) {
        var result = function.apply(value, argument, getMathContext());

        return result;
    }

    @Override
    public String toString() {
        return RoundingScaler.class.getSimpleName();
    }
}
