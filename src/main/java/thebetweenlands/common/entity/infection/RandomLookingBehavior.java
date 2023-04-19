package thebetweenlands.common.entity.infection;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.entity.IInfectionBehaviorOverlay;
import thebetweenlands.common.network.datamanager.GenericDataManager;

public class RandomLookingBehavior extends AbstractInfectionBehavior implements IInfectionBehaviorOverlay {

	private static final DataParameter<Float> YAW_OFFSET = GenericDataManager.createKey(RandomLookingBehavior.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> PITCH_OFFSET = GenericDataManager.createKey(RandomLookingBehavior.class, DataSerializers.FLOAT);

	private static final DataParameter<Integer> DURATION = GenericDataManager.createKey(RandomLookingBehavior.class, DataSerializers.VARINT);

	private int ticks = 0;

	private int lookTicks;

	private int easeType;

	private float prevYawOffset;
	private float prevPitchOffset;

	private EntityLivingBase entityOfInterest;

	public RandomLookingBehavior(EntityLivingBase entity) {
		super(entity);
		this.dataManager.register(DURATION, 0);
		this.dataManager.register(YAW_OFFSET, 0.0f);
		this.dataManager.register(PITCH_OFFSET, 0.0f);
	}

	@Override
	public boolean onParameterChange(DataParameter<?> key, Object value, boolean fromPacket) {
		if(key == DURATION) {
			this.lookTicks = 1;
			this.prevYawOffset = 0;
			this.prevPitchOffset = 0;
			this.easeType = 0;
		}
		return false;
	}

	@Override
	public void update() {
		++this.ticks;

		if(this.lookTicks > 0) {
			++this.lookTicks;

			if(this.lookTicks > this.dataManager.get(DURATION) + 5) {
				this.dataManager.set(DURATION, 0);
				this.dataManager.set(YAW_OFFSET, 0.0f);
				this.dataManager.set(PITCH_OFFSET, 0.0f);
				this.lookTicks = 0;
			}
		}

		if(!this.world.isRemote) {
			if(this.entityOfInterest != null && !this.entityOfInterest.isEntityAlive()) {
				this.entityOfInterest = null;
			}

			if(this.lookTicks == 0) {
				if(this.world.rand.nextInt(40) == 0) {
					this.dataManager.set(DURATION, 15 + this.world.rand.nextInt(40));
					this.dataManager.set(YAW_OFFSET, (this.world.rand.nextFloat() - 0.5f) * 30.0f);
					this.dataManager.set(PITCH_OFFSET, (this.world.rand.nextFloat() - 0.5f) * 30.0f);
				} else if(this.world.rand.nextInt(this.entityOfInterest != null ? 30 : 80) == 0) {
					EntityLivingBase entity = this.entityOfInterest != null ? this.entityOfInterest : this.findEntityOfInterest();

					if(entity != null) {
						if(this.world.rand.nextInt(10) == 0) {
							this.entityOfInterest = null;
						}

						Vec3d dir = new Vec3d(
								entity.posX + this.world.rand.nextFloat() - 0.5f - this.entity.posX,
								entity.posY + this.world.rand.nextFloat() - 0.5f + entity.height * 0.5f - (this.entity.posY + this.entity.getEyeHeight()),
								entity.posZ + this.world.rand.nextFloat() - 0.5f - this.entity.posZ
								);

						double d = (double)MathHelper.sqrt(dir.x * dir.x + dir.z * dir.z);
						float yaw = (float)(MathHelper.atan2(dir.z, dir.x) * (180D / Math.PI)) - 90.0F;
						float pitch = (float)(-(MathHelper.atan2(dir.y, d) * (180D / Math.PI)));

						yaw = MathHelper.wrapDegrees(yaw - this.entity.rotationYaw);
						pitch = MathHelper.wrapDegrees(pitch - this.entity.rotationPitch);

						float adst = MathHelper.sqrt(yaw * yaw + pitch * pitch);

						if(adst < 90.0f) {
							this.entityOfInterest = entity;

							this.dataManager.set(DURATION, 5 + MathHelper.ceil(adst / 90.0f * 10));
							this.dataManager.set(YAW_OFFSET, yaw);
							this.dataManager.set(PITCH_OFFSET, pitch);
						}
					}
				}
			}
		} else {
			int duration = this.dataManager.get(DURATION);

			if(duration > 0 && this.lookTicks > 0 && this.lookTicks <= duration) {
				if(this.easeType == 0) {
					this.easeType = 1 + this.world.rand.nextInt(3);
				}

				float p = this.getEasedLookTicks();

				float yawOffset = p * this.dataManager.get(YAW_OFFSET);
				float pitchOffset = p * this.dataManager.get(PITCH_OFFSET);

				this.entity.rotationYaw += yawOffset - this.prevYawOffset;
				this.entity.rotationPitch = MathHelper.clamp(this.entity.rotationPitch + pitchOffset - this.prevPitchOffset, -90.0f, 90.0f);

				this.prevYawOffset = yawOffset;
				this.prevPitchOffset = pitchOffset;
			}
		}

		super.update();
	}

	@Nullable
	private EntityLivingBase findEntityOfInterest() {
		double range = 24.0D;
		final double rangeSq = range * range;

		double motionThreshold = 0.05D;

		List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.entity.getEntityBoundingBox().grow(range, 8, range));

		Collections.shuffle(entities);

		for(EntityLivingBase entity : entities) {
			if(entity != this.entity && !entity.isInvisible() && entity.getDistanceSq(this.entity) <= rangeSq
					&& Math.sqrt(entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ) >= motionThreshold
					&& this.entity.canEntityBeSeen(entity)
					&& entity.getPositionVector().subtract(this.entity.getPositionVector()).dotProduct(this.entity.getLookVec()) > 0) {
				return entity;
			}
		}

		return null;
	}

	private float getEasedLookTicks() {
		int duration = this.dataManager.get(DURATION);

		if(duration <= 0) {
			return 0.0f;
		}

		if(this.lookTicks > duration) {
			return 1.0f;
		}

		switch(this.easeType) {
		default:
		case 1:
			return easeInOutCubic(this.lookTicks / (float)duration);
		case 2:
			return easeInOutQuart(this.lookTicks / (float)duration);
		case 3:
			return easeOutExpo(this.lookTicks / (float)duration);
		}
	}

	private static float easeInOutCubic(float x) {
		return x < 0.5f ? 4.0f * x * x * x : 1.0f - (float)Math.pow(-2.0f * x + 2.0f, 3.0f) / 2.0f;
	}

	private static float easeInOutQuart(float x) {
		return x < 0.5f ? 8.0f * x * x * x * x : 1.0f - (float)Math.pow(-2.0f * x + 2.0f, 4.0f) / 2.0f;
	}

	private static float easeOutExpo(float x) {
		return x >= 1.0f ? 1.0f : 1.0f - (float)Math.pow(2.0f, -10.0f * x);
	}

	@Override
	public boolean isDone() {
		return this.ticks > 1200;
	}

	@Override
	public float getOverlayPercentage() {
		return 0.1f + MathHelper.sin(this.getEasedLookTicks() * (float)Math.PI) * 0.15f;
	}

}
