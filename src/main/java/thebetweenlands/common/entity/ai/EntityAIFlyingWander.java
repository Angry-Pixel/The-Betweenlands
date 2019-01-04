package thebetweenlands.common.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.movement.FlightMoveHelper;

public class EntityAIFlyingWander extends EntityAIBase {
	protected final EntityCreature entity;
	protected double x;
	protected double y;
	protected double z;
	protected final double speed;
	protected int executionChance;
	protected boolean mustUpdate;

	public EntityAIFlyingWander(EntityCreature creatureIn, double speedIn) {
		this(creatureIn, speedIn, 10);
	}

	public EntityAIFlyingWander(EntityCreature creatureIn, double speedIn, int chance) {
		this.entity = creatureIn;
		this.speed = speedIn;
		this.executionChance = chance;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (!this.mustUpdate) {
			if (this.entity.getRNG().nextInt(this.executionChance) != 0) {
				return false;
			}
		}

		Vec3d randomPosition = getPosition();

		if (randomPosition == null) {
			return false;
		} else {
			BlockPos groundHeight = FlightMoveHelper.getGroundHeight(this.entity.world, new BlockPos(randomPosition), 8, new BlockPos(0, 0, 0));

			if(randomPosition.y - groundHeight.getY() >= 8) {
				randomPosition = randomPosition.add(0, -6, 0);
			}

			this.x = randomPosition.x;
			this.y = randomPosition.y;
			this.z = randomPosition.z;
			this.mustUpdate = false;
			return true;
		}
	}

	@Nullable
	protected Vec3d getPosition() {
		return RandomPositionGenerator.findRandomTarget(this.entity, 15, 4);
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.entity.getNavigator().noPath() && !this.entity.getMoveHelper().isUpdating();
	}

	@Override
	public void startExecuting() {
		if(!this.entity.getMoveHelper().isUpdating()) {
			this.entity.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, this.speed);
		}
	}

	public void makeUpdate() {
		this.mustUpdate = true;
	}
}
