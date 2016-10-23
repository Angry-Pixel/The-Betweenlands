package thebetweenlands.util;

public class CubicBezier {
	private float a1, b1, a2, b2;

	private float[][] controlPoints;

	public CubicBezier(float a1, float b1, float a2, float b2) {
		this.a1 = a1;
		this.b1 = b1;
		this.a2 = a2;
		this.b2 = b2;
		controlPoints = new float[4][];
		controlPoints[0] = new float[] { 0, 0 };
		controlPoints[1] = new float[] { a1, b1 };
		controlPoints[2] = new float[] { a2, b2 };
		controlPoints[3] = new float[] { 1, 1 };
	}

	public float eval(float t) {
		float[] scalars = new float[4];
		BezierUtils.allBernstein(3, t, scalars);
		float p = 0;
		for (int i = 0; i < 4; i++) {
			p += scalars[i] * controlPoints[i][1];
		}
		return p;
	}
}
