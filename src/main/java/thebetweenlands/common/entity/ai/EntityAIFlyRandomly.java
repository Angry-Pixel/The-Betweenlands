package thebetweenlands.common.entity.ai;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import thebetweenlands.common.entity.movement.FlightMoveHelper;

public class EntityAIFlyRandomly<T extends EntityLiving> extends EntityAIBase {
	protected final T entity;
	protected double distanceMultiplier = 1.0D;

	public EntityAIFlyRandomly(T entity) {
		this.entity = entity;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		EntityMoveHelper entitymovehelper = this.entity.getMoveHelper();

		if (!entitymovehelper.isUpdating()) {
			return true;
		} else {
			double dx = entitymovehelper.getX() - this.entity.posX;
			double dy = entitymovehelper.getY() - this.entity.posY;
			double dz = entitymovehelper.getZ() - this.entity.posZ;

			double distSq = dx * dx + dy * dy + dz * dz;

			return distSq < 1.0D || distSq > 3600.0D;
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		//Only run startExecuting once and then stop the task!
		return false;
	}

	@Override
	public void startExecuting() {
		Random random = this.entity.getRNG();
		this.entity.getMoveHelper().setMoveTo(this.getTargetX(random, this.distanceMultiplier), this.getTargetY(random, this.distanceMultiplier), this.getTargetZ(random, this.distanceMultiplier), this.getFlightSpeed());
	}

	@Override
	public void updateTask() {
		EntityMoveHelper moveHelper = this.entity.getMoveHelper();
		if(moveHelper instanceof FlightMoveHelper) {
			FlightMoveHelper flightMoveHelper = (FlightMoveHelper) moveHelper;

			if(flightMoveHelper.isBlocked()) {
				this.distanceMultiplier = Math.max(this.distanceMultiplier -= 0.04F, 0.1D);
			} else {
				this.distanceMultiplier = Math.min(this.distanceMultiplier += 0.15F, 1.0D);
			}
		}
	}

	/**
	 * Returns the flight speed
	 * @return
	 */
	protected double getFlightSpeed() {
		return 1.0D;
	}

	/**
	 * Returns the random position in the X direction
	 * @param rand Random
	 * @param distanceMultiplier Decreases if path is blocked or can't find a valid path. Ranges from 0.1 to 1.0
	 * @return
	 */
	protected double getTargetX(Random rand, double distanceMultiplier) {
		return this.entity.posX + (double)((rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier);
	}

	/**
	 * Returns the random position in the Y direction
	 * @param rand Random
	 * @param distanceMultiplier Decreases if path is blocked or can't find a valid path. Ranges from 0.1 to 1.0
	 * @return
	 */
	protected double getTargetY(Random rand, double distanceMultiplier) {
		return this.entity.posY + (double)((rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier);
	}

	/**
	 * Returns the random position in the Z direction
	 * @param rand Random
	 * @param distanceMultiplier Decreases if path is blocked or can't find a valid path. Ranges from 0.1 to 1.0
	 * @return
	 */
	protected double getTargetZ(Random rand, double distanceMultiplier) {
		return this.entity.posZ + (double)((rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier);
	}
}
