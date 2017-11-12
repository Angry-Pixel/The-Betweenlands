package thebetweenlands.common.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class EntityAIFlyingWander extends EntityAIBase {
	protected final EntityCreature entity;
	protected double x;
	protected double y;
	protected double z;
	protected final double speed;

	public EntityAIFlyingWander(EntityCreature creatureIn, double speedIn) {
		entity = creatureIn;
		speed = speedIn;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		Vec3d vec3d = getPosition();

		if (vec3d == null)
			return false;
		else {
			x = vec3d.x;
			y = vec3d.y;
			z = vec3d.z;
			return true;
		}
	}

	@Nullable
	protected Vec3d getPosition() {
		return RandomPositionGenerator.findRandomTarget(entity, 15, 4);
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !entity.getNavigator().noPath() && !entity.getMoveHelper().isUpdating();
	}

	@Override
	public void startExecuting() {
		if(!entity.getMoveHelper().isUpdating())
			entity.getNavigator().tryMoveToXYZ(x, y, z, speed);
	}
}
