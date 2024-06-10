package thebetweenlands.common.entitys.movement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.TheBetweenlands;

public class FlightMoveHelper extends MoveControl {
	protected int courseChangeCooldown;
	protected boolean blocked = false;
	
	public FlightMoveHelper(Mob p_24983_) {
		super(p_24983_);
	}
	
	@Override
	public void tick() {
		AttributeInstance entityMoveSpeedAttribute = this.mob.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
		double entityMoveSpeed = entityMoveSpeedAttribute != null ? entityMoveSpeedAttribute.getValue() : 1.0D;
		double speed = this.getFlightSpeed() * entityMoveSpeed;

		if(this.operation == Operation.MOVE_TO) {
			double dx = this.wantedX - this.mob.getX();
			double dy = this.wantedY - this.mob.getY();
			double dz = this.wantedZ - this.mob.getZ();
			double dist = dx * dx + dy * dy + dz * dz;

			if(this.courseChangeCooldown-- <= 0) {
				this.courseChangeCooldown += this.getCourseChangeCooldown();

				dist = (double)Math.sqrt(dist);
				
				if(this.isNotColliding(this.wantedX, this.wantedY, this.wantedZ, dist)) {
					if(dist < this.mob.getBbWidth() + speed) {
						speed *= dist / (this.mob.getBbWidth() + speed);
					}
					
					if(dist < 0.01D) {
						this.mob.setZza(0);
						this.operation = Operation.WAIT;
					} else {
						this.mob.xxa += dy / dist * speed;
						this.mob.yya += dx / dist * speed;
						this.mob.zza += dz / dist * speed;

						float yaw = (float)(Math.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
						this.mob.setXRot(this.rotlerp(this.mob.getXRot(), yaw, 90.0F));

						this.mob.setSpeed((float)speed);
					}

					this.blocked = false;
				} else {
					this.blocked = true;
				}
				
				if(this.blocked) {
					this.operation = Operation.WAIT;
				}
			}
		} else if(this.operation == Operation.STRAFE) {
			float forward = this.strafeForwards;
			float strafe = this.strafeRight;
			float dist = (float) Math.sqrt(forward * forward + strafe * strafe);

			float rotX = (float) Math.sin(this.mob.getXRot() * 0.017453292F);
			float rotZ = (float) Math.cos(this.mob.getXRot() * 0.017453292F);
			float strafeX = strafe * rotZ - forward * rotX;
			float strafeZ = forward * rotZ + strafe * rotX;

			this.mob.xxa += strafeX / dist * speed * 0.15D;
			this.mob.zza += strafeZ / dist * speed * 0.15D;

			this.mob.setSpeed((float)speed);
			this.mob.setZza((float)speed*this.strafeForwards);
			this.mob.setXxa((float)speed*this.strafeRight);
			
			this.operation = Operation.WAIT;
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
		return this.mob.getRandom().nextInt(5) + 2;
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
		if(!this.mob.noPhysics)
			return true;

		double stepX = (x - this.mob.getX()) / step;
		double stepY = (y - this.mob.getY()) / step;
		double stepZ = (z - this.mob.getZ()) / step;
		AABB aabb = this.mob.getBoundingBox();

		for(int i = 1; (double)i < step; ++i) {
			aabb = aabb.move(stepX, stepY, stepZ);

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
	protected boolean isBlocked(AABB aabb) {
		return false;
	}

	/**
	 * Returns the flight speed
	 * @return
	 */
	protected double getFlightSpeed() {
		return this.getSpeedModifier();
	}

	/**
	 * Returns the ground height at the specified block position
	 * @param world
	 * @param pos
	 * @param maxIter
	 * @param fallback
	 * @return
	 */
	public static BlockPos getGroundHeight(Level world, BlockPos pos, int maxIter, BlockPos fallback) {
		if(world.canSeeSky(pos)) {
			return world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
		}
		MutableBlockPos mutablePos = new MutableBlockPos();
		int i = 0;
		for(; i < maxIter; i++) {
			mutablePos.set(pos.getX(), pos.getY() - i, pos.getZ());
			if(!world.isEmptyBlock(mutablePos))
				break;
		}
		if(i < maxIter) {
			return new BlockPos(pos.getX(), pos.getY() - i, pos.getZ());
		}
		return fallback;
	}

}
