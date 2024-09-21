package thebetweenlands.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class PredatorArrowGuide extends Entity {

	private static final EntityDataAccessor<Integer> TARGET = SynchedEntityData.defineId(PredatorArrowGuide.class, EntityDataSerializers.INT);

	private int cachedTargetId = -1;
	@Nullable
	private Entity cachedTarget;

	public PredatorArrowGuide(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(TARGET, -1);
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	public void tick() {
		super.tick();

		Entity mountedEntity = this.getVehicle();
		if (mountedEntity == null) {
			if (!this.level().isClientSide()) {
				this.discard();
			}
		} else {
			this.updateHomingTrajectory(mountedEntity);
		}
	}

	@Override
	public boolean shouldRender(double x, double y, double z) {
		Entity ridingEntity = this.getVehicle();
		return ridingEntity != null ? ridingEntity.shouldRender(x, y, z) : super.shouldRender(x, y, z);
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		Entity ridingEntity = this.getVehicle();
		return ridingEntity != null ? ridingEntity.shouldRenderAtSqrDistance(distance) : super.shouldRenderAtSqrDistance(distance);
	}

	public void setTarget(@Nullable Entity target) {
		this.getEntityData().set(TARGET, target == null ? -1 : target.getId());
	}

	@Nullable
	public Entity getTarget() {
		int targetId = this.getEntityData().get(TARGET);
		if (targetId >= 0) {
			if (targetId == this.cachedTargetId) {
				return this.cachedTarget;
			} else {
				return this.cachedTarget = this.level().getEntity(this.cachedTargetId = targetId);
			}
		}
		this.cachedTarget = null;
		this.cachedTargetId = -1;
		return null;
	}

	protected void updateHomingTrajectory(Entity mountedEntity) {
		if (!this.level().isClientSide()) {
			Vec3 motion = mountedEntity.getDeltaMovement();
			double speed = motion.length();

			LivingEntity bestTarget = null;

			float maxFollowReach = 24.0F;

			float maxFollowAngle = 20.0F;

			float correctionMultiplier = 0.15F;

			double distanceMovedSq = (mountedEntity.xo - mountedEntity.getX()) * (mountedEntity.xo - mountedEntity.getX()) + (mountedEntity.yo - mountedEntity.getY()) * (mountedEntity.yo - mountedEntity.getY()) + (mountedEntity.zo - mountedEntity.getZ()) * (mountedEntity.zo - mountedEntity.getZ());

			if (speed > 0.1D && distanceMovedSq > 0.01D && !mountedEntity.onGround()) {
				Vec3 heading = motion.normalize();

				List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(maxFollowReach), entity -> entity.isAlive() && entity instanceof Enemy);

				double bestTargetScore = Double.MAX_VALUE;

				for (LivingEntity entity : nearbyEntities) {
					double dist = entity.distanceTo(mountedEntity);
					if (dist < maxFollowReach) {
						Vec3 dir = entity.getEyePosition(1).subtract(mountedEntity.getEyePosition(1)).normalize();

						double angle = Math.toDegrees(Math.acos(heading.dot(dir)));
						if (angle < maxFollowAngle) {
							double score = dist * Math.max(angle, 0.01D);

							if (bestTarget == null || score < bestTargetScore) {
								if (entity.hasLineOfSight(mountedEntity)) {
									bestTarget = entity;
									bestTargetScore = score;
								}
							}
						}
					}
				}
			}

			this.setTarget(bestTarget);

			if (bestTarget != null) {
				Vec3 newMotion = bestTarget.getEyePosition(1).subtract(mountedEntity.getEyePosition(1)).normalize().scale(speed * correctionMultiplier).add(motion.scale(1 - correctionMultiplier));
				mountedEntity.setDeltaMovement(newMotion);
				mountedEntity.hurtMarked = true;
			}
		}
	}
}
