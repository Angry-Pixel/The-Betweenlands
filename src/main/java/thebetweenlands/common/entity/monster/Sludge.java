package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.model.ControlledAnimation;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.SludgeAttackGoal;
import thebetweenlands.common.entity.ai.goals.SludgeFloatGoal;
import thebetweenlands.common.entity.ai.goals.SludgeJumpGoal;
import thebetweenlands.common.entity.ai.goals.SludgeRandomDirectionGoal;
import thebetweenlands.common.entity.movement.SludgeMoveControl;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class Sludge extends Monster implements BLEntity {

	public static final EntityDataAccessor<Boolean> IS_ACTIVE = SynchedEntityData.defineId(Sludge.class, EntityDataSerializers.BOOLEAN);

	private float squishAmount;
	private float squishFactor;
	private float prevSquishFactor;
	private boolean wasOnGround;

	public ControlledAnimation scale = new ControlledAnimation(5);

	protected int attackCooldown = 0;

	public Sludge(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.moveControl = new SludgeMoveControl(this);
		this.xpReward = 4;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new SludgeFloatGoal(this));
		this.goalSelector.addGoal(1, new SludgeAttackGoal(this));
		this.goalSelector.addGoal(2, new SludgeRandomDirectionGoal(this));
		this.goalSelector.addGoal(3, new SludgeJumpGoal(this));

		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_ACTIVE, true);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
			.add(Attributes.MAX_HEALTH, 20.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.6D)
			.add(Attributes.ATTACK_DAMAGE, 1.5D);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("wasOnGround", this.wasOnGround);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("wasOnGround")) {
			this.wasOnGround = compound.getBoolean("wasOnGround");
		}
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.setActive(this.level().getRandom().nextInt(5) == 0 || !this.canHideIn(this.level().getBlockState(this.blockPosition().below())));
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	@Override
	public boolean isInvisible() {
		return super.isInvisible();
	}

	@Override
	public boolean isInvisibleTo(Player player) {
		if (!player.isCreative() && !player.isSpectator() && !this.isActive()) {
			return true;
		}
		return super.isInvisibleTo(player);
	}

	@Override
	public void tick() {
		if (this.attackCooldown > 0) {
			this.attackCooldown--;
		}

		this.prevSquishFactor = this.squishFactor;
		this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;

		if (!this.level().isClientSide()) {
			if (this.getIsPlayerNearby(7, 3, 7, 7) || this.getTarget() != null || this.level().getRandom().nextInt(2200) == 0) {
				if (!this.isActive()) {
					this.setActive(true);
					this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.6D, 0.0D));
				}
			}

			if (this.isActive()) {
				if (this.canMakeTrail() && this.level().getNearestPlayer(this, 16) != null) {
					if (this.level().isEmptyBlock(this.blockPosition())) {
						this.createTrail(this.blockPosition());
					}
				}

				if (this.getTarget() == null && this.onGround() && this.level().getRandom().nextInt(350) == 0 && !this.isInWater() && this.canHideIn(this.level().getBlockState(this.blockPosition().below()))) {
					this.setActive(false);
				}
			} else if (this.isInWater() || !this.onGround()) {
				this.setActive(true);
			} else {
				this.setDeltaMovement(0.0D, this.getDeltaMovement().y() - 0.1D, 0.0D);
			}
		}

		super.tick();

		if (this.onGround() && !this.wasOnGround) {
			//Particles
			this.squishAmount = -0.5F;
		} else if (!this.onGround() && this.wasOnGround) {
			this.squishAmount = 1.0F;
		}

		this.wasOnGround = this.onGround();
		this.alterSquishAmount();

		//Update animation
		if (this.level().isClientSide()) {
			this.scale.updateTimer();
			if (this.isActive()) {
				this.scale.increaseTimer();
			} else {
				this.scale.decreaseTimer();
			}
		}
	}

	protected boolean canHideIn(BlockState state) {
		return state.is(BlockTags.DIRT); //TODO custom tag, used ground, sand, and grass materials previously
	}

	protected boolean canMakeTrail() {
		return true;
	}

	protected void createTrail(BlockPos pos) {
		if (BlockRegistry.SLUDGE.get().defaultBlockState().canSurvive(this.level(), pos)) {
			BlockRegistry.SLUDGE.get().generateTemporaryBlock(this.level(), pos);
		}
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		return super.getDefaultDimensions(pose).scale(this.isActive() ? 1.0F : 0.5F);
	}

	public float getSquishFactor(float partialTicks) {
		return this.prevSquishFactor + (this.squishFactor - this.prevSquishFactor) * partialTicks;
	}

	protected void alterSquishAmount() {
		this.squishAmount *= 0.8F;
	}

	public int getJumpDelay() {
		return this.getRandom().nextInt(20) + 10;
	}

	@Override
	protected boolean isImmobile() {
		return super.isImmobile() || !this.isActive();
	}

	@Override
	public void playerTouch(Player player) {
		if (this.attackCooldown <= 0) {
			this.attackCooldown = 20;
			this.dealDamage(player);
		}
	}

	protected void dealDamage(LivingEntity entity) {
		if (this.isActive() && this.hasLineOfSight(entity) && this.distanceToSqr(entity) < 2.5D && entity.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
			this.playSound(SoundRegistry.SLUDGE_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
		}
	}

	@Override
	public float getVoicePitch() {
		return (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public int getMaxHeadXRot() {
		return 0;
	}

	public boolean makesSoundOnJump() {
		return true;
	}

	@Override
	public void jumpFromGround() {
		this.setDeltaMovement(this.getDeltaMovement().x(), 0.42D, this.getDeltaMovement().z());
		this.hasImpulse = true;
	}

	public SoundEvent getJumpSound() {
		return SoundRegistry.SLUDGE_JUMP.get();
	}

	public void setActive(boolean active) {
		this.getEntityData().set(IS_ACTIVE, active);
		this.refreshDimensions();
	}

	public boolean isActive() {
		return this.getEntityData().get(IS_ACTIVE);
	}

	protected boolean getIsPlayerNearby(double distanceX, double distanceY, double distanceZ, double radius) {
		List<Player> entities = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(distanceX, distanceY, distanceZ), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
		for (Player entityNeighbor : entities) {
			if (this.distanceTo(entityNeighbor) <= radius && this.hasLineOfSight(entityNeighbor))
				return true;
		}
		return false;
	}


	@Override
	public void knockback(double strength, double xRatio, double zRatio) {
		if (this.isActive()) {
			super.knockback(strength, xRatio, zRatio);
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!this.isActive() && !source.isCreativePlayer()) {
			if (!this.level().isClientSide()) {
				this.setActive(true);
			}
			return false;
		}
		return super.hurt(source, amount);
	}
}
