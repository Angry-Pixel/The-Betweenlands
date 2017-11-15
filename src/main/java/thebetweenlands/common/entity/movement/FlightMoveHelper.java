package thebetweenlands.common.entity.movement;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FlightMoveHelper extends EntityMoveHelper {
	protected int courseChangeCooldown;
	protected boolean blocked = false;

	public FlightMoveHelper(EntityLiving entity) {
		super(entity);
	}

	@Override
	public void onUpdateMoveHelper() {
		if(this.action == EntityMoveHelper.Action.MOVE_TO) {
			double dx = this.posX - this.entity.posX;
			double dy = this.posY + 0.5D - this.entity.posY;
			double dz = this.posZ - this.entity.posZ;
			double dist = dx * dx + dy * dy + dz * dz;

			if(this.courseChangeCooldown-- <= 0) {
				this.courseChangeCooldown += this.getCourseChangeCooldown();

				dist = (double)MathHelper.sqrt(dist);
				
				IAttributeInstance entityMoveSpeedAttribute = this.entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
				double entityMoveSpeed = entityMoveSpeedAttribute != null ? entityMoveSpeedAttribute.getAttributeValue() : 1.0D;
				double speed = this.getFlightSpeed() * entityMoveSpeed;
				
				if(dist >= Math.max(speed * 1.5D, 0.25D) && this.isNotColliding(this.posX, this.posY, this.posZ, dist)) {
					this.entity.motionX += dx / dist * speed;
					this.entity.motionY += dy / dist * speed;
					this.entity.motionZ += dz / dist * speed;
					this.blocked = false;
				} else {
					this.action = EntityMoveHelper.Action.WAIT;
					this.blocked = true;
				}
			}
		} else if(this.action == EntityMoveHelper.Action.STRAFE) {
			
		}
	}

	/**
	 * Returns whether the path is currently blocked
	 * @return
	 */
	public boolean isBlocked() {
		return this.blocked;
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
		if(this.entity.noClip)
			return true;

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
		return false;
	}

	/**
	 * Returns the flight speed
	 * @return
	 */
	protected double getFlightSpeed() {
		return this.speed;
	}

	/**
	 * Returns the ground height at the specified block position
	 * @param world
	 * @param pos
	 * @param maxIter
	 * @param fallback
	 * @return
	 */
	public static BlockPos getGroundHeight(World world, BlockPos pos, int maxIter, BlockPos fallback) {
		if(world.canSeeSky(pos)) {
			return world.getHeight(pos);
		}
		MutableBlockPos mutablePos = new MutableBlockPos();
		int i = 0;
		for(; i < maxIter; i++) {
			mutablePos.setPos(pos.getX(), pos.getY() - i, pos.getZ());
			if(!world.isAirBlock(mutablePos))
				break;
		}
		if(i < maxIter) {
			return new BlockPos(pos.getX(), pos.getY() - i, pos.getZ());
		}
		return fallback;
	}
}