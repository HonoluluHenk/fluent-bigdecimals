package com.github.honoluluhenk.fluentbigdecimals;

import lombok.Getter;

@Getter
public class NotExactException extends ArithmeticException {
    private static final long serialVersionUID = 2018470829221504406L;

    public NotExactException(Object value, Configuration<?> configuration) {
        super(formatMessage(value, configuration));
    }

    private static String formatMessage(Object value, Configuration<?> configuration) {
        return String.format("The value %s needed rounding/scaling which is not permitted by %s", value, configuration);
    }
}
