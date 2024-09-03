package thebetweenlands.util;

public class BezierUtils {

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
	public static void allBernstein(int curveDegree, float t, float[] scalars) {
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

}
