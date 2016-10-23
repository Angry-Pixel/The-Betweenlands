package thebetweenlands.common.entity.ai;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;

public class EntityAIFlyRandomly extends EntityAIBase {
	protected final EntityLiving entity;

	public EntityAIFlyRandomly(EntityLiving entity) {
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
	public boolean continueExecuting() {
		return false;
	}

	@Override
	public void startExecuting() {
		Random random = this.entity.getRNG();
		this.entity.getMoveHelper().setMoveTo(this.getRandomX(random), this.getRandomY(random), this.getRandomZ(random), this.getFlightSpeed());
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
	 * @return
	 */
	protected double getRandomX(Random rand) {
		return this.entity.posX + (double)((rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
	}

	/**
	 * Returns the random position in the Y direction
	 * @return
	 */
	protected double getRandomY(Random rand) {
		return this.entity.posY + (double)((rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
	}

	/**
	 * Returns the random position in the Z direction
	 * @return
	 */
	protected double getRandomZ(Random rand) {
		return this.entity.posZ + (double)((rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
	}
}
