package thebetweenlands.common.entity.movement;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class FlightMoveHelper extends EntityMoveHelper {
	protected int courseChangeCooldown;

	public FlightMoveHelper(EntityLiving entity) {
		super(entity);
	}

	@Override
	public void onUpdateMoveHelper() {
		if(this.action == EntityMoveHelper.Action.MOVE_TO) {
			double dx = this.posX - this.entity.posX;
			double dy = this.posY - this.entity.posY;
			double dz = this.posZ - this.entity.posZ;
			double distSq = dx * dx + dy * dy + dz * dz;

			if(this.courseChangeCooldown-- <= 0) {
				this.courseChangeCooldown += this.getCourseChangeCooldown();

				distSq = (double)MathHelper.sqrt_double(distSq);

				if(this.isNotColliding(this.posX, this.posY, this.posZ, distSq)) {
					this.entity.motionX += dx / distSq * this.speed;
					this.entity.motionY += dy / distSq * this.speed;
					this.entity.motionZ += dz / distSq * this.speed;
				} else {
					this.action = EntityMoveHelper.Action.WAIT;
				}
			}
		}
	}

	/**
	 * Returns the amount of ticks before the course can be changed again
	 * @return
	 */
	protected int getCourseChangeCooldown() {
		return this.entity.getRNG().nextInt(5) + 2;
	}

	/**
	 * Returns whether the entity will collide on the current path
	 * @param x
	 * @param y
	 * @param z
	 * @param step
	 * @return
	 */
	protected boolean isNotColliding(double x, double y, double z, double step) {
		double stepX = (x - this.entity.posX) / step;
		double stepY = (y - this.entity.posY) / step;
		double stepZ = (z - this.entity.posZ) / step;
		AxisAlignedBB aabb = this.entity.getEntityBoundingBox();

		for(int i = 1; (double)i < step; ++i) {
			aabb = aabb.offset(stepX, stepY, stepZ);

			if(this.isBlocked(aabb)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns whether the entities path is blocked at the specified AABB
	 * @param aabb
	 * @return
	 */
	protected boolean isBlocked(AxisAlignedBB aabb) {
		return !this.entity.worldObj.getCollisionBoxes(this.entity, aabb).isEmpty();
	}
}