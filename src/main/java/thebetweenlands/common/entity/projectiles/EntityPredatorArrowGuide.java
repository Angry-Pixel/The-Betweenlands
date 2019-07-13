package thebetweenlands.common.entity.projectiles;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPredatorArrowGuide extends Entity {
	public EntityPredatorArrowGuide(World world) {
		super(world);
		this.setSize(0.1F, 0.1F);
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		Entity mountedEntity = this.getRidingEntity();
		if(mountedEntity == null) {
			if(!this.world.isRemote) {
				this.setDead();
			}
		} else {
			this.updateHomingTrajectory(mountedEntity);
		}
	}

	protected void updateHomingTrajectory(Entity mountedEntity) {
		if(!this.world.isRemote) {
			float maxFollowReach = 24.0F;

			float maxFollowAngle = 20.0F;

			float correctionMultiplier = 0.15F;

			Vec3d motion = new Vec3d(mountedEntity.motionX, mountedEntity.motionY, mountedEntity.motionZ);
			double speed = motion.length();
			Vec3d heading = motion.normalize();

			List<EntityLivingBase> nearbyEntities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(maxFollowReach), entity -> entity.isEntityAlive() && entity instanceof IMob);

			EntityLivingBase bestTarget = null;
			double bestTargetScore = Double.MAX_VALUE;

			for(EntityLivingBase entity : nearbyEntities) {
				double dist = entity.getDistance(mountedEntity);
				if(dist < maxFollowReach) {
					Vec3d dir = entity.getPositionEyes(1).subtract(mountedEntity.getPositionEyes(1)).normalize();

					double angle = Math.toDegrees(Math.acos(heading.dotProduct(dir)));
					if(angle < maxFollowAngle) {
						double score = dist * Math.max(angle, 0.01D);

						if(bestTarget == null || score < bestTargetScore) {
							bestTarget = entity;
							bestTargetScore = score;
						}
					}
				}
			}

			if(bestTarget != null) {
				Vec3d newMotion = bestTarget.getPositionEyes(1).subtract(mountedEntity.getPositionEyes(1)).normalize().scale(speed * correctionMultiplier).add(motion.scale(1 - correctionMultiplier));
				mountedEntity.motionX = newMotion.x;
				mountedEntity.motionY = newMotion.y;
				mountedEntity.motionZ = newMotion.z;
				mountedEntity.velocityChanged = true;
			}
		}
	}
}
