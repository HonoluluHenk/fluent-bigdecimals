package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import lombok.NonNull;

import java.math.MathContext;

public interface Configuration {
    @NonNull MathContext getMathContext();

    @NonNull Scaler getScaler();
}
