package thebetweenlands.utils;

import net.minecraft.util.MathHelper;

/**
 * 
 * @author Paul Fulham
 *
 */
public final class MathUtils {
	private MathUtils() {}

	public static final float TAU = (float) (2 * StrictMath.PI);

	public static final float PI = (float) StrictMath.PI;

	public static final float DEG_TO_RAD = (float) (StrictMath.PI / 180);

	public static final float RAD_TO_DEG = (float) (180 / StrictMath.PI);

	public static double linearTransformd(double x, double domainMin, double domainMax, double rangeMin, double rangeMax) {
		x = x < domainMin ? domainMin : x > domainMax ? domainMax : x;
		return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
	}

	public static float linearTransformf(float x, float domainMin, float domainMax, float rangeMin, float rangeMax) {
		x = x < domainMin ? domainMin : x > domainMax ? domainMax : x;
		return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
	}

	public static int[][] permutationsOf(int x) {
		return permutationsOf(sequence(x));
	}

	public static int[][] permutationsOf(int[] x) {
		if (x.length == 1) {
			return new int[][] { x };
		}
		int[] part = new int[x.length - 1];
		System.arraycopy(x, 1, part, 0, part.length);
		int[][] perms = permutationsOf(part);
		int element = x[0];
		int[][] result = new int[perms.length * (perms[0].length + 1)][];
		for (int n = 0; n < perms.length; n++) {
			int[] perm = perms[n];
			for (int i = 0; i <= perm.length; i++) {
				int[] r = result[i + n * (perm.length + 1)] = new int[x.length];
				System.arraycopy(perm, 0, r, 0, i);
				r[i] = element;
				System.arraycopy(perm, i, r, i + 1, perm.length - i);
			}
		}
		return result;
	}

	public static int factorial(int x) {
		int factorial = 1;
		for (int i = 2; i <= x; i++) {
			factorial *= i;
		}
		return factorial;
	}

	public static int[] sequence(int n) {
		int[] sequence = new int[n];
		for (int k = 0; k < n; k++) {
			sequence[k] = k;
		}
		return sequence;
	}

	public static float wrapAngle(float angle) {
		angle %= 360;
		if (angle < 0) {
			angle += 360;
		}
		return angle;
	}

	public static String radiansToDMS(float angle) {
		angle *= RAD_TO_DEG;
		int degrees = (int) angle;
		angle -= (int) angle;
		angle *= 60;
		int minutes = (int) angle;
		angle -= (int) angle;
		angle *= 60;
		return String.format("%s\u00B0%s'%.3f\"", degrees, minutes, angle);
	}

	public static float mod(float a, float b) {
		return (a % b + b) % b;
	}

	public static float lerpAngle(float a, float b, float t) {
		return t * (mod(-a + b + 180, 360) - 180) + a;
	}

	public static int joinFloat(float a, float b, float c) {
		return joinInt(MathHelper.floor_float(a * 255), MathHelper.floor_float(b * 255), MathHelper.floor_float(c * 255));
	}

	public static int joinInt(int a, int b, int c) {
		int r = a;
		r = (r << 8) + b;
		r = (r << 8) + c;
		return r;
	}
}
