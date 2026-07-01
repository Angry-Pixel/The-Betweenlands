package thebetweenlands.util.phys;

import java.util.Arrays;

// https://github.com/myphysicslab/myphysicslab/blob/03c811f465f7f343578e5bb3fbc42e8fedf913bf/src/lab/model/RungeKutta.js
public final class RungeKuttaSolver implements DiffEqSolver {
	private final Simulation sim;

	private float[] inp = new float[0];

	private float[] k1 = new float[0];

	private float[] k2 = new float[0];

	private float[] k3 = new float[0];

	private float[] k4 = new float[0];

	public RungeKuttaSolver(final Simulation sim) {
		this.sim = sim;
	}

	@Override
	public void step(final float stepSize) {
		final float[] vars = sim.getState();
		final int N = vars.length;
		if (this.inp.length < N) {
			this.inp = new float[N];
			this.k1 = new float[N];
			this.k2 = new float[N];
			this.k3 = new float[N];
			this.k4 = new float[N];
		}
		final float[] inp = this.inp;
		final float[] k1 = this.k1;
		final float[] k2 = this.k2;
		final float[] k3 = this.k3;
		final float[] k4 = this.k4;
		System.arraycopy(vars, 0, inp, 0, N);
		Arrays.fill(k1, 0.0F);
		this.sim.evaluate(inp, k1);
		for (int i = 0; i < N; i++) {
			inp[i] = vars[i] + k1[i] * stepSize / 2.0F;
		}
		Arrays.fill(k2, 0.0F);
		this.sim.evaluate(inp, k2);
		for (int i = 0; i < N; i++) {
			inp[i] = vars[i] + k2[i] * stepSize / 2.0F;
		}
		Arrays.fill(k3, 0.0F);
		this.sim.evaluate(inp, k3);
		for (int i = 0; i < N; i++) {
			inp[i] = vars[i] + k3[i] * stepSize;
		}
		Arrays.fill(k4, 0.0F);
		this.sim.evaluate(inp, k4);
		for (int i = 0; i < N; i++) {
			vars[i] += (k1[i] + 2.0F * k2[i] + 2.0F * k3[i] + k4[i]) * stepSize / 6.0F;
		}
	}
}
