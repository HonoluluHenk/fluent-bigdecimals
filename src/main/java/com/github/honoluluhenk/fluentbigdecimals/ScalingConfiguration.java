package com.github.honoluluhenk.fluentbigdecimals;

import com.github.honoluluhenk.fluentbigdecimals.scaler.Scaler;
import com.github.honoluluhenk.fluentbigdecimals.scaler.WithScale;
import lombok.var;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.math.MathContext;

// please note: good design would mandate a second generic argument:
// S extends Scaler & WithScale<S>
// But: this would make the API look sooooooooo very complicated while adding no benefits
// to the target usage. So I decided against this and added some ugly casts.
// The compiler makes sure that we cannot pass in a non-compliant scaler and thus we can expect calling
// all with*() methods to return an assignable instance.
public class ScalingConfiguration<T extends AbstractFluentBigDecimal<T>> extends Configuration<T> {
    private static final long serialVersionUID = 7476797484270808023L;

    public <S extends Scaler & WithScale<S>> ScalingConfiguration(
        @NonNull MathContext mathContext,
        @NonNull S scaler,
        @NonNull Factory<T> factory
    ) {
        super(mathContext, scaler, factory);
    }

    private static <FBD extends AbstractFluentBigDecimal<FBD>, Scal extends Scaler & WithScale<Scal>>
    ScalingConfiguration<FBD> from(Configuration<FBD> other) {
        @SuppressWarnings("unchecked") Scal scaler = (Scal) other.getScaler();
        return new ScalingConfiguration<>(
            other.getMathContext(),
            scaler,
            other.getFactory()
        );
    }

    @Override
    public ScalingConfiguration<T> withMathContext(@NonNull MathContext mathContext) {
        var result = from(super.withMathContext(mathContext));

        return result;
    }

    @Override
    public ScalingConfiguration<T> withScaler(@NonNull Scaler scaler) {
        var result = from(super.withScaler(scaler));

        return result;
    }

    @Override
    public <S extends Scaler & WithScale<S>> ScalingConfiguration<T> withScalingScaler(@NonNull S scaler) {
        var result = from(super.withScalingScaler(scaler));

        return result;
    }

    @Override
    public <O extends AbstractFluentBigDecimal<O>> ScalingConfiguration<O> withFactory(@NonNull Factory<O> factory) {
        var result = from(super.withFactory(factory));

        return result;
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
