package thebetweenlands.util.phys;

public interface Simulation {
	float[] getState();

	void evaluate(final float[] state, final float[] rate);
}
