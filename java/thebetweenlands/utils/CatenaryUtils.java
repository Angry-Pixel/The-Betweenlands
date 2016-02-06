package thebetweenlands.utils;

import net.minecraft.util.MathHelper;

public final class CatenaryUtils {
	static float[][] catenary(float[] a, float[] b, float arcLength, int n) {
		return catenary(a, b, arcLength, n, 1);
	}

	private static float[][] catenary(float[] a, float[] b, float arcLength, int n, float sag) {
		float[] x, y;

		if (a[0] > b[0]) {
			float[] a_copy = {a[0], a[1]};
			a = new float[]{b[0], b[1]};
			b = a_copy;
		}

		float d = b[0] - a[0];
		float h = b[1] - a[1];

		if (MathHelper.abs(d) < MIN_HORIZ) {
			x = new float[n];
			for (int i = 0; i < x.length; i++) {
				x[i] = (a[0] + b[0]) / 2;
			}
			if (arcLength < MathHelper.abs(h)) {
				y = linspace(a[1], b[1], n);
			} else {
				sag = (arcLength - MathHelper.abs(h)) / 2;
				int nSag = MathHelper.ceiling_float_int(n * sag / arcLength);
				float yMax = Math.max(a[1], b[1]);
				float yMin = Math.min(a[1], b[1]);
				y = concat(linspace(yMax, yMin - sag, n - nSag), linspace(yMin - sag, yMin, nSag));
			}
			return new float[][]{x, y};
		}

		x = linspace(a[0], b[0], n);

		if (arcLength <= MathHelper.sqrt_float(d * d + h * h)) {
			y = linspace(a[1], b[1], n);
			return new float[][]{x, y};
		}

		for (int iter = 0; iter < MAX_ITER; iter++) {
			float val = g(sag, d, arcLength, h);
			float grad = dg(sag, d);

			if (MathHelper.abs(val) < MIN_VAL || MathHelper.abs(grad) < MIN_GRAD) {
				break;
			}

			float search = -g(sag, d, arcLength, h) / dg(sag, d);

			float alpha = 1;
			float sagNew = sag + alpha * search;

			while (sagNew < 0 || MathHelper.abs(g(sagNew, d, arcLength, h)) > MathHelper.abs(val)) {
				alpha = STEP_DEC * alpha;
				if (alpha < MIN_STEP) {
					break;
				}
				sagNew = sag + alpha * search;
			}

			sag = sagNew;
		}

		float xLeft = 0.5F * ((float) Math.log((arcLength + h) / (arcLength - h)) / sag - d);
		float xMin = a[0] - xLeft;
		float bias = a[1] - (float) Math.cosh(xLeft * sag) / sag;

		y = new float[x.length];
		for (int i = 0; i < x.length; i++) {
			y[i] = (float) Math.cosh((x[i] - xMin) * sag) / sag + bias;
		}

		return new float[][]{x, y};
	}

	private static float[] concat(float[] a, float[] b) {
		int aLength = a.length;
		int bLength = b.length;
		float[] concat = new float[aLength + bLength];
		System.arraycopy(a, 0, concat, 0, aLength);
		System.arraycopy(b, 0, concat, aLength, bLength);
		return concat;
	}

	private static float dg(float s, float d) {
		return 2 * (float) Math.cosh(s * d / 2) * d / (2 * s) - 2 * (float) Math.sinh(s * d / 2) / (s * s);
	}

	private static float g(float s, float d, float arcLength, float h) {
		return 2 * (float) Math.sinh(s * d / 2) / s - MathHelper.sqrt_float(arcLength * arcLength - h * h);
	}

	private static float[] linspace(float base, float limit, int n) {
		float[] elements = new float[n];
		float scaler = n > 1 ? (limit - base) / (n - 1) : 0;
		for (int i = 0; i < n; i++) {
			elements[i] = base + scaler * i;
		}
		return elements;
	}

	public static final int SEG_LENGTH = 3;

	public static final float HALF_PI = MathUtils.PI / 2;

	private static final int MAX_ITER = 100;

	private static final float MIN_GRAD = 1e-10F;

	private static final float MIN_VAL = 1e-8F;

	private static final float STEP_DEC = 0.5F;

	private static final float MIN_STEP = 1e-9F;

	private static final float MIN_HORIZ = 1e-3F;

	private CatenaryUtils() {
	}
}
