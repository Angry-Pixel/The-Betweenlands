package thebetweenlands.common.entity.ai;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAISeekRainShelter extends EntityAIBase {
	private final EntityCreature creature;
	private double shelterX;
	private double shelterY;
	private double shelterZ;
	private final double movementSpeed;
	private final World world;

	public EntityAISeekRainShelter(EntityCreature creature, double movementSpeed) {
		this.creature = creature;
		this.movementSpeed = movementSpeed;
		this.world = creature.world;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		BlockPos pos = new BlockPos(this.creature.posX, this.creature.getEntityBoundingBox().minY, this.creature.posZ);
		if (!this.world.isRainingAt(pos) && !this.world.isRainingAt(pos.up())) {
			return false;
		} else {
			Vec3d vec3d = this.findPossibleShelter();

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
	public boolean shouldContinueExecuting() {
		return !this.creature.getNavigator().noPath();
	}

	@Override
	public void startExecuting() {
		this.creature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
	}

	@Nullable
	private Vec3d findPossibleShelter() {
		Random random = this.creature.getRNG();
		BlockPos pos = new BlockPos(this.creature.posX, this.creature.getEntityBoundingBox().minY, this.creature.posZ);

		for (int i = 0; i < 10; ++i) {
			BlockPos offsetPos = pos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

			if (!this.world.isRainingAt(offsetPos) && this.world.isAirBlock(offsetPos)) {
				return new Vec3d((double)offsetPos.getX() + 0.5D, (double)offsetPos.getY() + 0.5D, (double)offsetPos.getZ() + 0.5D);
			}
		}

		return null;
	}
}