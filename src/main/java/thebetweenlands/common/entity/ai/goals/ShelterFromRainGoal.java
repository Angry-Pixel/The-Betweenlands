package thebetweenlands.common.entity.ai.goals;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ShelterFromRainGoal extends Goal {
	private final PathfinderMob creature;
	private double shelterX;
	private double shelterY;
	private double shelterZ;
	private final double movementSpeed;
	private final Level level;

	public ShelterFromRainGoal(PathfinderMob creature, double movementSpeed) {
		this.creature = creature;
		this.movementSpeed = movementSpeed;
		this.level = creature.level();
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		BlockPos pos = BlockPos.containing(this.creature.getX(), this.creature.getBoundingBox().minY, this.creature.getZ());
		if (!this.level.isRainingAt(pos) && !this.level.isRainingAt(pos.above())) {
			return false;
		} else {
			Vec3 vec3d = this.findPossibleShelter();

			if (vec3d == null) {
				return false;
			} else {
				this.shelterX = vec3d.x;
				this.shelterY = vec3d.y;
				this.shelterZ = vec3d.z;
				return true;
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		return !this.creature.getNavigation().isDone();
	}

	@Override
	public void start() {
		this.creature.getNavigation().moveTo(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
	}

	@Nullable
	private Vec3 findPossibleShelter() {
		RandomSource random = this.creature.getRandom();
		BlockPos pos = BlockPos.containing(this.creature.getX(), this.creature.getBoundingBox().minY, this.creature.getZ());

		for (int i = 0; i < 10; ++i) {
			BlockPos offsetPos = pos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

			if (!this.level.isRainingAt(offsetPos) && this.level.isEmptyBlock(offsetPos)) {
				return new Vec3((double)offsetPos.getX() + 0.5D, (double)offsetPos.getY() + 0.5D, (double)offsetPos.getZ() + 0.5D);
			}
		}

		return null;
	}
}