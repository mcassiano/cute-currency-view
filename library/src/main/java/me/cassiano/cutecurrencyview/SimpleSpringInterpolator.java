package me.cassiano.cutecurrencyview;

import android.view.animation.Interpolator;

class SimpleSpringInterpolator implements Interpolator {

    private static final float FACTOR = 0.5f;

    @Override
    public float getInterpolation(float input) {
        return (float)
                (1 + Math.pow(2, -10 * input) * Math.sin((input - FACTOR / 4) * Math.PI * 2 / FACTOR));
    }
}