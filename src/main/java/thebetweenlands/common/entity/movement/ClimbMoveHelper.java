package thebetweenlands.common.entity.movement;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityClimber;

public class ClimbMoveHelper extends EntityMoveHelper {
	protected int courseChangeCooldown;
	protected boolean blocked = false;

	protected final EntityClimber climber;
	
	public ClimbMoveHelper(EntityClimber entity) {
		super(entity);
		this.climber = entity;
	}

	@Override
	public void onUpdateMoveHelper() {
		IAttributeInstance entityMoveSpeedAttribute = this.entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		double speed = entityMoveSpeedAttribute != null ? entityMoveSpeedAttribute.getAttributeValue() : 1.0D;

		if(this.action == EntityMoveHelper.Action.MOVE_TO) {
			double dx = this.posX - this.entity.posX;
			double dy = this.posY - this.entity.posY;
			double dz = this.posZ - this.entity.posZ;

			Vec3d dir = new Vec3d(dx, dy, dz);
			
			Pair<EnumFacing, Vec3d> walkingSide = this.climber.getWalkingSide();
			Vec3d normal = new Vec3d(walkingSide.getLeft().getXOffset(), walkingSide.getLeft().getYOffset(), walkingSide.getLeft().getZOffset());
			
			Vec3d walkingDir = dir.subtract(normal.scale(dir.dotProduct(normal)));
			
			double walkingDist = walkingDir.length();
			
			if(walkingDist < 0.1D) {
				this.entity.setMoveForward(0);
				this.action = EntityMoveHelper.Action.WAIT;
			} else {
				this.entity.motionX += walkingDir.x / walkingDist * speed;
				this.entity.motionY += walkingDir.y / walkingDist * speed;
				this.entity.motionZ += walkingDir.z / walkingDist * speed;

				this.entity.setAIMoveSpeed((float)speed);
			}
			
			/*if(this.courseChangeCooldown-- <= 0) {
				this.courseChangeCooldown += this.getCourseChangeCooldown();

				dist = (double)MathHelper.sqrt(dist);
				
				if(this.isNotColliding(this.posX, this.posY, this.posZ, dist)) {
					if(dist < this.entity.width + speed) {
						speed *= dist / (this.entity.width + speed);
					}
					
					if(dist < 0.01D) {
						this.entity.setMoveForward(0);
						this.action = EntityMoveHelper.Action.WAIT;
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
				
				if(this.blocked) {
					this.action = EntityMoveHelper.Action.WAIT;
				}
			}*/
		}
		
		/*if(this.action == EntityMoveHelper.Action.MOVE_TO) {
			double dx = this.posX - this.entity.posX;
			double dy = this.posY - this.entity.posY;
			double dz = this.posZ - this.entity.posZ;
			double dist = dx * dx + dy * dy + dz * dz;

			if(this.courseChangeCooldown-- <= 0) {
				this.courseChangeCooldown += this.getCourseChangeCooldown();

				dist = (double)MathHelper.sqrt(dist);
				
				if(this.isNotColliding(this.posX, this.posY, this.posZ, dist)) {
					if(dist < this.entity.width + speed) {
						speed *= dist / (this.entity.width + speed);
					}
					
					if(dist < 0.01D) {
						this.entity.setMoveForward(0);
						this.action = EntityMoveHelper.Action.WAIT;
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
				
				if(this.blocked) {
					this.action = EntityMoveHelper.Action.WAIT;
				}
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
		}*/
	}
}