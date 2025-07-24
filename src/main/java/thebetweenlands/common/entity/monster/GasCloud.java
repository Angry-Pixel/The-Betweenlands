package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class GasCloud extends Monster implements BLEntity {

	private static final EntityDataAccessor<Float> GAS_CLOUD_COLOR_R = SynchedEntityData.defineId(GasCloud.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> GAS_CLOUD_COLOR_G = SynchedEntityData.defineId(GasCloud.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> GAS_CLOUD_COLOR_B = SynchedEntityData.defineId(GasCloud.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> GAS_CLOUD_COLOR_A = SynchedEntityData.defineId(GasCloud.class, EntityDataSerializers.FLOAT);

	protected double aboveLayer = 6.0D;
	protected int targetBlockedTicks = 0;

	public static final DamageSource damageSourceSuffocation = (new DamageSource(Holder.direct(new DamageType("bl.suffocation", DamageScaling.NEVER, 2.0F, DamageEffects.HURT, DeathMessageType.DEFAULT))));

	public GasCloud(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
		this.noPhysics = false;
		this.noCulling = true;
		this.moveControl = new FlightMoveHelper(this);
		//this.setPathfindingMalus(PathType.WATER, -1.0F);
		//this.setPathfindingMalus(PathType.BLOCKED, -1.0F);
		//this.setPathfindingMalus(PathType.OPEN, 0.0F);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		float isLiquid = this.level().getBlockState(pos).liquid() ? -8F : 0F;
		float isAir = this.level().getBlockState(pos).isAir() ? 8F : -8F;
		return isLiquid + isAir;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.065D)
			.add(Attributes.MAX_HEALTH, 16.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 16.0D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(GAS_CLOUD_COLOR_R, (float)104 / 255);
		builder.define(GAS_CLOUD_COLOR_G, (float)196 / 255);
		builder.define(GAS_CLOUD_COLOR_B, (float)179 / 255);
		builder.define(GAS_CLOUD_COLOR_A, (float)170 / 255);
	}

	/**
	 * Returns the gas color in an array [red, green, blue, alpha]
	 * Porting: Revised to use raw float values to avoid unnecessary conversions
	 * @return
	 */
	public float[] getGasColor() {
		return new float[] { this.getEntityData().get(this.GAS_CLOUD_COLOR_R).floatValue(),
			this.getEntityData().get(this.GAS_CLOUD_COLOR_G).floatValue(),
			this.getEntityData().get(this.GAS_CLOUD_COLOR_B).floatValue(),
			this.getEntityData().get(this.GAS_CLOUD_COLOR_A).floatValue() };
	};

	/**
	 * Porting: BatchedParticleRenderer currently not ported.
	 * TODO: evaluate performance of standard particle rendering and whether BatchedParticleRenderer should be added
	 * @param strongMotion
	 */
	@OnlyIn(Dist.CLIENT)
	private void spawnCloudParticle(boolean strongMotion) {
		if(strongMotion) {
			double x = this.xo + this.xxa + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double y = this.yo + this.getEyeHeight() / 2.0D + this.yya + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double z = this.zo + this.zza + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			float[] color = this.getGasColor();

			TheBetweenlands.createParticle(ParticleRegistry.GAS_CLOUD.get(), this.level(), x, y, z,
				ParticleFactory.ParticleArgs.get()
					.withMotion((this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F)
					.withColor(color[0], color[1], color[2], color[3]));

			//TheBetweenlands.createParticle(ParticleRegistry.GAS_CLOUD_HAZE.get(), this.level(), x, y, z,
			//	ParticleFactory.ParticleArgs.get()
			//		.withMotion((this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F)
			//		.withColor(color[0], color[1], color[2], color[3]));

			//BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
			//BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, particle);
		} else {
			double x = this.xo + this.xxa + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double y = this.yo + this.getEyeHeight() / 2.0D + this.yya + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double z = this.zo + this.zza + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double mx = this.xxa + (this.level().random.nextFloat() - 0.5F) / 16.0F;
			double my = this.yya + (this.level().random.nextFloat() - 0.5F) / 16.0F;
			double mz = this.zza + (this.level().random.nextFloat() - 0.5F) / 16.0F;
			float[] color = this.getGasColor();

			TheBetweenlands.createParticle(ParticleRegistry.GAS_CLOUD.get(), this.level(), x, y, z,
				ParticleFactory.ParticleArgs.get()
					.withMotion(mx, my, mz)
					.withColor(color[0], color[1], color[2], color[3]));

			//TheBetweenlands.createParticle(ParticleRegistry.GAS_CLOUD_HAZE.get(), this.level(), x, y, z,
			//	ParticleFactory.ParticleArgs.get()
			//		.withMotion(mx, my, mz)
			//		.withColor(color[0], color[1], color[2], color[3]));

			//BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
			//BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, particle);
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			this.spawnCloudParticle(false);
			//this.updateParticleBatch();
		}

		if (this.isInWater()) {
			this.moveControl.setWantedPosition(this.xo, this.yo + 1.0D, this.zo, 1.0D);
		} else {
			if(this.getTarget() != null) {
				this.moveControl.setWantedPosition(this.getTarget().xo, this.getTarget().yo + this.getTarget().getEyeHeight(), this.getTarget().zo, 1.0D);
			}
		}

		if (!this.level().isClientSide() && this.isAlive()) {
			List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D));
			for (LivingEntity target : targets) {
				if (!(target instanceof GasCloud) && !(target instanceof BLEntity)) {
					target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
					if (target.tickCount % 10 == 0);
					//target.hurt(DamageSource, (float) 2.0D);
				}
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GAS_CLOUD_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.GAS_CLOUD_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GAS_CLOUD_DEATH.get();
	}

	@Override
	protected void tickDeath() {
		++this.deathTime;

		if(this.level().isClientSide()) {
			for(int i = 0; i < 6; i++) {
				this.spawnCloudParticle(true);
			}
		}

		if (this.isRemoved()){
			return;
		}

		if (this.deathTime >= 80) {
			this.level().broadcastEntityEvent(this, (byte)60);
			this.remove(RemovalReason.KILLED);
		}
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 1;
	}
}