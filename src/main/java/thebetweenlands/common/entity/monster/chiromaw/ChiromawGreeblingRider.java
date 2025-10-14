package thebetweenlands.common.entity.monster.chiromaw;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import thebetweenlands.common.entity.ai.goals.ChiromawRiderMoveTowardsTargetGoal;
import thebetweenlands.common.entity.ai.goals.ChiromawRiderSlingshotGoal;
import thebetweenlands.common.entity.ai.goals.ChiromawRiderTargetPullerGoal;
import thebetweenlands.common.entity.ai.goals.NearestSmellyAttackableTargetGoal;
import thebetweenlands.common.entity.creature.GreeblingVolarpadFloater;
import thebetweenlands.common.entity.monster.FlyingMonster;
import thebetweenlands.common.entity.projectile.BetweenstonePebble;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class ChiromawGreeblingRider extends Chiromaw implements RangedAttackMob {

	private static final EntityDataAccessor<Boolean> IS_SHOOTING = SynchedEntityData.defineId(ChiromawGreeblingRider.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> RELOAD_TIMER = SynchedEntityData.defineId(ChiromawGreeblingRider.class, EntityDataSerializers.INT);
	public boolean playPullSound;

	public ChiromawGreeblingRider(EntityType<? extends FlyingMonster> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.095D)
			.add(Attributes.FLYING_SPEED, 0.095D)
			.add(Attributes.MAX_HEALTH, 10.0D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.FOLLOW_RANGE, 30.0D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new ChiromawRiderSlingshotGoal(this));
		this.goalSelector.addGoal(2, new ChiromawRiderMoveTowardsTargetGoal(this, 1.5D, 8, 128));
		this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 0.75D));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new ChiromawRiderTargetPullerGoal<>(this, LivingEntity.class, true, 6).setUnseenMemoryTicks(160));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, ChiromawMatriarch.class, true).setUnseenMemoryTicks(160));
		this.targetSelector.addGoal(4, new NearestSmellyAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_SHOOTING, false);
		builder.define(RELOAD_TIMER, 0);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide()) {
			if (this.getTarget() != null) {
				if (this.getReloadTimer() < 90 && !this.isShooting()) {
					this.setReloadTimer(Math.min(90, this.getReloadTimer() + 2));
				}
				if (this.getReloadTimer() >= 90 && this.isShooting() && this.getReloadTimer() < 100) {
					this.setReloadTimer(Math.min(100, this.getReloadTimer() + 4));
				}
				this.startUsingItem(InteractionHand.OFF_HAND);
			} else {
				if (this.getReloadTimer() > 0 && !this.isShooting()) {
					this.setReloadTimer(Math.max(0, this.getReloadTimer() - 2));
				}
				this.stopUsingItem();
			}

			if (this.getReloadTimer() <= 0)
				this.playPullSound = true;

			if (this.isPulling())
				if (this.playPullSound) {
					this.playSound(SoundRegistry.SLINGSHOT_CHARGE.get());
					this.playPullSound = false;
				}
		}
	}

	@Override
	protected void triggerOnDeathMobEffects(RemovalReason reason) {
		super.triggerOnDeathMobEffects(reason);

		if (!this.level().isClientSide() && reason == RemovalReason.KILLED) {
			GreeblingVolarpadFloater floater = new GreeblingVolarpadFloater(this.level(), this.getX(), this.getY(), this.getZ());
			this.level().addFreshEntity(floater);
		}
	}

	@Override
	public boolean isHanging() {
		return false;
	}

	public boolean isShooting() {
		return this.getEntityData().get(IS_SHOOTING);
	}

	public void setShooting(boolean shooting) {
		this.getEntityData().set(IS_SHOOTING, shooting);
	}

	public int getReloadTimer() {
		return this.getEntityData().get(RELOAD_TIMER);
	}

	public void setReloadTimer(int timer) {
		this.getEntityData().set(RELOAD_TIMER, timer);
	}

	public boolean isPulling() {
		return this.getEntityData().get(RELOAD_TIMER) > 0 && this.getEntityData().get(RELOAD_TIMER) < 90;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
		this.populateDefaultEquipmentEnchantments(level, level.getRandom(), difficulty);
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
		this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemRegistry.SLINGSHOT.get()));
	}

	public int getLastHurtByPlayerTimestamp() {
		return this.lastHurtByPlayerTime;
	}

	@Override
	public void performRangedAttack(LivingEntity target, float velocity) {
		double targetX = target.getX() - this.getX();
		double targetY = target.getBoundingBox().minY + (double) (target.getBbHeight() / 2.0F) - (this.getY() + (double) (this.getBbHeight() / 2.0F));
		double targetZ = target.getZ() - this.getZ();
		double targetDistance = Mth.sqrt((float) (targetX * targetX + targetZ * targetZ));
		BetweenstonePebble pebble = new BetweenstonePebble(this, this.level(), ItemStack.EMPTY, null);
		pebble.shoot(targetX, targetY + targetDistance * 0.1D, targetZ, 1.6F, 0.0F);
		this.level().addFreshEntity(pebble);
		this.playSound(SoundRegistry.SLINGSHOT_SHOOT.get(), 1.0F, this.getVoicePitch());
		this.playPullSound = true;
	}
}
