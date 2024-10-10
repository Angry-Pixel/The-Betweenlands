package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WaterStrollGoal extends RandomStrollGoal {

	public WaterStrollGoal(PathfinderMob mob, double speedModifier, int interval) {
		super(mob, speedModifier, interval);
	}

	@Override
	protected @Nullable Vec3 getPosition() {
		return DefaultRandomPos.getPos(this.mob, 16, 0);
	}
}
