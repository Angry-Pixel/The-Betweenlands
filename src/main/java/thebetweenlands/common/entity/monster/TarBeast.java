package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.options.DripParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class TarBeast extends Monster implements BLEntity {

	private static final AttributeModifier KNOCKBACK_PRONE_MODIFIER = new AttributeModifier(TheBetweenlands.prefix("knockback_prone"), -0.25D, AttributeModifier.Operation.ADD_VALUE);

	private int shedCooldown = 70;
	private int sheddingProgress = 0;

	private int suckingCooldown = 400;
	private int suckingPreparation = 0;
	private int suckingProgress = 0;

	protected static final EntityDataAccessor<Byte> SUCKING_STATE = SynchedEntityData.defineId(TarBeast.class, EntityDataSerializers.BYTE);
	protected static final EntityDataAccessor<Boolean> SHEDDING = SynchedEntityData.defineId(TarBeast.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> GROW_TIMER = SynchedEntityData.defineId(TarBeast.class, EntityDataSerializers.INT);

	public int growCount, prevGrowCount;

	public TarBeast(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
		this.xpReward = 20;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 100.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.22D)
			.add(Attributes.ATTACK_DAMAGE, 6.0D)
			.add(Attributes.FOLLOW_RANGE, 25.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
			.add(Attributes.STEP_HEIGHT, 0.75F);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(2, new MoveToBlockGoal(this, 0.85D, 32) {

			@Override
			protected boolean isValidTarget(LevelReader level, BlockPos pos) {
				return level.getBlockState(pos).is(BlockRegistry.TAR);
			}
		});
		this.goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.85D));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.85D));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SUCKING_STATE, (byte) 0);
		builder.define(SHEDDING, false);
		builder.define(GROW_TIMER, 40);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 3;
	}

	@Override
	protected @Nullable SoundEvent getAmbientSound() {
		return SoundRegistry.TAR_BEAST_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.TAR_BEAST_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.TAR_BEAST_DEATH.get();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("shed_cooldown", this.shedCooldown);
		tag.putInt("shedding_progress", this.sheddingProgress);
		tag.putBoolean("shedding_state", this.isShedding());

		tag.putInt("sucking_cooldown", this.suckingCooldown);
		tag.putInt("sucking_preparation", this.suckingPreparation);
		tag.putInt("sucking_progress", this.suckingProgress);
		tag.putByte("sucking_state", this.getEntityData().get(SUCKING_STATE));
		tag.putInt("grow_timer", getGrowTimer());

		super.addAdditionalSaveData(tag);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		this.shedCooldown = tag.getInt("shedCooldown");
		this.sheddingProgress = tag.getInt("sheddingProgress");
		this.getEntityData().set(SHEDDING, tag.getBoolean("sheddingState"));
		this.suckingCooldown = tag.getInt("suckingCooldown");
		this.suckingPreparation = tag.getInt("suckingPreparation");
		this.suckingProgress = tag.getInt("suckingProgress");
		this.getEntityData().set(SUCKING_STATE, tag.getByte("suckingState"));
		this.setGrowTimer(tag.getInt("grow_timer"));

		super.readAdditionalSaveData(tag);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundRegistry.TAR_BEAST_STEP.get(), 1, 1);
	}

	@Override
	public float maxUpStep() {
		return this.isInFluidType(FluidTypeRegistry.TAR.get()) ? 2.0F : super.maxUpStep();
	}

	@Override
	public void aiStep() {
		super.aiStep();

		if (this.level().isClientSide()) {
			this.prevGrowCount = this.growCount;
			this.growCount = this.getGrowTimer();
			if (this.tickCount % 10 == 0) {
				this.renderParticles(this.level(), this.getRandom());
			}
			if (this.sheddingProgress > this.getSheddingSpeed()) {
				this.sheddingProgress = 0;

				for (int i = 0; i < 200; i++) {
					float rx = this.getRandom().nextFloat() * 4.0F - 2.0F;
					float ry = this.getRandom().nextFloat() * 4.0F - 2.0F;
					float rz = this.getRandom().nextFloat() * 4.0F - 2.0F;
					Vec3 vec = new Vec3(rx, ry, rz);
					vec = vec.normalize();
					TheBetweenlands.createParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL)), this.level(), this.getX() + rx + 0.25F, this.getY() + ry, this.getZ() + rz + 0.25F, ParticleFactory.ParticleArgs.get().withColor(0xFF000000).withMotion(vec.x * 0.5F, vec.y * 0.5F, vec.z * 0.5F));
				}
			} else if (this.isShedding() || this.sheddingProgress > 0) {
				this.sheddingProgress++;
			} else {
				this.sheddingProgress = 0;
			}

			if (this.isSucking()) {
				for (int i = 0; i < 5; i++) {
					float rx = this.getRandom().nextFloat() * 8.0F - 4.0F;
					float ry = this.getRandom().nextFloat() * 8.0F - 4.0F;
					float rz = this.getRandom().nextFloat() * 8.0F - 4.0F;
					Vec3 vec = new Vec3(rx, ry, rz);
					vec = vec.normalize();
					this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX() + rx + 0.25F, this.getY() + ry, this.getZ() + rz + 0.25F, -vec.x * 0.5F, -vec.y * 0.5F, -vec.z * 0.5F);
				}
			}
		} else {
			if (this.shedCooldown > 0 && this.getTarget() != null) {
				this.shedCooldown--;
			}

			if (!this.isSucking() && !this.isPreparing()) {
				if (this.shedCooldown == 0 && this.getTarget() != null && this.getTarget().distanceTo(this) < 6.0D && this.hasLineOfSight(this.getTarget())) {
					this.setShedding(true);
					this.shedCooldown = this.getSheddingCooldown() + this.getRandom().nextInt(this.getSheddingCooldown() / 2);
				}

				if (this.sheddingProgress > this.getSheddingSpeed()) {
					this.playSound(SoundRegistry.TAR_BEAST_LIVING.get(), 1F, (this.getRandom().nextFloat() * 0.2F + 1.0F) * 0.6F);
					for (int i = 0; i < 8; i++) {
						this.playSound(SoundRegistry.TAR_BEAST_STEP.get(), 1F, (this.getRandom().nextFloat() * 0.4F + 0.8F) * 0.8F);
					}
					this.sheddingProgress = 0;
					this.setShedding(false);
					if (this.getTarget() != null) {
						List<LivingEntity> affectedEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6.0F));
						for (LivingEntity e : affectedEntities) {
							if (e == this || e.distanceTo(this) > 6.0F || !e.hasLineOfSight(this) || e instanceof TarBeast) continue;
							if (e instanceof Player player) {
								if (player.isBlocking()) continue;
							}
							double dst = e.distanceTo(this);
							float dmg = (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) / dst * 7.0F);
							e.hurt(this.damageSources().mobAttack(this), dmg);
							e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (20 + (1.0F - dst / 6.0F) * 150), 1));
						}
					}
				}

				if (this.isShedding()) {
					this.sheddingProgress++;
				} else {
					this.sheddingProgress = 0;
				}
			}

			if (this.suckingCooldown > 0 && this.getTarget() != null) {
				this.suckingCooldown--;
			}

			if (!this.isShedding()) {
				if (this.suckingCooldown == 0 && this.getTarget() != null && this.getTarget().distanceTo(this) <= 10.0D && this.hasLineOfSight(this.getTarget())) {
					this.setPreparing();
				}

				if (this.isPreparing()) {
					this.suckingPreparation++;

					if (this.suckingPreparation > 40) {
						this.suckingPreparation = 0;

						this.setSucking(true);
						this.suckingCooldown = this.getSuckingCooldown() + this.getRandom().nextInt(this.getSuckingCooldown() / 2);
						this.playSound(SoundRegistry.TAR_BEAST_SUCK.get(), 1F, 1F);
					}
				}

				if (this.suckingProgress > 130) {
					this.setSucking(false);
					this.suckingProgress = 0;
				}

				if (this.isSucking()) {
					this.suckingProgress++;

					List<Entity> affectedEntities = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(10.0F));
					for (Entity e : affectedEntities) {
						if (e == this || e.distanceTo(this) > 10.0F || !this.hasLineOfSight(e) || e instanceof TarBeast) continue;
						Vec3 vec = new Vec3(this.getX() - e.getX(), this.getY() - e.getY(), this.getZ() - e.getZ());
						vec = vec.normalize();
						float dst = e.distanceTo(this);
						float mod = (float) Math.pow(1.0F - dst / 13.0F, 1.2D);
						if (e instanceof Player player) {
							if (player.isBlocking()) mod *= 0.18F;
						}
						if (dst < 1.0F && e instanceof LivingEntity living) {
							living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 3));
							living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3));
							living.setDeltaMovement(living.getDeltaMovement().multiply(0.008D, 0.008D, 0.008D));
							if (this.tickCount % 12 == 0) {
								e.hurt(this.damageSources().drown(), 1);
							}
						}
						e.setDeltaMovement(e.getDeltaMovement().add(vec.x * 0.18F * mod, vec.y * 0.18F * mod, vec.z * 0.18F * mod));
						e.hurtMarked = true;
					}
					this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addOrReplacePermanentModifier(KNOCKBACK_PRONE_MODIFIER);
				} else {
					this.suckingProgress = 0;
					this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).removeModifier(KNOCKBACK_PRONE_MODIFIER);
				}
			}

			if (this.getGrowTimer() < 40) {
				this.setGrowTimer(Math.min(40, this.getGrowTimer() + 1));
			}
		}
	}

	@Override
	protected boolean isImmobile() {
		return super.isImmobile() || this.isSucking() || this.getGrowTimer() < 40;
	}

	public void renderParticles(Level level, RandomSource rand) {
		for (int count = 0; count < 3; ++count) {
			int motionX = rand.nextInt(2) * 2 - 1;
			int motionZ = rand.nextInt(2) * 2 - 1;
			double a = Math.toRadians(this.yBodyRot);
			double offSetX = -Math.sin(a) * 0.5D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			double offSetZ = Math.cos(a) * 0.5D + rand.nextDouble() * 0.3D - rand.nextDouble() * 0.3D;
			double velY = (rand.nextFloat() - 0.5D) * 0.125D;
			double velZ = rand.nextFloat() * 0.5F * motionZ;
			double velX = rand.nextFloat() * 0.5F * motionX;
			TheBetweenlands.createParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL)), level, this.getX(), this.getY() + rand.nextDouble() * 1.9D, this.getZ(), ParticleFactory.ParticleArgs.get().withColor(0xFF000000).withMotion(velX * 0.15D, velY * 0.1D, velZ * 0.15D));
			TheBetweenlands.createParticle(ParticleRegistry.FALLING_TAR.get(), level, this.getX() + offSetX, this.getY() + 1.2D, this.getZ() + offSetZ, ParticleFactory.ParticleArgs.get().withColor(0xFF000000));
		}
	}

	public float getGrowthFactor(float partialTicks) {
		return Mth.lerp(partialTicks, this.prevGrowCount, this.growCount);
	}

	@Override
	protected void doPush(Entity entity) {
		if (!this.isSucking()) {
			super.doPush(entity);
		}
	}

	@Override
	public boolean isPushable() {
		return super.isPushable() && !this.isSucking();
	}

	public boolean isShedding() {
		return this.getEntityData().get(SHEDDING);
	}

	public void setShedding(boolean shedding) {
		this.getEntityData().set(SHEDDING, shedding);
	}

	public int getSheddingProgress() {
		return this.sheddingProgress;
	}

	public int getSheddingCooldown() {
		return 70;
	}

	public int getSheddingSpeed() {
		return 10;
	}

	public int getSuckingCooldown() {
		return 400;
	}

	public boolean isSucking() {
		return this.getEntityData().get(SUCKING_STATE) == 1;
	}

	public boolean isPreparing() {
		return this.getEntityData().get(SUCKING_STATE) == 2;
	}

	public void setSucking(boolean sucking) {
		this.getEntityData().set(SUCKING_STATE, (byte) (sucking ? 1 : 0));
	}

	public void setPreparing() {
		this.getEntityData().set(SUCKING_STATE, (byte) 2);
	}

	public int getGrowTimer() {
		return this.getEntityData().get(GROW_TIMER);
	}

	public void setGrowTimer(int timer) {
		this.getEntityData().set(GROW_TIMER, timer);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader level) {
		return 0.5F;
	}
}
