package thebetweenlands.util;


import net.minecraft.world.phys.Vec3;

/**
 * Catmull-Rom spline implementation
 */
public class CatmullRomSpline implements ISpline {
	private final Vec3[] pts;

	public CatmullRomSpline(Vec3[] pts) {
		this.pts = new Vec3[pts.length];
		System.arraycopy(pts, 0, this.pts, 0, pts.length);
	}

	@Override
	public Vec3[] getNodes() {
		return this.pts;
	}

	@Override
	public Vec3 interpolate(float s) {
		int numSections = pts.length - 3;
		int currPt = (int) Math.min(Math.floor(s * (float) numSections), numSections - 1);

		Vec3 P0 = this.pts[currPt];
		Vec3 P1 = this.pts[currPt + 1];
		Vec3 P2 = this.pts[currPt + 2];
		Vec3 P3 = this.pts[currPt + 3];

		float t0 = 0.0F;
		float t1 = this.getT(t0, P0, P1);
		float t2 = this.getT(t1, P1, P2);
		float t3 = this.getT(t2, P2, P3);

		float t = t1 + (s * (float) numSections - (float) currPt) * (t2 - t1);

		double A1Mul1 = (t1 - t) / (t1 - t0);
		double A1Mul2 = (t - t0) / (t1 - t0);

		double A1x = P0.x * A1Mul1 + P1.x * A1Mul2;
		double A1y = P0.y * A1Mul1 + P1.y * A1Mul2;
		double A1z = P0.z * A1Mul1 + P1.z * A1Mul2;

		double A2Mul1 = (t2 - t) / (t2 - t1);
		double A2Mul2 = (t - t1) / (t2 - t1);

		double A2x = P1.x * A2Mul1 + P2.x * A2Mul2;
		double A2y = P1.y * A2Mul1 + P2.y * A2Mul2;
		double A2z = P1.z * A2Mul1 + P2.z * A2Mul2;

		double A3Mul1 = (t3 - t) / (t3 - t2);
		double A3Mul2 = (t - t2) / (t3 - t2);

		double A3x = P2.x * A3Mul1 + P3.x * A3Mul2;
		double A3y = P2.y * A3Mul1 + P3.y * A3Mul2;
		double A3z = P2.z * A3Mul1 + P3.z * A3Mul2;

		double B1Mul1 = (t2 - t) / (t2 - t0);
		double B1Mul2 = (t - t0) / (t2 - t0);

		double B1x = A1x * B1Mul1 + A2x * B1Mul2;
		double B1y = A1y * B1Mul1 + A2y * B1Mul2;
		double B1z = A1z * B1Mul1 + A2z * B1Mul2;

		double B2Mul1 = (t3 - t) / (t3 - t1);
		double B2Mul2 = (t - t1) / (t3 - t1);

		double B2x = A2x * B2Mul1 + A3x * B2Mul2;
		double B2y = A2y * B2Mul1 + A3y * B2Mul2;
		double B2z = A2z * B2Mul1 + A3z * B2Mul2;

		double CMul1 = (t2 - t) / (t2 - t1);
		double CMul2 = (t - t1) / (t2 - t1);

		double Cx = B1x * CMul1 + B2x * CMul2;
		double Cy = B1y * CMul1 + B2y * CMul2;
		double Cz = B1z * CMul1 + B2z * CMul2;

		Vec3 C = new Vec3(Cx, Cy, Cz);

		return C;
	}

	@Override
	public Vec3 derivative(float s) {
		int numSections = pts.length - 3;
		int currPt = (int) Math.min(Math.floor(s * (float) numSections), numSections - 1);

		Vec3 P0 = this.pts[currPt];
		Vec3 P1 = this.pts[currPt + 1];
		Vec3 P2 = this.pts[currPt + 2];
		Vec3 P3 = this.pts[currPt + 3];

		float t0 = 0.0F;
		float t1 = this.getT(t0, P0, P1);
		float t2 = this.getT(t1, P1, P2);
		float t3 = this.getT(t2, P2, P3);

		float t = t1 + (s * (float) numSections - (float) currPt) * (t2 - t1);

		double A1Mul1 = (t1 - t) / (t1 - t0);
		double A1Mul2 = (t - t0) / (t1 - t0);

		double A1x = P0.x * A1Mul1 + P1.x * A1Mul2;
		double A1y = P0.y * A1Mul1 + P1.y * A1Mul2;
		double A1z = P0.z * A1Mul1 + P1.z * A1Mul2;

		double A2Mul1 = (t2 - t) / (t2 - t1);
		double A2Mul2 = (t - t1) / (t2 - t1);

		double A2x = P1.x * A2Mul1 + P2.x * A2Mul2;
		double A2y = P1.y * A2Mul1 + P2.y * A2Mul2;
		double A2z = P1.z * A2Mul1 + P2.z * A2Mul2;

		double A3Mul1 = (t3 - t) / (t3 - t2);
		double A3Mul2 = (t - t2) / (t3 - t2);

		double A3x = P2.x * A3Mul1 + P3.x * A3Mul2;
		double A3y = P2.y * A3Mul1 + P3.y * A3Mul2;
		double A3z = P2.z * A3Mul1 + P3.z * A3Mul2;

		double B1Mul1 = (t2 - t) / (t2 - t0);
		double B1Mul2 = (t - t0) / (t2 - t0);

		double B1x = A1x * B1Mul1 + A2x * B1Mul2;
		double B1y = A1y * B1Mul1 + A2y * B1Mul2;
		double B1z = A1z * B1Mul1 + A2z * B1Mul2;

		double B2Mul1 = (t3 - t) / (t3 - t1);
		double B2Mul2 = (t - t1) / (t3 - t1);

		double B2x = A2x * B2Mul1 + A3x * B2Mul2;
		double B2y = A2y * B2Mul1 + A3y * B2Mul2;
		double B2z = A2z * B2Mul1 + A3z * B2Mul2;

		double dA1Mul = 1.0D / (t1 - t0);
		double dA2Mul = 1.0D / (t2 - t1);
		double dA3Mul = 1.0D / (t3 - t2);

		double dA1x = (P1.x - P0.x) * dA1Mul;
		double dA1y = (P1.y - P0.y) * dA1Mul;
		double dA1z = (P1.z - P0.z) * dA1Mul;

		double dA2x = (P2.x - P1.x) * dA2Mul;
		double dA2y = (P2.y - P1.y) * dA2Mul;
		double dA2z = (P2.z - P1.z) * dA2Mul;

		double dA3x = (P3.x - P2.x) * dA3Mul;
		double dA3y = (P3.y - P2.y) * dA3Mul;
		double dA3z = (P3.z - P2.z) * dA3Mul;

		double dB1Mul1 = 1.0D / (t2 - t0);
		double dB1Mul2 = (t2 - t) / (t2 - t0);
		double dB1Mul3 = (t - t0) / (t2 - t0);

		double dB1x = (A2x - A1x) * dB1Mul1 + dA1x * dB1Mul2 + dA2x * dB1Mul3;
		double dB1y = (A2y - A1y) * dB1Mul1 + dA1y * dB1Mul2 + dA2y * dB1Mul3;
		double dB1z = (A2z - A1z) * dB1Mul1 + dA1z * dB1Mul2 + dA2z * dB1Mul3;

		double dB2Mul1 = 1.0D / (t3 - t1);
		double dB2Mul2 = (t3 - t) / (t3 - t1);
		double dB2Mul3 = (t - t1) / (t3 - t1);

		double dB2x = (A3x - A2x) * dB2Mul1 + dA2x * dB2Mul2 + dA3x * dB2Mul3;
		double dB2y = (A3y - A2y) * dB2Mul1 + dA2y * dB2Mul2 + dA3y * dB2Mul3;
		double dB2z = (A3z - A2z) * dB2Mul1 + dA2z * dB2Mul2 + dA3z * dB2Mul3;

		double dCMul1 = 1.0D / (t2 - t1);
		double dCMul2 = (t2 - t) / (t2 - t1);
		double dCMul3 = (t - t1) / (t2 - t1);

		double dCx = (B2x - B1x) * dCMul1 + dB1x * dCMul2 + dB2x * dCMul3;
		double dCy = (B2y - B1y) * dCMul1 + dB1y * dCMul2 + dB2y * dCMul3;
		double dCz = (B2z - B1z) * dCMul1 + dB1z * dCMul2 + dB2z * dCMul3;

		Vec3 dC = new Vec3(dCx, dCy, dCz);

		return dC;
	}

	@Override
	public int getNumSegments() {
		return this.pts.length - 3;
	}

	private float getT(float t, Vec3 p0, Vec3 p1) {
		//alpha = 0.0F: standard
		//alpha = 0.5F: centripetal
		//alpha = 1.0F: chordal
		float alpha = 0.5F;

		return (float) Math.pow(p1.subtract(p0).length(), alpha) + t;
	}

	@Override
	public boolean hasArcLength() {
		return false;
	}

	@Override
	public double getArcLength() {
		return 0;
	}
}
