package com.github.honoluluhenk.fluentbigdecimals.customtypes;

import com.github.honoluluhenk.fluentbigdecimals.customtypes.TypeFixtures.Source;
import com.github.honoluluhenk.fluentbigdecimals.customtypes.TypeFixtures.Target;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ConversionTest {

    @Test
    void creates_a_rounded_instance_with_the_target_configuration() {
        Source src = TypeFixtures.SOURCE.of("123.980");
        Target target = src.roundInto(TypeFixtures.TARGET);

        assertThat(target)
            .isInstanceOf(Target.class);

        assertThat(target.getValue())
            .isEqualTo("124.00");

        assertThat(target.getConfiguration())
            .isSameAs(TypeFixtures.TARGET);

    }

    @Test
    void allow_some_methods_on_differing_types() {
        Source a = TypeFixtures.SOURCE.of("123.980");
        Target b = TypeFixtures.TARGET.of("123.980");

        assertAll(
            () -> assertThatCode(() -> a.add(b)).doesNotThrowAnyException(),
            () -> assertThatCode(() -> a.subtract(b)).doesNotThrowAnyException(),
            () -> assertThatCode(() -> a.multiply(b)).doesNotThrowAnyException(),
            () -> assertThatCode(() -> a.divide(b)).doesNotThrowAnyException()
        );
    }

}
