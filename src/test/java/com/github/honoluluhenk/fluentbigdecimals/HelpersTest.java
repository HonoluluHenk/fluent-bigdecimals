package com.github.honoluluhenk.fluentbigdecimals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;

import static com.github.honoluluhenk.fluentbigdecimals.internal.Helpers.castNonNull;
import static com.github.honoluluhenk.fluentbigdecimals.internal.Helpers.curryReverse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HelpersTest {

    @Nested
    class CastNonNull {

        @Test
        void throws_on_null_input() {
            //noinspection ResultOfMethodCallIgnored
            assertThatThrownBy(() -> castNonNull(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void returns_same_instance() {
            Object obj = new Object();

            assertThat(obj)
                .isSameAs(obj);
        }

    }

    @Nested
    class CurryReverse {

        @Test
        void has_correct_parameter_ordering() {
            BiFunction<Integer, String, String> biFunction = (x, y) -> x + " and then " + y;

            String result = curryReverse(biFunction, "p2").apply(1);

            assertThat(result)
                .isEqualTo("1 and then p2");
        }
    }

}
