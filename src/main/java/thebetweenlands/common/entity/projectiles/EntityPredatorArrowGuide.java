package thebetweenlands.common.entity.projectiles;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityPredatorArrowGuide extends Entity {
	private static final DataParameter<Integer> TARGET = EntityDataManager.createKey(EntityPredatorArrowGuide.class, DataSerializers.VARINT);

	private int cachedTargetId = -1;
	private Entity cachedTarget;

	public EntityPredatorArrowGuide(World world) {
		super(world);
		this.setSize(0.1F, 0.1F);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(TARGET, -1);
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
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

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		Entity ridingEntity = this.getRidingEntity();
		return ridingEntity != null ? ridingEntity.getRenderBoundingBox() : super.getRenderBoundingBox();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRender3d(double x, double y, double z) {
		Entity ridingEntity = this.getRidingEntity();
		return ridingEntity != null ? ridingEntity.isInRangeToRender3d(x, y, z) : super.isInRangeToRender3d(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		Entity ridingEntity = this.getRidingEntity();
		return ridingEntity != null ? ridingEntity.isInRangeToRenderDist(distance) : super.isInRangeToRenderDist(distance);
	}

	public void setTarget(@Nullable Entity target) {
		this.dataManager.set(TARGET, target == null ? -1 : target.getEntityId());
	}

	public Entity getTarget() {
		int targetId = this.dataManager.get(TARGET);
		if(targetId >= 0) {
			if(targetId == this.cachedTargetId) {
				return this.cachedTarget;
			} else {
				return this.cachedTarget = this.world.getEntityByID(this.cachedTargetId = targetId);
			}
		}
		this.cachedTarget = null;
		this.cachedTargetId = -1;
		return null;
	}

	protected void updateHomingTrajectory(Entity mountedEntity) {
		if(!this.world.isRemote) {
			Vec3d motion = new Vec3d(mountedEntity.motionX, mountedEntity.motionY, mountedEntity.motionZ);
			double speed = motion.length();

			EntityLivingBase bestTarget = null;

			float maxFollowReach = 24.0F;

			float maxFollowAngle = 20.0F;

			float correctionMultiplier = 0.15F;

			double distanceMovedSq = (mountedEntity.prevPosX - mountedEntity.posX) * (mountedEntity.prevPosX - mountedEntity.posX) + (mountedEntity.prevPosY - mountedEntity.posY) * (mountedEntity.prevPosY - mountedEntity.posY) + (mountedEntity.prevPosZ - mountedEntity.posZ) * (mountedEntity.prevPosZ - mountedEntity.posZ);

			if(speed > 0.1D && distanceMovedSq > 0.01D && !mountedEntity.onGround) {
				Vec3d heading = motion.normalize();

				List<EntityLivingBase> nearbyEntities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(maxFollowReach), entity -> entity.isEntityAlive() && entity instanceof IMob);

				double bestTargetScore = Double.MAX_VALUE;

				for(EntityLivingBase entity : nearbyEntities) {
					double dist = entity.getDistance(mountedEntity);
					if(dist < maxFollowReach) {
						Vec3d dir = entity.getPositionEyes(1).subtract(mountedEntity.getPositionEyes(1)).normalize();

						double angle = Math.toDegrees(Math.acos(heading.dotProduct(dir)));
						if(angle < maxFollowAngle) {
							double score = dist * Math.max(angle, 0.01D);

							if(bestTarget == null || score < bestTargetScore) {
								if(entity.canEntityBeSeen(mountedEntity)) {
									bestTarget = entity;
									bestTargetScore = score;
								}
							}
						}
					}
				}
			}

			this.setTarget(bestTarget);

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
