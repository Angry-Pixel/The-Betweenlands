package thebetweenlands.util.phys;

import net.minecraft.util.math.MathHelper;

// https://www.compadre.org/osp/items/detail.cfm?ID=14160
public final class PendulumSimulation implements Simulation {
	private static final float G = 9.8F;

	private final float length;

	private final float friction;

	private float moveAngle;

	private float moveForce;

	public float[] state;

	public PendulumSimulation(final float length, final float friction) {
		this.state = new float[3];
		this.length = length;
		this.friction = friction;
		this.moveForce = 0.0F;
		this.moveAngle = 0.0F;
	}

	public float getAngle() {
		return this.state[0];
	}

	public void move(final float x, final float y) {
		if (x != 0.0F || y != 0.0F) {
			this.moveAngle = -(float) MathHelper.atan2(x, -y);
			this.moveForce = MathHelper.sqrt(x * x + y * y);
		} else {
			this.moveAngle = 0.0F;
			this.moveForce = 0.0F;
		}
	}

	@Override
	public float[] getState() {
		return this.state;
	}

	@Override
	public void evaluate(final float[] state, final float[] rate) {
		final float theta = state[0];
		final float omega = state[1];
		final float alpha = -(this.moveForce / this.length) * MathHelper.sin(theta + this.moveAngle) -
			(G / this.length) * MathHelper.sin(theta) -
			this.friction * this.length * omega;
		rate[0] = omega;
		rate[1] = alpha;
		rate[2] = 1.0F;
	}
}
