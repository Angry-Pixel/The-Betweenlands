package thebetweenlands.util;

import net.minecraft.util.math.Vec3d;

public class ReparameterizedSpline implements ISpline {
	private final ISpline spline;

	private double length;

	private CatmullRomSpline reparameterizedInterpol;

	private double[] segmentLengths;

	private int segmentSubdivs;
	private double epsilon = 1e-3;
	private int maxNewtonIter = 16;

	public ReparameterizedSpline(ISpline spline) {
		this.spline = spline;
	}

	public ReparameterizedSpline init(int subdivs, int segmentSubdivs, double epsilon, int maxNewtonIter) {
		this.init(subdivs, segmentSubdivs);
		this.epsilon = epsilon;
		this.maxNewtonIter = maxNewtonIter;
		return this;
	}

	/**
	 * Initializes the reparameterized spline
	 * @param subdivs The number of total subdivisions to sample velocity
	 * @param segmentSubdivs The number of segment subdivisions to approximate length
	 */
	public ReparameterizedSpline init(int subdivs, int segmentSubdivs) {
		this.segmentSubdivs = segmentSubdivs;

		this.segmentLengths = new double[this.spline.getNumSegments() + 1];
		this.segmentLengths[0] = 0.0D;
		double segmentDelta = 1.0D / this.spline.getNumSegments();
		for(int i = 1; i <= this.spline.getNumSegments(); i++) {
			this.segmentLengths[i] = this.segmentLengths[i - 1] + this.getArcLength(segmentDelta * (i - 1), segmentDelta * i, subdivs);
		}
		this.length = this.segmentLengths[this.spline.getNumSegments()];

		Vec3d[] arcLengths = new Vec3d[subdivs + 2];
		double delta = this.length / (subdivs - 1);
		for(int i = 0; i < subdivs; i++) {
			arcLengths[i+1] = new Vec3d(this.getCurveParameter(delta * i), 0.0D, 0.0D);
		}
		arcLengths[0] = arcLengths[subdivs + 1] = new Vec3d(0, 0, 0.000001F);
		//TODO Use simple lerp?
		this.reparameterizedInterpol = new CatmullRomSpline(arcLengths);
		return this;
	}

	@Override
	public Vec3d interpolate(float t) {
		return this.spline.interpolate(this.getInterpolatedCurveParameter(t * this.length));
	}

	@Override
	public Vec3d derivative(float t) {
		return this.spline.derivative(this.getInterpolatedCurveParameter(t * this.length));
	}

	@Override
	public Vec3d[] getNodes() {
		return this.spline.getNodes();
	}

	@Override
	public int getNumSegments() {
		return this.spline.getNumSegments();
	}

	private float getInterpolatedCurveParameter(double s) {
		float parameter;
		if(s <= 0.0D) {
			parameter = 0.0F;
		} else if(s >= this.length) {
			parameter = 1.0F;
		} else {
			parameter = (float)this.reparameterizedInterpol.interpolate((float)(s / this.length)).x;
		}
		return parameter;
	}

	/**
	 * Approximates the arc length of the spline by subdividing it
	 * @param t
	 * @return
	 */
	private double getArcLength(double start, double end, int subdivs) {
		double sum = 0.0D;
		double delta = (end - start) / subdivs;
		Vec3d prev = null;
		for(int i = 0; i <= subdivs; i++) {
			Vec3d interp = this.spline.interpolate((float)(start + (i * delta)));
			if(prev != null) {
				sum += prev.subtract(interp).lengthVector();
			}
			prev = interp;
		}
		return sum;
	}

	private double speed(double t) {
		return this.spline.derivative((float) t).lengthVector();
	}

	/**
	 * Approximates the interpolation parameter to get to the specified length of the spline
	 * https://www.geometrictools.com/Documentation/MovingAlongCurveSpecifiedSpeed.pdf
	 * @param s
	 * @return
	 */
	private double getCurveParameter(double s) {
		if(s <= 0.0D) {
			return 0.0D;
		} else if(s >= this.length) {
			return 1.0D;
		}

		//The segment we're currently in
		int segment;
		for(segment = 1; segment < this.spline.getNumSegments(); segment++) {
			if(s <= this.segmentLengths[segment]) {
				break;
			}
		}

		double segmentDelta = 1.0D / this.spline.getNumSegments();

		double t = (s - this.segmentLengths[segment - 1]) / this.length;

		double segmentT = segmentDelta * (segment - 1);

		double lower = 0.0D;
		double upper = segmentDelta;

		for(int i = 0; i < this.maxNewtonIter; i++) {
			double error = this.segmentLengths[segment - 1] + this.getArcLength(segmentT, segmentT + t, this.segmentSubdivs) - s;

			if(Math.abs(error) < this.epsilon) {
				return segmentT + t;
			}

			//Use Newton's method to find a better approximation
			double tCandidate = t - error / this.speed(segmentT + t);

			if(error > 0) {
				upper = t;

				if(tCandidate <= lower) {
					t = 0.5D * (upper + lower);
				} else {
					t = tCandidate;
				}
			} else {
				lower = t;

				if(tCandidate >= upper) {
					t = 0.5D * (upper + lower);
				} else {
					t = tCandidate;
				}
			}
		}

		return segmentT + t;
	}
	
	@Override
	public boolean hasArcLength() {
		return true;
	}

	@Override
	public double getArcLength() {
		return this.length;
	}
}
