package thebetweenlands.util.phys;

import java.util.Arrays;

// https://github.com/myphysicslab/myphysicslab/blob/03c811f465f7f343578e5bb3fbc42e8fedf913bf/src/lab/model/EulersMethod.js
public final class EulersSolver implements DiffEqSolver {
	private final Simulation sim;

	private float[] inp = new float[0];

	private float[] k1 = new float[0];

	public EulersSolver(final Simulation sim) {
		this.sim = sim;
	}

	@Override
	public void step(final float stepSize) {
		final float[] vars = this.sim.getState();
		final int N = vars.length;
		if (this.inp.length < N) {
			this.inp = new float[N];
			this.k1 = new float[N];
		}
		final float[] inp = this.inp;
		final float[] k1 = this.k1;
		System.arraycopy(vars, 0, inp, 0, N);
		Arrays.fill(k1, 0.0F);
		this.sim.evaluate(inp, k1);
		for (int i = 0; i < N; i++) {
			vars[i] += k1[i] * stepSize;
		}
	}
}
