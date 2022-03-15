package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.MaxScaleScaler;
import lombok.var;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NotExactExceptionTest {
    private final Configuration<FluentBigDecimal> config = ConfigurationFactory.create(
        4,
        HALF_UP,
        new MaxScaleScaler(2)
    );

    @Test
    void constructs_a_nice_message_with_debug_info() {
        var actual = new NotExactException(new BigDecimal("123.45"), config);

        assertThat(actual)
            .hasMessageContaining("123.45")
            .hasMessageContaining("[4,HALF_UP,MaxScaleScaler[2]]");
    }

}
