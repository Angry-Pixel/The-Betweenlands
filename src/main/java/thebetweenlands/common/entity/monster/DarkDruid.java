package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ai.goals.DruidHurtByTargetGoal;
import thebetweenlands.common.entity.ai.goals.DruidNearestAttackableTargetGoal;
import thebetweenlands.common.entity.ai.goals.DruidTeleportGoal;
import thebetweenlands.common.network.clientbound.DruidParticlePacket;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.UUID;

public class DarkDruid extends Monster {

	private static final EntityDataAccessor<Boolean> CASTING = SynchedEntityData.defineId(DarkDruid.class, EntityDataSerializers.BOOLEAN);

	private static final int MIN_ATTACK_DELAY = 40, MAX_ATTACK_DELAY = 120;
	private static final int MAX_ATTACK_TIME = 20;

	private static final int MAX_ATTACK_ANIMATION_TIME = 8;

	private MeleeAttackGoal meleeAI;
	private WaterAvoidingRandomStrollGoal wanderAI;
	private LookAtPlayerGoal watchAI;

	private int attackDelayCounter;
	private int attackCounter;
	private int teleportCooldown;
	private boolean isWatching = true;

	private int prevAttackAnimationTime;
	private int attackAnimationTime;

	public DarkDruid(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 10;
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		GroundPathNavigation nav = new GroundPathNavigation(this, level);
		nav.setCanOpenDoors(false);
		nav.setCanFloat(true);
		nav.setCanPassDoors(true);
		return nav;
	}

	@Override
	protected void registerGoals() {
		this.meleeAI = new MeleeAttackGoal(this, 0.6F, true);
		this.wanderAI = new WaterAvoidingRandomStrollGoal(this, 0.8F);
		this.watchAI = new LookAtPlayerGoal(this, Player.class, 16);

		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new BreakDoorGoal(this, difficulty -> true));
		this.goalSelector.addGoal(2, this.meleeAI);
		this.goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.23F));
		this.goalSelector.addGoal(4, this.wanderAI);
		this.goalSelector.addGoal(5, this.watchAI);
		this.goalSelector.addGoal(6, new DruidTeleportGoal(this));

		this.targetSelector.addGoal(0, new DruidHurtByTargetGoal(this, DarkDruid.class));
		this.targetSelector.addGoal(1, new DruidNearestAttackableTargetGoal(this));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(CASTING, false);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 50.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.45D)
			.add(Attributes.ATTACK_DAMAGE, 5.0D)
			.add(Attributes.FOLLOW_RANGE, 16.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (!this.level().isClientSide()) {
			if (this.getTarget() != null) {
				if (this.attackDelayCounter > 0 && !this.isCasting() && this.getTarget().distanceTo(this) < 10.0D) {
					this.attackDelayCounter--;
				}
				if (this.attackDelayCounter <= 0 || this.attackCounter > 0) {
					if (this.hasLineOfSight(this.getTarget())) {
						if (attackCounter == 0) {
							if (this.getTarget().onGround() && !this.getTarget().isPassenger()) {
								attackCounter++;
								if (!this.level().isClientSide()) {
									this.goalSelector.removeGoal(this.meleeAI);
								}
							}
						} else if (this.attackCounter < MAX_ATTACK_TIME) {
							this.attackCounter++;
							this.startCasting();
							if (!this.level().isClientSide()) {
								this.chargeSpell(this.getTarget());
							}
						} else {
							this.attackDelayCounter = MIN_ATTACK_DELAY + this.getRandom().nextInt(MAX_ATTACK_DELAY - MIN_ATTACK_DELAY + 1) + 1;
							this.attackCounter = 0;
							this.stopCasting();
							if (!this.level().isClientSide()) {
								if (!this.getTarget().isPassenger()) {
									this.castSpell(this.getTarget());
								}
								this.goalSelector.addGoal(2, this.meleeAI);
							}
						}
					}
				}
			} else if (this.isCasting() || this.attackCounter != 0) {
				if (this.attackDelayCounter <= 0) {
					this.attackDelayCounter = MIN_ATTACK_DELAY + this.getRandom().nextInt(MAX_ATTACK_DELAY - MIN_ATTACK_DELAY + 1) + 1;
				}
				this.attackCounter = 0;
				this.stopCasting();
			}
		}
		if (this.level().isClientSide()) {
			this.yBodyRotO = this.yRotO;
			this.yBodyRot = this.getYRot();
			this.prevAttackAnimationTime = this.attackAnimationTime;
			if (this.isCasting()) {
				if (this.attackAnimationTime < MAX_ATTACK_ANIMATION_TIME) {
					this.attackAnimationTime++;
				}
				this.spawnParticles();
			} else {
				if (this.attackAnimationTime > 0) {
					this.attackAnimationTime--;
				}
			}
		} else {
			if (this.getTarget() != null) {
				this.lookAt(this.getTarget(), 100, 100);
			}
			if (this.teleportCooldown > 0) {
				this.teleportCooldown--;
			}
		}
	}

	private void enableWatch() {
		if (!this.isWatching) {
			this.isWatching = true;
			this.goalSelector.addGoal(5, this.watchAI);
		}
	}

	private void disableWatch() {
		if (this.isWatching) {
			this.isWatching = false;
			this.goalSelector.removeGoal(this.watchAI);
		}
	}

	public boolean teleportNearEntity(Entity entity) {
		double targetX = entity.getX() + (this.getRandom().nextDouble() - 0.5D) * 6.0D;
		double targetY = entity.getY() + (this.getRandom().nextInt(3) - 1);
		double targetZ = entity.getZ() + (this.getRandom().nextDouble() - 0.5D) * 6.0D;
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		boolean successful = false;
		BlockPos pos = this.blockPosition();
		if (this.level().isLoaded(pos)) {
			boolean validBlock = false;
			while (!validBlock && pos.getY() > 0) {
				BlockState block = this.level().getBlockState(pos.below());
				if (block.isSolid()) {
					validBlock = true;
				} else {
					pos = pos.below();
				}
			}
			if (validBlock) {
				this.teleportCooldown = this.getRandom().nextInt(20 * 2) + 20 * 2;
				DarkDruid newDruid = new DarkDruid(EntityRegistry.DARK_DRUID.get(), this.level());
				newDruid.copyDataFromOld(this);
				newDruid.setUUID(UUID.randomUUID());
				newDruid.setPos(targetX, targetY, targetZ);
				newDruid.lookAt(entity, 100, 100);
				newDruid.setTarget(this.getTarget());
				newDruid.attackDelayCounter = MIN_ATTACK_DELAY + this.getRandom().nextInt(MAX_ATTACK_DELAY - MIN_ATTACK_DELAY + 1) + 1;
				if (!this.level().collidesWithSuffocatingBlock(newDruid, newDruid.getBoundingBox()) && !this.level().containsAnyLiquid(newDruid.getBoundingBox())) {
					successful = true;
					this.discard();
					this.level().addFreshEntity(newDruid);
					this.druidParticlePacketOrigin();
					this.druidParticlePacketTarget(newDruid);

					this.playSound(SoundRegistry.DRUID_TELEPORT.get(), 1.0F, 1.0F);
					newDruid.playSound(SoundRegistry.DRUID_TELEPORT.get(), 1.0F, 1.0F);
				} else
					newDruid.discard();
			}
		}

		if (successful) {
			return true;
		}
		this.setPos(x, y, z);
		return false;
	}

	private void druidParticlePacketTarget(DarkDruid newDruid) {
		if (this.level() instanceof ServerLevel server) {
			PacketDistributor.sendToPlayersNear(server, null, newDruid.getX(), newDruid.getY(), newDruid.getZ(), 64.0D, new DruidParticlePacket(newDruid.blockPosition()));
		}
	}

	private void druidParticlePacketOrigin() {
		if (this.level() instanceof ServerLevel server) {
			PacketDistributor.sendToPlayersNear(server, null, this.getX(), this.getY(), this.getZ(), 64.0D, new DruidParticlePacket(this.blockPosition()));
		}
	}

	public void spawnParticles() {
		double yaw = this.getYRot() * Mth.DEG_TO_RAD;
		double y = Math.cos(-this.getXRot() * Mth.DEG_TO_RAD);
		double offsetX = -Math.sin(yaw) * 0.5D * y;
		double offsetY = 1.2 - Math.sin(-this.getXRot() * Mth.DEG_TO_RAD) * 0.5D * y;
		double offsetZ = Math.cos(yaw) * 0.5D * y;
		double motionX = -Math.sin(yaw) * y * 0.2 * (this.getRandom().nextDouble() * 0.7 + 0.3) + this.getRandom().nextDouble() * 0.05 - 0.025;
		double motionY = Math.sin(-this.getXRot() * Mth.DEG_TO_RAD) + this.getRandom().nextDouble() * 0.25 - 0.125;
		double motionZ = Math.cos(yaw) * y * 0.2 * (this.getRandom().nextDouble() * 0.7 + 0.3) + this.getRandom().nextDouble() * 0.05 - 0.025;
		TheBetweenlands.createParticle(ParticleRegistry.DRUID_CASTING.get(), this.level(), this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, ParticleFactory.ParticleArgs.get().withMotion(motionX, motionY, motionZ).withScale(this.getRandom().nextFloat() + 0.5F));
	}

	public void chargeSpell(Entity entity) {
		if (entity.distanceTo(this) <= 4) {
			double dx = entity.getX() - this.getX();
			double dz = entity.getZ() - this.getZ();
			double len = Math.sqrt(dx * dx + dz * dz);
			entity.setDeltaMovement(1.5D * dx / len, 0.1D, 1.5D * dx / len);
		} else {
			entity.setDeltaMovement(0.0D, 0.1D, 0.0D);
		}
		entity.hurtMarked = true;
	}

	public void castSpell(Entity entity) {
		double dx = entity.getX() - this.getX();
		double dz = entity.getZ() - this.getZ();
		double len = Math.sqrt(dx * dx + dz * dz);
		entity.setDeltaMovement(0.5D * dx / len, 1.05D, 0.5D * dx / len);
		entity.hurtMarked = true;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 5;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.DARK_DRUID_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.DARK_DRUID_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.DARK_DRUID_DEATH.get();
	}

	public int getAttackCounter() {
		return this.attackCounter;
	}

	private void copyDataFromOld(Entity entity) {
		CompoundTag compound = new CompoundTag();
		entity.save(compound);
		compound.remove("Dimension");
		this.load(compound);
	}

	public void startCasting() {
		this.getEntityData().set(CASTING, true);
		this.enableWatch();
	}

	public void stopCasting() {
		this.getEntityData().set(CASTING, false);
		this.disableWatch();
	}


	public boolean isCasting() {
		return this.getEntityData().get(CASTING);
	}

	public boolean canTeleport() {
		return !this.isCasting() && this.teleportCooldown == 0;
	}

	public float getAttackAnimationTime(float partialRenderTicks) {
		return (this.prevAttackAnimationTime + (this.attackAnimationTime - this.prevAttackAnimationTime) * partialRenderTicks) / MAX_ATTACK_ANIMATION_TIME;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("teleport", this.teleportCooldown);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.teleportCooldown = compound.getInt("teleport");
	}
}
