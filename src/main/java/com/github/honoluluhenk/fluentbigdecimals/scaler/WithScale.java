package com.github.honoluluhenk.fluentbigdecimals.scaler;

public interface WithScale<T extends Scaler> {
    T withScale(int newScale);
}
