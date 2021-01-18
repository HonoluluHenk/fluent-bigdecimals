package com.github.honoluluhenk.fluentbigdecimals.demo;

import com.github.honoluluhenk.fluentbigdecimals.AbstractFluentBigDecimal;
import com.github.honoluluhenk.fluentbigdecimals.CashRoundingUnits;
import com.github.honoluluhenk.fluentbigdecimals.Configuration;
import com.github.honoluluhenk.fluentbigdecimals.ConfigurationFactory;
import lombok.NonNull;
import lombok.var;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ExtensionDemoTest {
    public static final Configuration<MyMath> MY_MATH = ConfigurationFactory.monetary(20)
        .withFactory(MyMath::new);

    public static final Configuration<MyMath> SWISS_CASH = ConfigurationFactory
        .cashRounding(20, CashRoundingUnits.ROUND_DOT05)
        .withFactory(MyMath::new);


    public static class MyMath extends AbstractFluentBigDecimal<MyMath> {
        private static final long serialVersionUID = -1828369497254888980L;

        protected MyMath(@NonNull BigDecimal value, @NonNull Configuration<MyMath> configuration) {
            super(value, configuration);
        }

        public String toJson() {
            return "{ value: \"" + getValue().toPlainString() + "\" }";
        }

        public MyMath roundIntoSwissRappen() {
            return roundInto(SWISS_CASH);
        }
    }


    @Test
    void myBusinessMethod() {
        var json = MY_MATH.of("42.04") // of() creates an instance of MyMath
            .roundIntoSwissRappen()
            .add(new BigDecimal("23"))
            .toJson();

        assertThat(json)
            .isEqualTo("{ value: \"65.05\" }");
    }
}
