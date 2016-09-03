package thebetweenlands.util;

import com.google.common.base.Preconditions;

/*
 * Use http://cubic-bezier.com/ to find parameters
 *
 * Based off of
 * https://github.com/gre/bezier-easing/blob/18f06f5d058184690f5975a243e5bcfcba2e89c4/src/index.js
 */
public class CubicBezier {
    private static final float NEWTON_ITERATIONS = 4;

    private static final float NEWTON_MIN_SLOPE = 1e-3F;

    private static final float SUBDIVISION_PRECISION = 1e-7F;

    private static final float SUBDIVISION_MAX_ITERATIONS = 10;

    private static final int SPLINE_TABLE_SIZE = 11;

    private static final float SAMPLE_STEP_SIZE = 1 / (SPLINE_TABLE_SIZE - 1F);

    private float x1, y1, x2, y2;

    private float[] sampleValues;

    public CubicBezier(float x1, float y1, float x2, float y2) {
        Preconditions.checkArgument(x1 >= 0 && x1 <= 1, "Bezier x1 value must be in [0, 1] range");
        Preconditions.checkArgument(x2 >= 0 && x2 <= 1, "Bezier x2 value must be in [0, 1] range");
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        sampleValues = new float[SPLINE_TABLE_SIZE];
        for (int i = 0; i < sampleValues.length; i++) {
            sampleValues[i] = calcBezier(i * SAMPLE_STEP_SIZE, x1, x2);
        }
    }

    public float eval(float x) {
        if (x1 == y1 && x2 == y2) {
            return x;
        }
        if (x == 0) {
            return 0;
        }
        if (x == 1) {
            return 1;
        }
        return calcBezier(getTForX(x), y1, y2);
    }

    public float getTForX(float x) {
        float intervalStart = 0;
        int currentSample = 1;
        for (int lastSample = SPLINE_TABLE_SIZE - 1; currentSample != lastSample && sampleValues[currentSample] <= x; currentSample++) {
            intervalStart += SAMPLE_STEP_SIZE;
        }
        currentSample--;
        float dist = (x - sampleValues[currentSample]) / (sampleValues[currentSample + 1] - sampleValues[currentSample]);
        float guessForT = intervalStart + dist * SAMPLE_STEP_SIZE;
        float intialSlope = getSlope(guessForT, x1, x2);
        if (intialSlope >= NEWTON_MIN_SLOPE) {
            return newtonRaphsonIterate(x, guessForT, x1, x2);
        }
        if (intialSlope == 0) {
            return guessForT;
        }
        return binarySubdivide(x, intervalStart, intervalStart + SAMPLE_STEP_SIZE, x1, x2);
    }

    private static float binarySubdivide(float x, float a, float b, float x1, float x2) {
        float currentX, currentT;
        int i = 0;
        do {
            currentT = a + (b - a) / 2;
            currentX = calcBezier(currentT, x1, x2) - x;
            if (currentX > 0) {
                b = currentT;
            } else {
                a = currentT;
            }
        } while (Math.abs(currentX) > SUBDIVISION_PRECISION && ++i < SUBDIVISION_MAX_ITERATIONS);
        return currentT;
    }

    private static float newtonRaphsonIterate(float x, float guessT, float x1, float x2) {
        for (int i = 0; i < NEWTON_ITERATIONS; i++) {
            float currentSlope = getSlope(guessT, x1, x2);
            if (currentSlope == 0) {
                return guessT;
            }
            float currentX = calcBezier(guessT, x1, x2) - x;
            guessT -= currentX / currentSlope;
        }
        return guessT;
    }

    private static float calcBezier(float t, float a1, float a2) {
        return ((getA(a1, a2) * t + getB(a1, a2)) * t + getC(a1)) * t;
    }

    private static float getSlope(float t, float a1, float a2) {
        return 3 * getA(a1, a2) * t * t + 2 * getB(a1, a2) * t + getC(a1);
    }

    private static float getA(float a1, float a2) {
        return 1 - 3 * a2 + 3 * a1;
    }

    private static float getB(float a1, float a2) {
        return 3 * a2 - 6 * a1;
    }

    private static float getC(float a1) {
        return 3 * a1;
    }
}
