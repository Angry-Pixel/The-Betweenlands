package thebetweenlands.util;

public final class BezierUtils {
	private BezierUtils() {
	}

	/**
	 * Compute the value of all nth degree Bernstein polynomials.
	 * 
	 * @param curveDegree
	 *            : degree of curve
	 * @param t
	 *            : curve parameter on interval [0,1]
	 * @param scalars
	 *            : curveDegree + 1 Bernstein values.
	 */
	public static void allBernstein(int curveDegree, float t, float scalars[]) {
		int j, k;
		float nt = 1 - t;
		float saved;
		scalars[0] = 1;
		for (j = 1; j <= curveDegree; j++) {
			saved = 0;
			for (k = 0; k < j; k++) {
				float temp = scalars[k];
				scalars[k] = saved + nt * temp;
				saved = t * temp;
			}
			scalars[j] = saved;
		}
	}

	/**
	 * 
	 * Compute point of nth degree Bezier curve.
	 * 
	 * @param controlPoints
	 *            : curveDegree + 1 control points
	 * @param curveDegree
	 *            : degree of curve
	 * @param t
	 *            : curve parameter on interval [0,1]
	 * @param point
	 *            : resulting point
	 */
	public static void pointOnBezierCurve(float controlPoints[][], int curveDegree, float t, float point[]) {
		float scalars[] = new float[curveDegree + 1];
		int k;
		allBernstein(curveDegree, t, scalars);
		point[0] = point[1] = point[2] = 0;
		for (k = 0; k <= curveDegree; k++) {
			point[0] += scalars[k] * controlPoints[k][0];
			point[1] += scalars[k] * controlPoints[k][1];
			point[2] += scalars[k] * controlPoints[k][2];
		}
	}

	/**
	 * Compute an approximate length of a Bezier curve given the control points.
	 * 
	 * @param controlPoints
	 *            : control points of a Bezier curve
	 * @return the approximate length
	 */
	public static float approximateLength(float controlPoints[][]) {
		float length = 0;
		for (int i = 0; i < controlPoints.length - 1; i++) {
			float xDif = controlPoints[i + 1][0] - controlPoints[i][0];
			float yDif = controlPoints[i + 1][1] - controlPoints[i][1];
			float zDif = controlPoints[i + 1][2] - controlPoints[i][2];
			length += Math.sqrt(xDif * xDif + yDif * yDif + zDif * zDif);
		}
		return length;
	}

	public static int tesselationSegementsForLength(float length, float scale) {
		float noLessThan = 10 * scale;
		float segs = length * scale / 30f;
		return (int) Math.ceil(Math.sqrt(segs * segs * 0.6 + noLessThan * noLessThan));
	}

	public static float[][] curve(float controlPoints[][]) {
		return curve(controlPoints, 1);
	}

	public static float[][] curve(float controlPoints[][], float scale) {
		int count = tesselationSegementsForLength(approximateLength(controlPoints), scale);
		float[][] points = new float[count][3];
		for (int i = 0; i < count; i++) {
			float t = i / (float) (count - 1);
			float[] point = new float[3];
			pointOnBezierCurve(controlPoints, controlPoints.length - 1, t, point);
			points[i] = point;
		}
		return points;
	}

	public static float[][] derivative(float[][] controlPoints) {
		float[][] derivative = new float[controlPoints.length - 1][controlPoints[0].length];
		for (int i = 0; i < derivative.length; i++) {
			float[] point = derivative[i];
			for (int n =  0; n < point.length; n++) {
				point[n] = derivative.length * (controlPoints[i + 1][n] - controlPoints[i][n]);
			}
		}
		return derivative;
	}
}
