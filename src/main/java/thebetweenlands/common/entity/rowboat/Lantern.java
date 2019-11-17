package thebetweenlands.common.entity.rowboat;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.phys.DiffEqSolver;
import thebetweenlands.util.phys.PendulumSimulation;
import thebetweenlands.util.phys.RungeKuttaSolver;

public final class Lantern {
	private static final float MOVE_FORCE = 6.0F;

	private final PendulumSimulation sim;

	private final DiffEqSolver solver;

	private float prevAngle;

	private Vec3d position = Vec3d.ZERO;

	public Lantern(final float length, final float friction) {
		this.sim = new PendulumSimulation(length, friction);
		this.solver = new RungeKuttaSolver(this.sim);
	}

	public float getAngle(final float t) {
		return MathUtils.lerpAngle(this.prevAngle, this.sim.getAngle(), t);
	}

	public void tick(final Vec3d position, final float yaw) {
		final Vec3d m = position.subtract(this.position);
		if (m.lengthSquared() < 1.0D) {
			this.move(m, yaw);
		}
		this.position = position;
		this.prevAngle = this.sim.getAngle();
		this.solver.step(1.0F / 20.0F);
	}

	private void move(final Vec3d motion, final float yaw) {
		float vx = MathHelper.sin(-yaw * MathUtils.DEG_TO_RAD - MathUtils.PI);
		float vz = MathHelper.cos(-yaw * MathUtils.DEG_TO_RAD - MathUtils.PI);
		this.sim.move(
			MOVE_FORCE * (float) ((vx * motion.x + vz * motion.z) / MathHelper.sqrt(vx * vx + vz * vz)),
			MOVE_FORCE * (float) motion.y
		);
	}
}
