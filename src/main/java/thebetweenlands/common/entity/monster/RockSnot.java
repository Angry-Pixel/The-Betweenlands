package thebetweenlands.common.entity.monster;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntityWithSpawnRules;
import thebetweenlands.common.entity.BasicProximitySpawnerExtended;
import thebetweenlands.common.entity.creature.Lurker;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class RockSnot extends BasicProximitySpawnerExtended implements BLEntityWithSpawnRules <RockSnot> {
	private static final EntityDataAccessor<Integer> TENDRIL_COUNT = SynchedEntityData.defineId(RockSnot.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> JAW_ANGLE = SynchedEntityData.defineId(RockSnot.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> EATING_TIMER = SynchedEntityData.defineId(RockSnot.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> PEARL_TIMER = SynchedEntityData.defineId(RockSnot.class, EntityDataSerializers.INT);
	public boolean placed_by_player;
	public int spawnDelayCounter = 20;
	public static final int PEARL_CREATION_TIME = 120; // 6 seconds
	public int xpStored = 0;

	public RockSnot(EntityType<? extends BasicProximitySpawnerExtended> type, Level level) {
		super(type, level);
	}

	public Level getWorld() {
		return level();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CRUNCH.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CRUNCH.get();
	}

	@Override
	protected void registerGoals() {
		//targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers(RockSnot.class));
		targetSelector.addGoal(1, new RockSnot.ShootTendril(this));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(TENDRIL_COUNT, 0);
		builder.define(JAW_ANGLE, 0);
		builder.define(EATING_TIMER, 0);
		builder.define(PEARL_TIMER, -1);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 10.0D)
			.add(Attributes.MOVEMENT_SPEED, 0D)
			.add(Attributes.ATTACK_DAMAGE, 1D)
			.add(Attributes.FOLLOW_RANGE, 20.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1D);
	}

	@SuppressWarnings("deprecation")
	public static boolean canSpawnHere(EntityType<RockSnot> entity, LevelAccessor level, MobSpawnType spawn, BlockPos pos, RandomSource random) {
		return level.getDifficulty() != Difficulty.PEACEFUL && level.getBlockState(pos).liquid() && level.getFluidState(pos).is(FluidTags.WATER);
	}

	@Override
	public boolean checkSpawnObstruction(LevelReader level) {
		return level.isUnobstructed(this);
	}

	@Override
	public void aiStep() {
		if (!level().isClientSide()) {
			if (spawnDelayCounter > 0)
				spawnDelayCounter--;
		}
		super.aiStep();
	}

	@Override
	public void tick() {
		if (level().isClientSide()) {
			if (isInWater() && !level().getBlockState(blockPosition().below()).isRedstoneConductor(level(), blockPosition().below()))
				setDeltaMovement(getDeltaMovement().add(0D, -0.2D, 0D));

			if (isVehicle() && getJawAngle() == 16)
				if (level().getGameTime() % 20 == 0)
					spawnEatingParticles();
			if (getPearlTimer() == 1)
				spawnSpittingParticles();
			if (getPearlTimer() >= 30)
				if (level().getGameTime() % 10 == 0)
					spawnMakingParticles();
		}

		if (!level().isClientSide()) {
			if (getPearlTimer() < 0) {
				if (level().getGameTime() % 5 == 0)
					checkAreaHere();

				if (getTarget() != null) {
					if (!isVehicle() && getJawAngle() < 80) {
						setJawAngle(getJawAngle() + 8);
						if (getEatingHeight() > 0)
							setEatingHeight(0);
					}
					if (isVehicle() && getJawAngle() > 16)
						setJawAngle(getJawAngle() - 8);
				}

				if (getTarget() == null) {
					if (!isVehicle()) {
						if (getJawAngle() > 0 && getTendrilCount() <= 0)
							setJawAngle(getJawAngle() - 8);
					}
				}

				if (isVehicle() && getJawAngle() == 16 && getPlacedByPlayer()) {
					if (level().getGameTime() % 20 == 0) {
						setEatingHeight(getEatingHeight() + 1);
						level().playSound(null, blockPosition(), SoundRegistry.ROCK_SNOT_EAT.get(), SoundSource.HOSTILE, 1F, 1F);
						getPassengers().getFirst().hurt(damageSources().generic(), 0F);
						if (getEatingHeight() == 10 && getPassengers().getFirst() != null) {
							setContainedXP(getContainedXP() + Math.round(getXPEaten((LivingEntity) getPassengers().getFirst()) * 0.5F));
							getPassengers().getFirst().discard();
							if (getContainedXP() >= 10)
								setPearlTimer(PEARL_CREATION_TIME);
							setEatingHeight(0);
							setJawAngle(0);
						}
					}
				}

				if (isVehicle() && getJawAngle() == 16 && !getPlacedByPlayer())
					if (level().getGameTime() % 20 == 0) {
						level().playSound(null, blockPosition(), SoundRegistry.ROCK_SNOT_EAT.get(), SoundSource.HOSTILE, 1F, 1F);
						getPassengers().getFirst().hurt(damageSources().mobAttack(this), (float) getAttributeValue(Attributes.ATTACK_DAMAGE));
					}

			} else {
				if (getPearlTimer() >= 0) {
					setPearlTimer(getPearlTimer() - 1);
					if (level().getGameTime() % 20 == 0 && getPearlTimer() >= 30)
						level().playSound(null, blockPosition(), SoundRegistry.ROCK_SNOT_DIGEST.get(), SoundSource.HOSTILE, 1F, 0.8F + random.nextFloat() * 0.5F);
				}

				if (getPearlTimer() == 0) {
					int quotient = getContainedXP() / 10;
					int result = getContainedXP() - quotient * 10;
					setContainedXP(result);
					ItemStack pearl = new ItemStack(ItemRegistry.ROCK_SNOT_PEARL.get(), quotient);
					ItemEntity item = new ItemEntity(level(), blockPosition().getX() + 0.5D, blockPosition().getY() + 0.5D, blockPosition().getZ() + 0.5D, pearl);
					item.setDeltaMovement(getDeltaMovement().add(0D, 0.8D, 0D));
					level().addFreshEntity(item);
					level().playSound(null, blockPosition(), SoundRegistry.ROCK_SNOT_SPIT.get(), SoundSource.HOSTILE, 1F, 1F);
				}
			}
		}
		super.tick();
	}

	public int getXPEaten(LivingEntity entity) {
		return entity.getExperienceReward((ServerLevel) level(), null);
	}

	public int getContainedXP() {
		return xpStored;
	}

	public void setContainedXP(int amount) {
		xpStored = amount;
	}

	public void checkAreaHere() {
		if (!level().isClientSide() && level().getDifficulty() != Difficulty.PEACEFUL) {
			List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, proximityBox(this));
			list.removeIf(entity -> entity != null && !canTarget(entity));
			if (list.isEmpty()) {
				setTarget(null);
			} else {
				LivingEntity entity = list.getFirst();
				if (canSneakPast() && entity.isCrouching())
					return;
				else if (checkSight() && !hasLineOfSight(entity))
					return;
				else if (getCanShootTendril())
					setTarget(entity);
				if (isAlive() && isSingleUse())
					kill();
			}
		}
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		return this.hasLineOfSight(entity) && super.doHurtTarget(entity);
	}

	protected boolean damageSnot(DamageSource source, float amount) {
		return super.hurt(source, amount);
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		if (source.is(DamageTypes.DROWN))
			return false;
		if (source.is(DamageTypes.MOB_ATTACK)) {
			Entity sourceEntity = source.getEntity();
			if (!(sourceEntity instanceof Player))
				if (this.isVehicle() && this.getPassengers().getFirst() == sourceEntity)
					return false;
			if (source.isCreativePlayer()) {
				this.kill();
			}
		}
		return damageSnot(source, damage);
	}

	public boolean canTarget(LivingEntity entity) {
		if ((entity instanceof Player player && (this.getPlacedByPlayer() || player.isSpectator() || player.isCreative())))
			return false;
		if (entity instanceof RockSnot)
			return false;
		if (entity instanceof Lurker)
			return false;
		if (entity.getType().is(Tags.EntityTypes.BOSSES))
			return false;
		return isInWater();
	}

	@Override
	public boolean canAttackType(EntityType<?> type) {
		if (type == EntityType.PLAYER && getPlacedByPlayer())
			return false;
		if (type == EntityRegistry.ROCK_SNOT.get() || type == EntityRegistry.ROCK_SNOT_TENDRIL.get())
			return false;
		if (type == EntityRegistry.LURKER.get())
			return false;
		return super.canAttackType(type);
	}

	@Override
	public void setTarget(@Nullable LivingEntity entity) {
		if (getPlacedByPlayer()) {
			if (!(entity instanceof Player))
				super.setTarget(entity);
		} else
			super.setTarget(entity);
	}

	@Override
	public void positionRider(Entity entity, Entity.MoveFunction moveFunction) {
		entity.setPos(getX(), getY() + getBbHeight() * 0.5F - (entity.getBbHeight() / 10F * ((float) getEatingHeight())), getZ());
	}

	public void spawnMakingParticles() {
		for (int count = 0; count < 5; ++count)
			level().addParticle(ParticleTypes.CRIT, getX(), getY() + count * 0.5D, getZ(), 0.0D, 0.1D, 0.0D);
		//TODO - use the gem proc particles here
		//TheBetweenlands.createParticle(ParticleRegistry.GEM_PROC.get(),level(), getX() + (level().random.nextDouble() - 0.5D), getY() + 0.5D + level().random.nextDouble(), getZ() + (level().random.nextDouble() - 0.5D));
	}

	public void spawnEatingParticles() {
		for (int count = 0; count < 5; ++count)
			level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ItemRegistry.SNOT.get())), getX() + (level().random.nextDouble() - 0.5D), getY() + level().random.nextDouble(), getZ() + (level().random.nextDouble() - 0.5D), 0.0D, 0.0D, 0.0D);
	}

	public void spawnSpittingParticles() {
		for (int count = 0; count < 5; ++count)
			for (int more = 0; more < 5; ++more) {
				if (isInWater())
					level().addParticle(ParticleTypes.BUBBLE, getX(), getY() + count * 0.5D, getZ(), 0.0D, 0.1D, 0.0D);
				else
					TheBetweenlands.createParticle(ParticleRegistry.RAIN.get(), level(), getX(), getY() + count * 0.5D, getZ(), ParticleFactory.ParticleArgs.get().withColor(FastColor.ARGB32.colorFromFloat(1F, 0.4118F, 0.2745F, 0.1568F)));
			}
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	public boolean dismountsUnderwater() {
		return false;
	}

	private int getEatingHeight() {
		return getEntityData().get(EATING_TIMER);
	}

	public void setEatingHeight(int count) {
		getEntityData().set(EATING_TIMER, count);
	}

	public int getPearlTimer() {
		return getEntityData().get(PEARL_TIMER);
	}

	public void setPearlTimer(int count) {
		getEntityData().set(PEARL_TIMER, count);
	}

	public boolean getCanShootTendril() {
		return getTendrilCount() < 4 && getPearlTimer() <= 0;
	}

	public int getTendrilCount() {
		return getEntityData().get(TENDRIL_COUNT);
	}

	public void setTendrilCount(int count) {
		getEntityData().set(TENDRIL_COUNT, count);
	}

	public void setJawAngle(int angle) {
		getEntityData().set(JAW_ANGLE, angle);
	}

	public int getJawAngle() {
		return getEntityData().get(JAW_ANGLE);
	}

	public void setPlacedByPlayer(boolean placed) {
		placed_by_player = placed;
	}

	public boolean getPlacedByPlayer() {
		return placed_by_player;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setPlacedByPlayer(tag.getBoolean("placed_by_player"));
		setContainedXP(tag.getInt("xpStored"));
		setEatingHeight(tag.getInt("eating_height"));
		setJawAngle(tag.getInt("jaw_angle"));
		setPearlTimer(tag.getInt("pearl_timer"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("placed_by_player", getPlacedByPlayer());
		tag.putInt("xpStored", getContainedXP());
		tag.putInt("eating_height", getEatingHeight());
		tag.putInt("jaw_angle", getJawAngle());
		tag.putInt("pearl_timer", getPearlTimer());
	}

	@Override
	protected boolean isImmobile() {
		return false;
	}

	@Override
	public void push(double x, double y, double z) {
		this.jumping = false;
		this.xxa = 0.0F;
		this.yya = 0.0F;
		this.zza = 0.0F;
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return !(entity instanceof RockSnotTendril) && super.canCollideWith(entity);
	}

	@Override
	public float getProximityHorizontal() {
		return 4;
	}

	@Override
	public float getProximityVertical() {
		return 2;
	}

	@Override
	public AABB proximityBox(LivingEntity spawner) {
		return spawner.getBoundingBox().inflate(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal()).move(0D, getProximityVertical() + getBbHeight(), 0D);
	}

	@Override
	public Entity getEntitySpawned() {
		return null;
	}

	@Override
	public int getEntitySpawnCount() {
		return 0;
	}

	@Override
	public boolean isSingleUse() {
		return false;
	}

	@Override
	public int maxUseCount() {
		return 0;
	}

	@Override
	public <T extends LivingEntity> void performDetectionLogic(T detected) {
	}

	static class ShootTendril extends Goal {
		private final RockSnot parentEntity;
		@Nullable
		private LivingEntity target;

		public ShootTendril(RockSnot parent) {
			this.parentEntity = parent;
			this.setFlags(EnumSet.of(Flag.TARGET, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			target = parentEntity.getTarget();
			if (target == null || parentEntity.isVehicle() || !parentEntity.getCanShootTendril())
				return false;
			else return parentEntity.spawnDelayCounter == 0;
		}

		@Override
		public boolean canContinueToUse() {
			return target != null && parentEntity.hurtTime <= 40 && parentEntity.getCanShootTendril() && parentEntity.spawnDelayCounter == 0 && !parentEntity.isVehicle();
		}

		@Override
		public void tick() {
			if (!parentEntity.level().isClientSide() && target != null) {
				double targetX = target.getX() - parentEntity.getX();
				double targetY = target.getBoundingBox().minY + (double) (target.getBbHeight() / 2.0F) - (parentEntity.getY() + (double) (parentEntity.getBbHeight() / 2.0F));
				double targetZ = target.getZ() - parentEntity.getZ();
				RockSnotTendril grabber = new RockSnotTendril(EntityRegistry.ROCK_SNOT_TENDRIL.get(), parentEntity.level());
				grabber.setPos(parentEntity.getX(), parentEntity.getY() + parentEntity.getBbHeight() * 0.5D, parentEntity.getZ());
				grabber.setParentEntityID(parentEntity.getId());
				parentEntity.level().addFreshEntity(grabber);
				parentEntity.level().playSound(null, parentEntity.blockPosition(), SoundRegistry.ROCK_SNOT_ATTACK.get(), SoundSource.HOSTILE, 1F, 1F);
				grabber.moveToTarget(targetX, targetY, targetZ, 0.3F);
				if (!grabber.getExtending())
					grabber.setExtending(true);
				parentEntity.setTendrilCount(parentEntity.getTendrilCount() + 1);
				parentEntity.spawnDelayCounter = 10;
			}
		}

		@Override
		public void stop() {
			target = null;
		}
	}

	@Override
	public boolean canSpawnHere(EntityType<RockSnot> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
		return level.getDifficulty() != Difficulty.PEACEFUL && level.getBlockState(pos).is(BlockRegistry.SWAMP_WATER);
	}
}
