package thebetweenlands.util;

import java.util.Arrays;

import net.minecraft.util.Mth;

public final class MathUtils {

	public static double linearTransformd(double x, double domainMin, double domainMax, double rangeMin, double rangeMax) {
		x = x < domainMin ? domainMin : Math.min(x, domainMax);
		return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
	}

	public static float linearTransformf(float x, float domainMin, float domainMax, float rangeMin, float rangeMax) {
		x = x < domainMin ? domainMin : Math.min(x, domainMax);
		return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
	}

	public static int[][] permutationsOf(int x) {
		return permutationsOf(sequence(x));
	}

	public static int[][] permutationsOf(int[] x) {
		if (x.length == 1) {
			return new int[][]{x};
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
		angle *= Mth.DEG_TO_RAD;
		int degrees = (int) angle;
		angle -= (int) angle;
		angle *= 60;
		int minutes = (int) angle;
		angle -= (int) angle;
		angle *= 60;
		return String.format("%s°%s'%.3f\"", degrees, minutes, angle);
	}

	public static float mod(float a, float b) {
		return (a % b + b) % b;
	}

	public static double mod(double a, double b) {
		return (a % b + b) % b;
	}

	public static float lerpAngle(float a, float b, float t) {
		return lerpValue(a, b, t, 360);
	}

	public static float lerpValue(float a, float b, float t, float range) {
		return t * modularDelta(a, b, range) + a;
	}

	public static float modularDelta(float a, float b, float range) {
		return mod(-a + b + range / 2, range) - range / 2;
	}

	public static double modularDelta(double a, double b, double range) {
		return mod(-a + b + range / 2, range) - range / 2;
	}

	public static boolean epsilonEquals(float a, float b) {
		return Math.abs(b - a) < 1.0E-5F;
	}

	public static float adjustAngleForInterpolation(float angle, float prevAngle) {
		return adjustValueForInterpolation(angle, prevAngle, -180, 180);
	}

	public static float adjustValueForInterpolation(float x, float prevX, float min, float max) {
		float range = max - min;
		while (x - prevX < min) {
			prevX -= range;
		}
		while (x - prevX >= max) {
			prevX += range;
		}
		return prevX;
	}
	/*
	public static int degToByte(float angle) {
		return Math.floor(angle * (256F / 360));
	}

	public static int hash(int x) {
		x = (x >> 16 ^ x) * 0x45D9F3B;
		x = (x >> 16 ^ x) * 0x45D9F3B;
		x = x >> 16 ^ x;
		return x;
	}

	public static void minmax( min, Point3f max, float x, float y, float z) {
		if (min.x != min.x) {
			min.x = x;
			min.y = y;
			min.z = z;
		}
		if (max.x != max.x) {
			max.x = x;
			max.y = y;
			max.z = z;
		}
		min.x = x < min.x ? x : min.x;
		min.y = y < min.y ? y : min.y;
		min.z = z < min.z ? z : min.z;
		max.x = x > max.x ? x : max.x;
		max.y = y > max.y ? y : max.y;
		max.z = z > max.z ? z : max.z;
	}
	*/

	/**
	 * Precision summation via Kahan's Summation Algorithm.
	 * The output is stored in {@code out}
	 * @param out       an array containing the {@code total} and correction sum {@code c}
	 * @param fromIndex the index of {@code input} to start summation from; inclusive
	 * @param toIndex   the index of {@code input} to end summation at;     exclusive
	 * @param input     an array containing the input values to sum
	 */
	public static void precisionSum(double[] out, int fromIndex, int toIndex, double[] input) {
		assert out.length == 2;

		// Get total and correction sum
		double total = out[0];
		double c = out[1];

		for (int i = fromIndex; i < toIndex; ++i) {
			final double value = input[i];
			final double y = value - c;
			final double t = total + y;
			c = (t - total) - y;
			total = t;
		}
		
		// Put total and correction sum in output
		out[0] = total;
		out[1] = c;
	}
	
	/**
	 * Precision summation via Kahan's Summation Algorithm
	 * @param fromIndex the index of {@code input} to start summation from; inclusive
	 * @param toIndex   the index of {@code input} to end summation at;     exclusive
	 * @param input     an array containing the input values to sum
	 * @return the total result
	 */
	public static double precisionSum(int fromIndex, int toIndex, double[] input) {
		rangeCheck(input.length, fromIndex, toIndex);
		
		// Get total and correction sum
		double total = 0.0;
		double c = 0.0;
		
		for (int i = fromIndex; i < toIndex; ++i) {
			final double value = input[i];
			final double y = value - c;
			final double t = total + y;
			c = (t - total) - y;
			total = t;
		}
		
		// Return total sum
		return total;
	}

	/**
	 * Precision summation via Kahan's Summation Algorithm.
	 * The output is stored in {@code out}
	 * @param out   an array containing the {@code total} and correction sum {@code c}
	 * @param input an array containing the input values to sum
	 */
	public static void precisionSum(double[] out, double[] input) {
		assert out.length == 2;

		// Get total and correction sum
		double total = out[0];
		double c = out[1];
		
		for (final double value : input) {
			final double y = value - c;
			final double t = total + y;
			c = (t - total) - y;
			total = t;
		}
		
		// Put total and correction sum in output
		out[0] = total;
		out[1] = c;
	}

	/**
	 * Precision summation via Kahan's Summation Algorithm
	 * @param input an array containing the input values to sum
	 * @return the total result
	 */
	public static double precisionSum(double[] input) {
		// Get total and correction sum
		double total = 0.0;
		double c = 0.0;
		
		for (final double value : input) {
			final double y = value - c;
			final double t = total + y;
			c = (t - total) - y;
			total = t;
		}
		
		// Return total sum
		return total;
	}


	/**
	 * Precision summation via Kahan's Summation Algorithm.
	 * The output is stored in {@code out}
	 * @param out   an array containing the {@code total} and correction sum {@code c}
	 * @param value the value to add to {@code total}
	 */
	public static void precisionSum(double[] out, double value) {
		assert out.length == 2;

		// Get total and correction sum
		double total = out[0];
		double c = out[1];
		
		final double y = value - c;
		final double t = total + y;
		c = (t - total) - y;
		total = t;
		
		// Put total and correction sum in output
		out[0] = total;
		out[1] = c;
	}

	// varargs versions of precisionSum
	/**
	 * Precision summation via Kahan's Summation Algorithm.
	 * The output is stored in {@code out}
	 * @param out   an array containing the {@code total} and correction sum {@code c}
	 * @param input an array containing the input values to sum
	 */
	@SafeVarargs
	public static void precisionSumAll(double[] out, double ...input) {
		precisionSum(out, input);
	}
	
	/**
	 * Precision summation via Kahan's Summation Algorithm
	 * @param input an array containing the input values to sum
	 * @return the total result
	 */
	@SafeVarargs
	public static double precisionSumAll(double ...input) {
		return precisionSum(input);
	}
	
	 
	/**
	 * Checks that {@code fromIndex} and {@code toIndex} are in
	 * the range and throws an exception if they aren't.
	 */
	private static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException(
				"fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
		}
		if (fromIndex < 0) {
			throw new ArrayIndexOutOfBoundsException(fromIndex);
		}
		if (toIndex > arrayLength) {
			throw new ArrayIndexOutOfBoundsException(toIndex);
		}
	}
}
