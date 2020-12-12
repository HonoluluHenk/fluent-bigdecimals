package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.WithScale;
import lombok.NonNull;

import java.math.MathContext;

// please note: good design would mandate a second generic argument:
// S extends Scaler & WithScale<S>
// But: this would make the API look sooooooooo very complicated while adding no benefits
// to the target usage. So I decided against this and added some ugly casts.
// The compiler makes sure that we cannot pass in a non-compliant scaler and thus we can expect calling
// all with*() methods to return an assignable instance.
public class ScalingConfiguration<T extends AbstractFluentBigDecimal<T>> extends Configuration<T> {
    public <S extends Scaler & WithScale<S>> ScalingConfiguration(
        @NonNull MathContext mathContext,
        @NonNull S scaler,
        @NonNull Factory<T> factory
    ) {
        super(mathContext, scaler, factory);
    }

    @Override
    public ScalingConfiguration<T> withMathContext(@NonNull MathContext mathContext) {
        return (ScalingConfiguration<T>) super.withMathContext(mathContext);
    }

    @Override
    public ScalingConfiguration<T> withScaler(@NonNull Scaler scaler) {
        return (ScalingConfiguration<T>) super.withScaler(scaler);
    }

    @Override
    public <S extends Scaler & WithScale<S>> ScalingConfiguration<T> withScalingScaler(@NonNull S scaler) {
        return super.withScalingScaler(scaler);
    }

    @Override
    public <O extends AbstractFluentBigDecimal<O>> ScalingConfiguration<O> withFactory(@NonNull Factory<O> factory) {
        return (ScalingConfiguration<O>) super.withFactory(factory);
    }

    public <S extends Scaler & WithScale<S>> @NonNull ScalingConfiguration<T> withScale(int newScale) {
        @SuppressWarnings("unchecked")
        WithScale<S> scaler = (WithScale<S>) getScaler();

        S newScaler = scaler.withScale(newScale)
            .withScale(newScale);

        var result = new ScalingConfiguration<>(getMathContext(), newScaler, getFactory());

        return result;
    }
}
