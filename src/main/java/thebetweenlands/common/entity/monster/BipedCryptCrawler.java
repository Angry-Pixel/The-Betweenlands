package thebetweenlands.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.CryptCrawlerBlockGoal;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class BipedCryptCrawler extends Monster implements BLEntity {

	private static final byte EVENT_SHIELD_BLOCKED = 80;
	private static final EntityDataAccessor<Boolean> IS_BLOCKING = SynchedEntityData.defineId(BipedCryptCrawler.class, EntityDataSerializers.BOOLEAN);

	public boolean recentlyBlockedAttack;

	public BipedCryptCrawler(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 10;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 30.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.29D)
			.add(Attributes.ATTACK_DAMAGE, 2.5D)
			.add(Attributes.FOLLOW_RANGE, 20.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_BLOCKING, false);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FloatGoal(this));
		this.goalSelector.addGoal(3, new CryptCrawlerBlockGoal(this));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, false));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, 3, true, true, null).setUnseenMemoryTicks(120));
		this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers(CryptCrawler.class, BipedCryptCrawler.class, ChiefCryptCrawler.class));
	}

	public boolean isBlocking() {
		return this.getEntityData().get(IS_BLOCKING);
	}

	public void setIsBlocking(boolean blocking) {
		this.getEntityData().set(IS_BLOCKING, blocking);
	}

	@Override
	public void aiStep() {
		if (this.isBlocking() && !this.getOffhandItem().isEmpty() && this.getOffhandItem().canPerformAction(ItemAbilities.SHIELD_BLOCK)) {
			this.startUsingItem(InteractionHand.OFF_HAND);

			//"Fix" for janky item pose
			if (this.level().isClientSide()) {
				this.useItem = this.getOffhandItem();
			}
		} else if (this.isUsingItem() && this.getUsedItemHand() == InteractionHand.OFF_HAND) {
			this.stopUsingItem();
		}
		super.aiStep();
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if(!this.isBlocking() && this.hasLineOfSight(entity)) {
			boolean hasHitTarget = entity.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));

			if (hasHitTarget) {
				if (!this.level().isClientSide()) {
					this.playSound(this.getAmbientSound(), 1.0F, 0.5F);
				}
			}
			return hasHitTarget;
		}
		return false;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.tickCount < 40 && source.is(DamageTypes.IN_WALL)) {
			return false;
		}

		boolean wasAttackBlocked = super.hurt(source, amount);

		if(this.isBlocking() && !wasAttackBlocked) {
			this.recentlyBlockedAttack = true;

			//Play shield block sound to all listeners. For some reason shield block sound from item doesn't seem to work.
			this.level().broadcastEntityEvent(this, EVENT_SHIELD_BLOCKED);
		}

		return wasAttackBlocked;
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
		RandomSource randomsource = level.getRandom();
		this.populateDefaultEquipmentSlots(randomsource, difficulty);
		this.populateDefaultEquipmentEnchantments(level, randomsource, difficulty);
		return spawnGroupData;
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
		int randomWeapon = random.nextInt(5);
		int randomShield = random.nextInt(3);

		switch (randomWeapon) {
			case 0:
				this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.WIGHTS_BANE.get()));
				break;
			case 1:
				this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.WEEDWOOD_SWORD.get()));
				break;
			case 2:
				this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.BONE_SWORD.get()));
				break;
			case 3:
				this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.BONE_AXE.get()));
				break;
			case 4:
				this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemRegistry.WEEDWOOD_AXE.get()));
				break;
		}

		if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
			switch (randomShield) {
				case 0:
					this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
					break;
				case 1:
					this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemRegistry.WEEDWOOD_SHIELD.get()));
					break;
				case 2:
					this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ItemRegistry.BONE_SHIELD.get()));
					break;
			}
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if(id == EVENT_SHIELD_BLOCKED) {
			this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level().getRandom().nextFloat() * 0.4F);
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CRYPT_CRAWLER_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CRYPT_CRAWLER_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CRYPT_CRAWLER_DEATH.get();
	}

	@Override
	public float getSpeed() {
		//Half move speed when blocking
		return (this.isBlocking() ? 0.5F : 1.0F) * super.getSpeed();
	}
}
