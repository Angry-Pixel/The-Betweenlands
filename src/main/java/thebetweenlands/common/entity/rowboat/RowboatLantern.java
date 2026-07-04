package thebetweenlands.common.entity.rowboat;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.phys.DiffEqSolver;
import thebetweenlands.util.phys.PendulumSimulation;
import thebetweenlands.util.phys.RungeKuttaSolver;

public class RowboatLantern {

	private static final float MOVE_FORCE = 6.0F;

	private final PendulumSimulation sim;

	private final DiffEqSolver solver;

	private float prevAngle;

	private Vec3 position = Vec3.ZERO;

	public RowboatLantern(final float length, final float friction) {
		this.sim = new PendulumSimulation(length, friction);
		this.solver = new RungeKuttaSolver(this.sim);
	}

	public float getAngle(final float t) {
		return MathUtils.lerpAngle(this.prevAngle, this.sim.getAngle(), t);
	}

	public void tick(final Vec3 position, final float yaw) {
		final Vec3 m = position.subtract(this.position);
		if (m.lengthSqr() < 1.0D) {
			this.move(m, yaw);
		}
		this.position = position;
		this.prevAngle = this.sim.getAngle();
		this.solver.step(1.0F / 20.0F);
	}

	private void move(final Vec3 motion, final float yaw) {
		float vx = Mth.sin(-yaw * Mth.DEG_TO_RAD - Mth.PI);
		float vz = Mth.cos(-yaw * Mth.DEG_TO_RAD - Mth.PI);
		this.sim.move(
			MOVE_FORCE * (float) ((vx * motion.x + vz * motion.z) / Mth.sqrt(vx * vx + vz * vz)),
			MOVE_FORCE * (float) motion.y
		);
	}
}
