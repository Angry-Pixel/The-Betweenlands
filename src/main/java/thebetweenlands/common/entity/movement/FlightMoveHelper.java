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
		IAttributeInstance entityMoveSpeedAttribute = this.entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		double entityMoveSpeed = entityMoveSpeedAttribute != null ? entityMoveSpeedAttribute.getAttributeValue() : 1.0D;
		double speed = this.getFlightSpeed() * entityMoveSpeed;

		if(this.action == EntityMoveHelper.Action.MOVE_TO) {
			double dx = this.posX - this.entity.posX;
			double dy = this.posY - this.entity.posY;
			double dz = this.posZ - this.entity.posZ;
			double dist = dx * dx + dy * dy + dz * dz;

			if(this.courseChangeCooldown-- <= 0) {
				this.courseChangeCooldown += this.getCourseChangeCooldown();

				dist = (double)MathHelper.sqrt(dist);

				if(dist < this.entity.width + speed) {
					this.blocked = false;
				} else if(this.isNotColliding(this.posX, this.posY, this.posZ, dist)) {
					if(dist < 0.01D) {
						this.entity.setMoveForward(0);
					} else {
						this.entity.motionX += dx / dist * speed;
						this.entity.motionY += dy / dist * speed;
						this.entity.motionZ += dz / dist * speed;

						float yaw = (float)(MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
						this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, yaw, 90.0F);

						this.entity.setAIMoveSpeed((float)speed);
					}

					this.blocked = false;
				} else {
					this.blocked = true;
				}
				
				this.action = EntityMoveHelper.Action.WAIT;
			}
		} else if(this.action == EntityMoveHelper.Action.STRAFE) {
			float forward = this.moveForward;
			float strafe = this.moveStrafe;
			float dist = MathHelper.sqrt(forward * forward + strafe * strafe);

			float rotX = MathHelper.sin(this.entity.rotationYaw * 0.017453292F);
			float rotZ = MathHelper.cos(this.entity.rotationYaw * 0.017453292F);
			float strafeX = strafe * rotZ - forward * rotX;
			float strafeZ = forward * rotZ + strafe * rotX;

			this.entity.motionX += strafeX / dist * speed * 0.15D;
			this.entity.motionZ += strafeZ / dist * speed * 0.15D;

			this.entity.setAIMoveSpeed((float)speed);
			this.entity.setMoveForward(this.moveForward);
			this.entity.setMoveStrafing(this.moveStrafe);
			
			this.action = EntityMoveHelper.Action.WAIT;
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