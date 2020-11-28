package com.github.honoluluhenk.fluentbigdecimals;

import java.math.BigDecimal;

/**
 * Just returns the unchanged value.
 * <p>
 * Useful if you just like the API of {@link BigDecimalExt} but want to do rounding/scaling yourself.
 */
public class IdentityAdjuster implements Adjuster {
    private static final long serialVersionUID = -1451544265321329723L;

    @Override
    public BigDecimal adjust(BigDecimal value) {
        return value;
    }

    @Override
    public String toString() {
        return IdentityAdjuster.class.getSimpleName();
    }
}
