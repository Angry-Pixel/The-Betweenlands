package thebetweenlands.common.entity.monster;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.options.EntitySwirlParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceProjectile;
import thebetweenlands.common.entity.movement.BLFlightMoveControl;
import thebetweenlands.common.entity.projectile.BoneShamanProjectile;
import thebetweenlands.common.registries.EntityDataSerializerRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.ParticleRegistry;

public class BoneShaman extends FlyingMonster {

	public static final EntityDataAccessor<Boolean> IS_SUMMONING_PUPPETS = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> SPAWN_TIMER = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<List<BlockPos>> PUPPET_SUMMON_TARGETS = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializerRegistry.BLOCK_POS_LIST.get());
	public static final EntityDataAccessor<Boolean> RELOADING = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> RELOAD_TIMER = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> CASTING_TIMER = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> ATTACK_TIMER = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> IS_CASTING = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> SPIKE_PROJECTILE = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> SUMMON_TIMER = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> PUPPET_COUNT = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.INT);

	public int prevReloadTimer;
	public int prevAttackTimer;
	public int prevCastingTimer;
	public int lastSpawningAnimationTicks = 0;
	public int prevSummonTimer = 0;
	public int spawnDuration = 30;

	public BoneShaman(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_SUMMONING_PUPPETS, false);
		builder.define(SPAWN_TIMER, 0);
		builder.define(PUPPET_SUMMON_TARGETS, List.of());
		builder.define(RELOADING, false);
		builder.define(RELOAD_TIMER, 0);
		builder.define(ATTACK_TIMER, 0);
		builder.define(IS_ATTACKING, false);
		builder.define(CASTING_TIMER, 0);
		builder.define(IS_CASTING, false);
		builder.define(SPIKE_PROJECTILE, false);
		builder.define(SUMMON_TIMER, 0);
		builder.define(PUPPET_COUNT, 0);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(1, new BoneShamanHoverAttackGoal(this, 1D, 10F, 6F));
		goalSelector.addGoal(2, new BoneShamanSummonPuppetsGoal(this));
		// goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.7D));
		targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 2.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.33D)
				.add(Attributes.FLYING_SPEED, 0.32D)
				.add(Attributes.ATTACK_DAMAGE, 0.5D)
				.add(Attributes.ATTACK_KNOCKBACK, 2.0D)
				.add(Attributes.FOLLOW_RANGE, 64.0D);
	}

	@Override
	public void aiStep() {
		if (isAlive()) {
			lastSpawningAnimationTicks = getSpawnTimer();
			if (!level().isClientSide()) {
				if (getSpawnTimer() < spawnDuration)
					setSpawnTimer(getSpawnTimer() + 1);
				if(!isEmerging() && !isSummoningPuppets() && canSummonNewPuppets()) //just a temp catch for stop it firing off until timer is made
					stepSummoning();
			}

			if (level().isClientSide()) {
				if (getSpawnTimer() <= 10)
					spawnEmergingParticles();
				
				if (isAlive() && isEmerging() && getSpawnTimer() == 20)
					spawnEmergingEndParticles();
				
				if (!isShootingSpikes() && getCastingTimer() > 0)
					spawnCastingParticles();

				if (isSummoningPuppets() && getSummonTimer() == 10) {
					BlockPos targetPos = getPuppetSummonTargets().get(0);
					shootParticles(level(), new Vec3(targetPos.getX() + 0.5D - getX(), targetPos.getY() + 0.5D - getY() - getBbHeight() / 1.25D, targetPos.getZ() + 0.5D - getZ()));
				}

				if (isAlive() && !isEmerging()) {
					if (getRandom().nextInt(3) == 0) {
						ParticleFactory.ParticleArgs<?> args = ParticleFactory.ParticleArgs.get().withData(400, getRandom().nextFloat(), this);
						args.withScale((1.5F + getRandom().nextFloat() * 1.5F) * 0.5F);
						args.withColor(1F, 1F, 1F, 0.5F);
						TheBetweenlands.createParticle(new EntitySwirlParticleOptions(ParticleRegistry.WIGHT_FACE_SWIRL.get(), new Vec3(0, -2D, 0), new Vec3(0, 0, 0), Vec3.ZERO, Vec3.ZERO, 4.0D, true), level(), getX(), getY(), getZ(), args);
						TheBetweenlands.createParticle(new EntitySwirlParticleOptions(ParticleRegistry.FLY_SWIRL.get(), new Vec3(0, -2D, 0), new Vec3(0, 0, 0), Vec3.ZERO, Vec3.ZERO, 2.0D, true), level(), getX(), getY(), getZ(), args);
					}
				}
			}
		}

		super.aiStep();

		if (level().isClientSide()) {
			prevReloadTimer = getReloadTimer();
			prevAttackTimer = getAttackTimer();
			prevCastingTimer = getCastingTimer();
			prevSummonTimer = getSummonTimer();
			if (getAttackTimer() == 0)
				prevAttackTimer = 0;
			if (getReloadTimer() == 0)
				prevReloadTimer = 0;
			if (getCastingTimer() == 0)
				prevCastingTimer = 0;
			if (getSummonTimer() == 0)
				prevSummonTimer = 0;
			if (isDeadOrDying())
				setDeltaMovement(Vec3.ZERO);
		}

		if (!level().isClientSide()) {
			if (isDeadOrDying()) {
				//just stop doing everything when dead :lonk:
				getNavigation().stop();
				setReloadTimer(0);
				setReloading(false);
				setAttackTimer(0);
				setAttacking(false);
				setCastingTimer(0);
				setCasting(false);
				setSummoningPuppets(false);
				setSummonTimer(0);
			}

			if (isAlive()) {
				if(!isSummoningPuppets()) {
					if (isAttacking()) {
						setAttackTimer(getAttackTimer() + 1);
						if (getAttackTimer() >= 20) {
							setShootingSpikes(level().getRandom().nextBoolean());
							setAttackTimer(0);
							setAttacking(false);
						}
					} else
						setAttackTimer(0);

					if (isReloading()) {
						if (!isCasting()) {
							setReloadTimer(getReloadTimer() + 1);
							if (getReloadTimer() == 20)
								setCasting(true);
						}

						if(isCasting())
							setCastingTimer(getCastingTimer() + 1);

						if (getCastingTimer() >= 40) {
							setCastingTimer(0);
							setCasting(false);
						}

						if (getReloadTimer() >= 40) {
							setReloadTimer(0);
							setReloading(false);
						}
					} else
						setReloadTimer(0);
				}
				else {
					setReloadTimer(0);
					setReloading(false);
					setAttackTimer(0);
					setAttacking(false);
					setCastingTimer(0);
					setCasting(false);
				}
			}
		}
	}

	public void stepSummoning() {
			List<BlockPos> puppetSummonLocations = findNearbySummonLocations();
			if(!puppetSummonLocations.isEmpty()) {
				setSummoningPuppets(true);
				setPuppetSummonTargets(puppetSummonLocations);
			}
	}

	public List<BlockPos> findNearbySummonLocations() {
		List<BlockPos> puppetSummonLocations = new ArrayList<>();
		AABB searchBox = new AABB(blockPosition()).inflate(8D, 8D, 8D);
		BlockPos minPos = BlockPos.containing(searchBox.minX, searchBox.minY, searchBox.minZ);
		BlockPos maxPos = BlockPos.containing(searchBox.maxX, searchBox.maxY, searchBox.maxZ);

		Level level = level();
		for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos))
			if (level.getBlockState(pos).is(BLBlockTagProvider.BONE_PUPPET_CONVERTABLE) && level.isEmptyBlock(pos.above()))
				puppetSummonLocations.add(pos.immutable());

		return puppetSummonLocations;
	}

	private void spawnPuppets() {
		List<BlockPos> list = getPuppetSummonTargets();

		if (!list.isEmpty()) {
			// TODO just pick the first one for now
			int spawn = 0;
			//for (int spawn = 0; spawn < list.size(); spawn++) {
				BonePuppetRanged puppet1 = new BonePuppetRanged(level(), this);
				BonePuppetMelee puppet2 = new BonePuppetMelee(level(), this);
				level().destroyBlock(list.get(spawn), false);
				if (level().getRandom().nextBoolean()) {
					if (puppet1 != null) {
						puppet1.setPos(list.get(spawn).getBottomCenter());
						puppet1.setYRot(getYRot());
						level().addFreshEntity(puppet1);
					}
				} else {
					if (puppet2 != null) {
						puppet2.setPos(list.get(spawn).getBottomCenter());
						puppet2.setYRot(getYRot());
						level().addFreshEntity(puppet2);
					}
					
				}
				setPuppetCount(getPuppetCount() + 1);
			//}
		}
	}

	public void spawnEmergingParticles() {
		double px = getX();
		double py = getY();
		double pz = getZ();
		for (int i = 0, amount = 5 + level().getRandom().nextInt(2); i < amount; i++) {
			double ox = level().getRandom().nextDouble() * 0.1F - 0.05F;
			double oz = level().getRandom().nextDouble() * 0.1F - 0.05F;
			double motionX = level().getRandom().nextDouble() * 0.2F - 0.1F;
			double motionY = level().getRandom().nextDouble() * 0.1F + 0.075F;
			double motionZ = level().getRandom().nextDouble() * 0.2F - 0.1F;
			level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, getBlockStateOn()), false, px + ox, py, pz + oz, motionX, motionY, motionZ);
		}
	}

	public void spawnEmergingEndParticles() {
		for (int a = 0; a < 360; a += 4) {
			double ang = a * Math.PI / 180D;
			TheBetweenlands.createParticle(ParticleRegistry.WIGHT_FACE.get(), level(), getX() -Math.sin((float) ang) * 0.75D, getY() + getBbHeight() * 0.6D , getZ() + Math.cos((float) ang) * 0.75D, ParticleFactory.ParticleArgs.get().withMotion(-Math.sin((float) ang) * 0.2D, 0D, Math.cos((float) ang) * 0.2D).withScale(1F).withData(200)); 
		}
	}

	private void spawnCastingParticles() {
		double px = getX();
		double py = getY() + 3.5D;
		double pz = getZ();
		double ox = level().getRandom().nextDouble() * 0.5F - 0.25F;
		double oz = level().getRandom().nextDouble() * 0.5F - 0.25F;
		double motionX = level().getRandom().nextDouble() * 0.4F - 0.2F;
		double motionY = level().getRandom().nextDouble() * 0.2F + 0.15F;
		double motionZ = level().getRandom().nextDouble() * 0.4F - 0.2F;
		TheBetweenlands.createParticle(ParticleRegistry.DRUID_CASTING.get(), level(), px + ox, py, pz + oz, ParticleFactory.ParticleArgs.get().withMotion(motionX, motionY, motionZ).withScale(getRandom().nextFloat() * 0.5F + 0.5F).withColor(1F, 1F, 1F, 1F));
	}

	private void shootParticles(Level level, Vec3 target) {
		for(int i = 0; i < 20; i++) {
			float offsetLen = level.getRandom().nextFloat();
			Vec3 offset = new Vec3(target.x * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f, target.y * offsetLen, target.z * offsetLen + level.getRandom().nextFloat() * 0.2f - 0.1f);
			float vx = (level.getRandom().nextFloat() * 0.5f - 0.25f) * 0.00125f;
			float vz = (level.getRandom().nextFloat() * 0.5f - 0.25f) * 0.00125f;
			float scale = 0.25f + level.getRandom().nextFloat();
			//TODO may need a tweak to line up nice with the staff
			double angle = Math.toRadians(getYRot() + 20F);
			double offSetX = -Math.sin(angle) * 1.25D;
			double offSetZ = Math.cos(angle) * 1.25D;
			//TODO add better particles - will use smoke for now
			TheBetweenlands.createParticle(ParticleRegistry.WIGHT_FACE.get(), level, getX() + offset.x + offSetX, getY() + getBbHeight() / 1.25D + offset.y, getZ() + offset.z + offSetZ, ParticleFactory.ParticleArgs.get().withMotion(vx, 0D, vz).withScale(scale).withData(10)); 
			//level.addParticle(ParticleTypes.CLOUD, false, getX() + offset.x + offSetX, getY() + getBbHeight() / 1.25D + offset.y, getZ() + offset.z + offSetZ, vx, vy, vz);
		}
	}

	@Override
	protected boolean isImmobile() {
		return isAlive() && (super.isImmobile() || getSpawnTimer() < spawnDuration);
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		boolean isCreative = source.getEntity() instanceof Player player && player.isCreative();
		return (isEmerging() && !isCreative) || super.isInvulnerableTo(source);
	}

	@Override
	public boolean isPushable() {
		return !isEmerging() && super.isPushable();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (isEmerging() && source.is(DamageTypes.IN_WALL))
			return false;
		return super.hurt(source, amount);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("spawn_timer", getSpawnTimer());
		compound.putInt("puppet_count", getPuppetCount());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		setSpawnTimer(compound.getInt("spawn_timer"));
		setPuppetCount(compound.getInt("puppet_count"));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	public boolean isEmerging() {
		return getEntityData().get(SPAWN_TIMER) < spawnDuration;
	}

	public boolean isSummoningPuppets() {
		return getEntityData().get(IS_SUMMONING_PUPPETS);
	}

	public void setSummoningPuppets(boolean summon) {
		getEntityData().set(IS_SUMMONING_PUPPETS, summon);
	}

	public int getSpawnTimer() {
		return getEntityData().get(SPAWN_TIMER);
	}

	public void setSpawnTimer(int timer) {
		getEntityData().set(SPAWN_TIMER, timer);
	}

	public List<BlockPos> getPuppetSummonTargets() {
		return getEntityData().get(PUPPET_SUMMON_TARGETS);
	}

	public void setPuppetSummonTargets(List<BlockPos> summonTargets) {
		getEntityData().set(PUPPET_SUMMON_TARGETS, List.copyOf(summonTargets));
	}

	public void setReloading(boolean attacking) {
		getEntityData().set(RELOADING, attacking);
	}

	public boolean isReloading() {
		return getEntityData().get(RELOADING);
	}

	public void setReloadTimer(int progress) {
		getEntityData().set(RELOAD_TIMER, progress);
	}

	public int getReloadTimer() {
		return getEntityData().get(RELOAD_TIMER);
	}

	public void setAttackTimer(int progress) {
		getEntityData().set(ATTACK_TIMER, progress);
	}

	public int getAttackTimer() {
		return getEntityData().get(ATTACK_TIMER);
	}

	public void setAttacking(boolean attacking) {
		getEntityData().set(IS_ATTACKING, attacking);
	}

	public boolean isAttacking() {
		return getEntityData().get(IS_ATTACKING);
	}

	public void setCastingTimer(int progress) {
		getEntityData().set(CASTING_TIMER, progress);
	}

	public int getCastingTimer() {
		return getEntityData().get(CASTING_TIMER);
	}

	public void setCasting(boolean attacking) {
		getEntityData().set(IS_CASTING, attacking);
	}

	public boolean isCasting() {
		return getEntityData().get(IS_CASTING);
	}

	public void setShootingSpikes(boolean attacking) {
		getEntityData().set(SPIKE_PROJECTILE, attacking);
	}

	public boolean isShootingSpikes() {
		return getEntityData().get(SPIKE_PROJECTILE);
	}

	public void setSummonTimer(int count) {
		getEntityData().set(SUMMON_TIMER, count);
	}

	public int getSummonTimer() {
		return getEntityData().get(SUMMON_TIMER);
	}

	public void setPuppetCount(int puppets) {
		getEntityData().set(PUPPET_COUNT, puppets);
	}

	public int getPuppetCount() {
		return getEntityData().get(PUPPET_COUNT);
	}

	public boolean canSummonNewPuppets() {
		return getEntityData().get(PUPPET_COUNT) < 2; // two for now
	}

	public float getSpawningAnimation(float partialTicks) {
		return Mth.lerp(partialTicks, lastSpawningAnimationTicks, getSpawnTimer()) / (float) spawnDuration;
	}

	// May need this for things and stuffs
	public boolean isWearingSkullMask(LivingEntity entity) {
		ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
		return !helmet.isEmpty() && helmet.is(ItemRegistry.SKULL_MASK);
	}

	public static class BoneShamanHoverAttackGoal extends Goal {
		public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
		private final BoneShaman shaman;
		private final double speedModifier;
		private final float attackRadiusSqr;
		private final float repositionRadiusSqr;
		private int updatePathDelay;

		public BoneShamanHoverAttackGoal(BoneShaman shaman, double speedModifier, float range, float near) {
			this.shaman = shaman;
			this.speedModifier = speedModifier;
			attackRadiusSqr = range * range;
			repositionRadiusSqr = near * near;
			setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return isValidTarget() && !shaman.isSummoningPuppets();
		}

		@Override
		public boolean canContinueToUse() {
			return (isValidTarget() || !shaman.getNavigation().isDone()) && !shaman.isSummoningPuppets();
		}

		private boolean isValidTarget() {
			return shaman.getTarget() != null && shaman.getTarget().isAlive();
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			int groundHeight = BLFlightMoveControl.getGroundHeight(shaman.level(), shaman.blockPosition(), 16, shaman.blockPosition()).getY();
			if (shaman.blockPosition().getY() - groundHeight < 4) {
				shaman.setDeltaMovement(shaman.getDeltaMovement().add(0D, 0.01D, 0D));
				shaman.hurtMarked = true;
			} else {
				shaman.setDeltaMovement(shaman.getDeltaMovement().subtract(0D, 0.01D, 0D));
				shaman.hurtMarked = true;
			}
			LivingEntity livingentity = shaman.getTarget();
			if (livingentity != null) {
				boolean canSee = shaman.getSensing().hasLineOfSight(livingentity);
				if (canSee) {
					double distanceToTarget = shaman.distanceToSqr(livingentity);
					boolean outOfRange = distanceToTarget > (double) attackRadiusSqr;
					boolean tooNear = distanceToTarget < (double) repositionRadiusSqr;
					if (outOfRange) {
						updatePathDelay--;
						if (updatePathDelay <= 0) {
							shaman.getNavigation().moveTo(livingentity.getX(), Math.max(groundHeight + 4, livingentity.getY() + 4), livingentity.getZ(), speedModifier);
							updatePathDelay = PATHFINDING_DELAY_RANGE.sample(shaman.getRandom());
						}
					} else if (tooNear) {
						updatePathDelay--;
						if (updatePathDelay <= 0) {
							Vec3 view = shaman.getViewVector(0.0F);
							Vec3 newTarget = AirAndWaterRandomPos.getPos(shaman, 8, 0, 0, view.x, view.z, 0F);
							if (newTarget != null) {
								shaman.getNavigation().moveTo(newTarget.x, Math.max(groundHeight + 4, livingentity.getY() + 4), newTarget.z, speedModifier);
								updatePathDelay = PATHFINDING_DELAY_RANGE.sample(shaman.getRandom());
							}
						}
					} else {
						updatePathDelay = 0;
						shaman.getNavigation().stop();
					}

					shaman.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
					shootStuff(livingentity);
				}
			}
		}

		private void shootStuff(LivingEntity target) {
			if (canPerformAttack(target)) {
				if (!shaman.level().isClientSide()) {
					shaman.setAttacking(true);
					Level level = shaman.level();
					double direction = Math.toRadians(shaman.getYRot());
					Vec3 diff = (new Vec3(shaman.position().x, shaman.position().y + shaman.getBbHeight(), shaman.position().z)).subtract(new Vec3(target.getBoundingBox().minX + (target.getBoundingBox().maxX - target.getBoundingBox().minX) / 2.0D, target.getBoundingBox().minY + (target.getBoundingBox().maxY - target.getBoundingBox().minY) / 2.0D, target.getBoundingBox().minZ + (target.getBoundingBox().maxZ - target.getBoundingBox().minZ) / 2.0D)).normalize();

					if (!shaman.isShootingSpikes()) {
						 if(shaman.getAttackTimer() == 10) {
							Projectile projectile = new PrimordialMalevolenceProjectile(level, shaman);
							((PrimordialMalevolenceProjectile) projectile).setDeflectable(true);
							projectile.absMoveTo(shaman.getX() - Math.sin(direction) * 0.5D, shaman.getY() + shaman.getBbHeight(), shaman.getZ() + Math.cos(direction) * 0.5D, shaman.getYRot(), 0F);
							projectile.shoot(-diff.x, -diff.y, -diff.z, 0.5F, 0F);
							level.addFreshEntity(projectile);
						 }
					} else {
						if (shaman.getAttackTimer()%2 == 0) {
							if (shaman.getAttackTimer() >= 8 ) {
								diff = (new Vec3(shaman.position().x, shaman.position().y + shaman.getBbHeight() / 2D, shaman.position().z)).subtract(new Vec3(target.position().x, target.position().y + target.getBbHeight() / 2D, target.position().z)).normalize();
								Projectile projectile = new BoneShamanProjectile(level, shaman, (float) shaman.getAttributeValue(Attributes.ATTACK_DAMAGE));
								projectile.absMoveTo(shaman.getX() - Math.sin(direction) * 0.5D, shaman.getY() + shaman.getBbHeight() / 2D, shaman.getZ() + Math.cos(direction) * 0.5D, shaman.getYRot(), 0F);
								float shootingAngle = 60 - shaman.getAttackTimer() * 6F;
								float angle = (float) Math.toRadians(shaman.getYHeadRot() + shootingAngle);
								double xOffset = -Math.sin(angle);
								double zOffset = Math.cos(angle);
								Vec3 targetVector = new Vec3(xOffset, 0D, zOffset).normalize();
								projectile.shoot(targetVector.x, -diff.y, targetVector.z, 0.5F, 0F);
								level.addFreshEntity(projectile);
							}
						}
					}

					if (shaman.getAttackTimer() == 19)
						shaman.setReloading(true);
				}
			}
		}

		protected boolean canPerformAttack(LivingEntity entity) {
			return !shaman.isReloading() && shaman.getSensing().hasLineOfSight(entity);
		}
	}

	public static class BoneShamanSummonPuppetsGoal extends Goal {
		private final BoneShaman shaman;

		public BoneShamanSummonPuppetsGoal(BoneShaman shaman) {
			this.shaman = shaman;
			setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return shaman.isSummoningPuppets();
		}

		@Override
		public boolean canContinueToUse() {
			return canUse() && shaman.canSummonNewPuppets() && (getTargetBlockPos() != null  || !shaman.level().isEmptyBlock(getTargetBlockPos()));
		}

		@Override
		   public void start() {
			shaman.setSummonTimer(0);
		}

		@Override
		   public void stop() {
      		shaman.setSummonTimer(0);
    		shaman.setSummoningPuppets(false);
		}

	    @Override
	    public void tick() {
	    	Level level = shaman.level();
	    	if(!level.isClientSide()) {
		    	shaman.stepSummoning();
		        if (getTargetBlockPos() != null && !level.isEmptyBlock(getTargetBlockPos())) {
		        	shaman.getLookControl().setLookAt(getTargetBlockPos().getX(), getTargetBlockPos().getY(), getTargetBlockPos().getZ());
		            if (shaman.getLookControl().isLookingAtTarget()) {
		                shaman.getMoveControl().setWantedPosition(getTargetBlockPos().getX(), getTargetBlockPos().getY(), getTargetBlockPos().getZ(), 0.1D);
		                shaman.hurtMarked = true;
		                if (shaman.getYRot() == shaman.yRotO && shaman.getXRot() == shaman.xRotO) { //jank
		                		shaman.setSummonTimer(shaman.getSummonTimer() + 1);
		                	if(shaman.getSummonTimer() >= 20) {
		                		shaman.spawnPuppets();
		                		shaman.setSummonTimer(0);
		                		shaman.setSummoningPuppets(false);
		                	}
		               }
		            }
		        }
		        else {
            		stop();
		        }
	    	}
	    }
 
	    @Nullable
		private BlockPos getTargetBlockPos() {
			List<BlockPos> list = shaman.getPuppetSummonTargets();
			if(!list.isEmpty())
				return list.get(0);
			return null;
		}
	}

}