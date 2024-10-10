package thebetweenlands.common.entity.fishing.anadia;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.tags.BLBiomeTagProvider;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.fishing.BLFishHook;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Anadia extends PathfinderMob implements BLEntity {
	public static final String HEAD_KEY = "head_item";
	public static final String BODY_KEY = "body_item";
	public static final String TAIL_KEY = "tail_item";

	public static final String SPEED_MOD = "speed_modifier";
	public static final String HEALTH_MOD = "health_modifier";
	public static final String STRENGTH_MOD = "strength_modifier";
	public static final String STAMINA_MOD = "stamina_modifier";

	private static final EntityDataAccessor<Float> FISH_SIZE = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Byte> HEAD_TYPE = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Byte> BODY_TYPE = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Byte> TAIL_TYPE = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Boolean> IS_LEAPING = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.BOOLEAN);
	//private static final EntityDataAccessor<Integer> HUNGER_COOLDOWN = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> STAMINA_TICKS = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Byte> FISH_COLOR = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Integer> ESCAPE_TICKS = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ESCAPE_DELAY = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> OBSTRUCTION_TICKS_1 = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> OBSTRUCTION_TICKS_2 = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> OBSTRUCTION_TICKS_3 = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> OBSTRUCTION_TICKS_4 = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<ItemStack> HEAD_ITEM = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<ItemStack> BODY_ITEM = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<ItemStack> TAIL_ITEM = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> IS_TREASURE_FISH = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> TREASURE_TICKS = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> TREASURE_UNLOCKED = SynchedEntityData.defineId(Anadia.class, EntityDataSerializers.BOOLEAN);

	public AnadiaFindBaitGoal findBaitGoal;
	public AnadiaFindHookGoal findHookGoal;

	public boolean playAnadiaWonSound = true;

	private static final int MAX_NETTABLE_TIME = 20;
	private int nettableTimer = 0;
	private int glowTimer = 0;

	private float healthMod;
	private float speedMod;
	private float strengthMod;
	private float staminaMod;

	public Anadia(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.xpReward = 3;
		this.moveControl = new AnadiaMoveControl(this);
		this.setPathfindingMalus(PathType.WALKABLE, -1.0F);
		this.setPathfindingMalus(PathType.BLOCKED, -1.0F);
		this.setPathfindingMalus(PathType.WATER, 0.0F);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return WaterAnimal.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 5.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.2D)
			.add(Attributes.ATTACK_DAMAGE, 1.0D)
			.add(Attributes.FOLLOW_RANGE, 12.0D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.7D, true));
		//this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Lurker.class, 8F, 4D, 8D));
		this.goalSelector.addGoal(2, new MoveTowardsRestrictionGoal(this, 0.4D));
		this.goalSelector.addGoal(3, new AnadiaWanderGoal(this, 0.5D, 20));
		this.goalSelector.addGoal(4, new PanicWhenUnhookedGoal(this));
		this.goalSelector.addGoal(5, new PanicWhenHookedGoal(this));
		this.findBaitGoal = new AnadiaFindBaitGoal(this, 3.0D);
		this.findHookGoal = new AnadiaFindHookGoal(this, 3.0D);
		this.goalSelector.addGoal(6, this.findBaitGoal);
		this.goalSelector.addGoal(7, this.findHookGoal);
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(FISH_SIZE, 0.5F);
		builder.define(HEAD_TYPE, (byte) AnadiaParts.AnadiaHeadParts.HEAD_1.ordinal());
		builder.define(BODY_TYPE, (byte) AnadiaParts.AnadiaBodyParts.BODY_1.ordinal());
		builder.define(TAIL_TYPE, (byte) AnadiaParts.AnadiaTailParts.TAIL_1.ordinal());
		builder.define(IS_LEAPING, false);
		//  builder.define(HUNGER_COOLDOWN, 0);
		builder.define(STAMINA_TICKS, 40);
		builder.define(ESCAPE_TICKS, 1024);
		builder.define(ESCAPE_DELAY, 80);
		builder.define(OBSTRUCTION_TICKS_1, 64);
		builder.define(OBSTRUCTION_TICKS_2, 128);
		builder.define(OBSTRUCTION_TICKS_3, 192);
		builder.define(OBSTRUCTION_TICKS_4, 256);
		builder.define(FISH_COLOR, (byte) AnadiaParts.AnadiaColor.BASE.ordinal());
		builder.define(HEAD_ITEM, ItemStack.EMPTY);
		builder.define(BODY_ITEM, ItemStack.EMPTY);
		builder.define(TAIL_ITEM, ItemStack.EMPTY);
		builder.define(IS_TREASURE_FISH, false);
		builder.define(TREASURE_TICKS, 256);
		builder.define(TREASURE_UNLOCKED, false);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (FISH_SIZE.equals(key)) {
			this.refreshDimensions();
			this.setYRot(this.getYHeadRot());
			this.setYBodyRot(this.getYHeadRot());
		}
		if (FISH_COLOR.equals(key)) {
			this.setFishColor(this.getFishColor());
		}
		super.onSyncedDataUpdated(key);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.randomizeAnadiaProperties();
		this.randomiseObstructionOrder();
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	private void recalculateModifiers() {
		this.speedMod = Math.round((this.getFishSize() * 0.5F) * this.getHeadType().getSpeedModifier() + this.getBodyType().getSpeedModifier() + this.getTailType().getSpeedModifier() * 16F) / 16F;
		this.healthMod = Math.round(this.getFishSize() * this.getHeadType().getHealthModifier() + this.getBodyType().getHealthModifier() + this.getTailType().getHealthModifier() * 2F) / 2F;
		this.strengthMod = Math.round((this.getFishSize() * 0.5F) * this.getHeadType().getStrengthModifier() + this.getBodyType().getStrengthModifier() + this.getTailType().getStrengthModifier() * 2F) / 2F;
		this.staminaMod = Math.max(Math.round(this.getFishSize() * this.getHeadType().getStaminaModifier() + this.getBodyType().getStaminaModifier() + this.getTailType().getStaminaModifier() * 2F) / 2F, 3.5F);
	}

	public void randomizeAnadiaProperties() {
		this.setHeadType(AnadiaParts.AnadiaHeadParts.random(this.getRandom()));
		this.setBodyType(AnadiaParts.AnadiaBodyParts.random(this.getRandom()));
		this.setTailType(AnadiaParts.AnadiaTailParts.random(this.getRandom()));
		this.setFishSize(Math.round(Math.max(0.125F, this.getRandom().nextFloat()) * 16F) / 16F);
		if (this.level().getBiome(this.blockPosition()).is(BLBiomeTagProvider.SPAWNS_SILVER_ANADIA)) {
			this.setFishColor(AnadiaParts.AnadiaColor.SILVER);
		} else if (this.level().getBiome(this.blockPosition()).is(BLBiomeTagProvider.SPAWNS_GREEN_ANADIA)) {
			this.setFishColor(AnadiaParts.AnadiaColor.GREEN);
		} else if (this.level().getBiome(this.blockPosition()).is(BLBiomeTagProvider.SPAWNS_PURPLE_ANADIA)) {
			this.setFishColor(AnadiaParts.AnadiaColor.PURPLE);
		} else {
			this.setFishColor(AnadiaParts.AnadiaColor.BASE);
		}
		this.setHeadItem(getPartFromLootTable(this.level(), this.position(), this, LootTableRegistry.ANADIA_HEAD));
		this.setBodyItem(getPartFromLootTable(this.level(), this.position(), this, LootTableRegistry.ANADIA_BODY));
		this.setTailItem(getPartFromLootTable(this.level(), this.position(), this, LootTableRegistry.ANADIA_TAIL));

		if (!this.level().getBiome(this.blockPosition()).is(BiomeRegistry.DEEP_WATERS)) {
			if (this.getStaminaModifier() >= 5.0F && this.getFishSize() >= 0.875F) {
				this.setIsTreasureFish(true);
			}
		} else if (this.getStaminaModifier() >= 4.5F && this.getFishSize() >= 0.5F) { // will happen on smaller && less stamina fish more often in deep waters
			this.setIsTreasureFish(true);
		}
	}

	public static ItemStack getPartFromLootTable(Level level, Vec3 pos, @Nullable Entity entity, ResourceKey<LootTable> table) {
		if (level instanceof ServerLevel server) {
			LootTable lootTable = server.getServer().reloadableRegistries().getLootTable(table);
			if (lootTable != LootTable.EMPTY) {
				LootParams lootparams = new LootParams.Builder(server)
					.withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
					.withParameter(LootContextParams.ORIGIN, pos)
					.create(LootContextParamSets.EQUIPMENT);
				List<ItemStack> loot = lootTable.getRandomItems(lootparams);
				if (!loot.isEmpty())
					return loot.getFirst();
			}
		}
		return ItemStack.EMPTY; // to stop null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.FISH_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FISH_DEATH.get();
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public void aiStep() {
		if (this.level().isClientSide() && this.level().getGameTime() % 5 + this.level().getRandom().nextInt(5) == 0) {
			if (this.canDrownInFluidType(this.getEyeInFluidType())) {
				for (int i = 0; i < 2; ++i) {
					double a = Math.toRadians(this.getYRot());
					double offSetX = -Math.sin(a) * this.getBbWidth() * 0.5D;
					double offSetZ = Math.cos(a) * this.getBbWidth() * 0.5D;
					this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + offSetX, this.getY() + this.getBbHeight() * 0.5D + this.getRandom().nextDouble() * 0.5D, this.getZ() + offSetZ, 0.0D, 0.4D, 0.0D);
				}
			}
		}

		if (this.glowTimer > 0) {
			this.glowTimer--;
		}

		if (this.isInWater()) {
			this.setAirSupply(300);
		} else if (this.onGround()) {
			this.setDeltaMovement(this.getDeltaMovement().add((this.getRandom().nextFloat() * 2.0F - 1.0F) * 0.4F, 0.5D, (this.getRandom().nextFloat() * 2.0F - 1.0F) * 0.4F));
			this.setYRot(this.getRandom().nextFloat() * 360.0F);
			if (this.isLeaping())
				this.setIsLeaping(false);
			this.setOnGround(false);
			this.hasImpulse = true;
			if (this.level().getGameTime() % 5 == 0)
				this.level().playSound(null, this.blockPosition(), SoundRegistry.FISH_FLOP.get(), SoundSource.HOSTILE, 1F, 1F);
			this.hurt(this.damageSources().drown(), 0.5F);
		}

		super.aiStep();
	}

	@Override
	public boolean isInWater() {
		return this.canDrownInFluidType(this.getEyeInFluidType());
	}

	@Override
	public void tick() {
		if (this.getStaminaTicks() <= 0 && this.isVehicle() && this.getFirstPassenger() instanceof BLFishHook) {
			this.nettableTimer = MAX_NETTABLE_TIME;
		} else if (this.nettableTimer > 0) {
			this.nettableTimer--;
		}

		if (this.level().isClientSide()) {
			this.refreshDimensions();
		}

		if (!this.level().isClientSide()) {
			if (this.getTarget() != null && !this.level().containsAnyLiquid(this.getTarget().getBoundingBox())) {
				double distance = this.blockPosition().distToCenterSqr(this.getTarget().getX(), this.getTarget().getY(), this.getTarget().getZ());
				if (distance > 1.0F && distance < 6.0F)
					if (this.isInWater() && this.level().isEmptyBlock(this.blockPosition().above()))
						this.leapAtTarget(this.getTarget().getX(), this.getTarget().getZ());
			}

			//			if(this.getHungerCooldown() >= 0)
			//				this.setHungerCooldown(this.getHungerCooldown() - 1);

			//regains stamina over time whilst not hooked
			if (!this.isVehicle()) {
				this.playAnadiaWonSound = true;

				if (this.getEscapeDelay() < (int) this.getStaminaModifier() * 30)
					this.setEscapeDelay((int) (this.getStaminaModifier() * 30));

				if (this.getStaminaTicks() < (int) (this.getStaminaModifier() * 20))
					this.setStaminaTicks(this.getStaminaTicks() + 1);
				//if(this.getEscapeTicks() < 1024)
				//	this.setEscapeTicks(1024);
				if (this.getObstruction1Ticks() < 256)
					this.setObstruction1Ticks(256);
				if (this.getObstruction2Ticks() < 256)
					this.setObstruction2Ticks(256);
				if (this.getObstruction3Ticks() < 256)
					this.setObstruction3Ticks(256);
				if (this.getObstruction4Ticks() < 512)
					this.setObstruction4Ticks(512);
				if (this.getTreasureTicks() < 1024)
					this.setTreasureTicks(1024);
			}

			if (this.isVehicle() && this.getFirstPassenger() instanceof BLFishHook hook) {
				if (this.getStaminaTicks() <= 0 && this.playAnadiaWonSound) {
					if (hook.getPlayerOwner() != null)
						this.playAnadiaWonSound(hook.getPlayerOwner());
					this.playAnadiaWonSound = false;
					this.getNavigation().stop();
				}

				if (this.getStaminaTicks() > 0 && this.getEscapeDelay() > 0) {
					this.setEscapeDelay(this.getEscapeDelay() - 1);

					if (this.getEscapeDelay() == 10)
						if (hook.getPlayerOwner() != null)
							this.playAnadiaCrab(hook.getPlayerOwner());
				}

				if (this.getEscapeTicks() > 0 && this.getEscapeDelay() <= 0)
					this.setEscapeTicks(this.getEscapeTicks() - 3);

				if (this.getEscapeTicks() * 256 / 1024 < this.getStaminaTicks() * 256 / 180 && this.getEscapeDelay() <= 0) {
					if (hook.getPlayerOwner() != null) {
						this.playAnadiaLostSound(hook.getPlayerOwner());
						this.getPassengers().getFirst().stopRiding(); // this just  releases the fish atm
					}
				}

				if (this.getObstruction1Ticks() >= 0) {
					this.setObstruction1Ticks(this.getObstruction1Ticks() - 1);
					if (this.getObstruction1Ticks() <= 0)
						this.setObstruction1Ticks(256);
				}

				if (this.getObstruction2Ticks() >= 0) {
					this.setObstruction2Ticks(this.getObstruction2Ticks() - 2);
					if (this.getObstruction2Ticks() <= 0)
						this.setObstruction2Ticks(256);
				}

				if (this.getObstruction3Ticks() >= 0) {
					this.setObstruction3Ticks(this.getObstruction3Ticks() - 1);
					if (this.getObstruction3Ticks() <= 0)
						this.setObstruction3Ticks(256);
				}

				if (this.getObstruction4Ticks() >= 0) {
					this.setObstruction4Ticks(this.getObstruction4Ticks() - 1);
					if (this.getObstruction4Ticks() <= 0)
						this.setObstruction4Ticks(512);
				}

				if (this.isTreasureFish() && this.getTreasureTicks() >= 0) {
					this.setTreasureTicks(this.getTreasureTicks() - 1);
					if (this.getTreasureTicks() <= 0) {
						if (this.getTreasureUnlocked())
							this.setIsTreasureFish(false);
						this.setTreasureTicks(1024);
					}
				}
			}
		}
		super.tick();
	}

	@Override
	public void refreshDimensions() {
		super.refreshDimensions();
		this.recalculateModifiers();
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		return EntityDimensions.scalable(this.getFishSize(), this.getFishSize() * 0.75F);
	}

	public boolean isObstructed() {
		if (256 - this.getObstruction1Ticks() <= this.getStaminaTicks() * 256 / 180 && 256 - this.getObstruction1Ticks() >= this.getStaminaTicks() * 256 / 180 - 16)
			return true;
		else if (256 - this.getObstruction2Ticks() <= this.getStaminaTicks() * 256 / 180 && 256 - this.getObstruction2Ticks() >= this.getStaminaTicks() * 256 / 180 - 16)
			return true;
		else if (256 - this.getObstruction3Ticks() <= this.getStaminaTicks() * 256 / 180 && 256 - this.getObstruction3Ticks() >= this.getStaminaTicks() * 256 / 180 - 16)
			return true;
		else return 512 - this.getObstruction4Ticks() <= this.getStaminaTicks() * 512 / 180 && 512 - this.getObstruction4Ticks() >= this.getStaminaTicks() * 512 / 180 - 16;
	}

	public boolean isObstructedTreasure() {
		return (1024 - this.getTreasureTicks() <= this.getStaminaTicks() * 1024 / 180 + 8 && 1024 - this.getTreasureTicks() >= this.getStaminaTicks() * 1024 / 180 - 16);
	}

	public void leapAtTarget(double targetX, double targetZ) {
		if (!this.isLeaping()) {
			this.setIsLeaping(true);
			this.level().playSound(null, this.blockPosition(), SoundEvents.FISH_SWIM, SoundSource.NEUTRAL, 1F, 2F);
		}
		double distanceX = targetX - this.getX();
		double distanceZ = targetZ - this.getZ();
		float distanceSqrRoot = Mth.sqrt((float) (distanceX * distanceX + distanceZ * distanceZ));
		this.setDeltaMovement(
			distanceX / distanceSqrRoot * 0.1D + this.getDeltaMovement().x() * this.getAttributeValue(Attributes.MOVEMENT_SPEED),
			0.3F,
			distanceZ / distanceSqrRoot * 0.1D + this.getDeltaMovement().z() * this.getAttributeValue(Attributes.MOVEMENT_SPEED));
	}

	public void randomiseObstructionOrder() {
		List<Integer> obstructionList = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			obstructionList.add(i, 64 + i * 64 + this.getRandom().nextInt(32) - this.getRandom().nextInt(32));
		Collections.shuffle(obstructionList);
		this.setObstruction1Ticks(obstructionList.get(0));
		this.setObstruction2Ticks(obstructionList.get(1));
		this.setObstruction3Ticks(obstructionList.get(2));
		this.setObstruction4Ticks(obstructionList.get(3) * 2);
		this.setEscapeTicks((int) (124 + this.getStaminaModifier() * 100));
	}

	public float getFishSize() {
		return this.getEntityData().get(FISH_SIZE);
	}

	public void setFishSize(float size) {
		this.getEntityData().set(FISH_SIZE, size);
		this.refreshDimensions();
		this.setPos(this.getX(), this.getY(), this.getZ());
		this.getAttribute(Attributes.MOVEMENT_SPEED).addOrReplacePermanentModifier(new AttributeModifier(TheBetweenlands.prefix(SPEED_MOD), this.speedMod, AttributeModifier.Operation.ADD_VALUE));
		this.getAttribute(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(new AttributeModifier(TheBetweenlands.prefix(HEALTH_MOD), this.healthMod, AttributeModifier.Operation.ADD_VALUE));
		this.setHealth(this.getMaxHealth());
	}

	public void setAsLootFish(boolean lootFish) {
		if (lootFish) {
			this.setBodyItem(getPartFromLootTable(this.level(), this.position(), this, LootTableRegistry.ANADIA_TREASURE));
			this.setTreasureUnlocked(true);
		} else {
			this.setBodyItem(getPartFromLootTable(this.level(), this.position(), this, LootTableRegistry.ANADIA_BODY));
			this.setTreasureUnlocked(false);
		}
	}

	public void setHeadType(AnadiaParts.AnadiaHeadParts type) {
		this.getEntityData().set(HEAD_TYPE, (byte) type.ordinal());
	}

	public AnadiaParts.AnadiaHeadParts getHeadType() {
		return AnadiaParts.AnadiaHeadParts.get(this.getEntityData().get(HEAD_TYPE));
	}

	public void setBodyType(AnadiaParts.AnadiaBodyParts type) {
		this.getEntityData().set(BODY_TYPE, (byte) type.ordinal());
	}

	public AnadiaParts.AnadiaBodyParts getBodyType() {
		return AnadiaParts.AnadiaBodyParts.get(this.getEntityData().get(BODY_TYPE));
	}

	public void setTailType(AnadiaParts.AnadiaTailParts type) {
		this.getEntityData().set(TAIL_TYPE, (byte) type.ordinal());
	}

	public AnadiaParts.AnadiaTailParts getTailType() {
		return AnadiaParts.AnadiaTailParts.get(this.getEntityData().get(TAIL_TYPE));
	}

	public void setFishColor(AnadiaParts.AnadiaColor color) {
		this.getEntityData().set(FISH_COLOR, (byte) color.ordinal());
	}

	public AnadiaParts.AnadiaColor getFishColor() {
		return AnadiaParts.AnadiaColor.get(this.getEntityData().get(FISH_COLOR));
	}

	void setIsLeaping(boolean leaping) {
		this.getEntityData().set(IS_LEAPING, leaping);
	}

	public boolean isLeaping() {
		return this.getEntityData().get(IS_LEAPING);
	}


	/*
    public int getHungerCooldown() {
        return this.getEntityData().get(HUNGER_COOLDOWN);
    }

    private void setHungerCooldown(int count) {
        this.getEntityData().set(HUNGER_COOLDOWN, count);
    }
	 */

	public void setStaminaTicks(int count) {
		this.getEntityData().set(STAMINA_TICKS, count);
	}

	public int getStaminaTicks() {
		return this.getEntityData().get(STAMINA_TICKS);
	}

	public void setEscapeTicks(int count) {
		this.getEntityData().set(ESCAPE_TICKS, count);
	}

	public int getEscapeTicks() {
		return this.getEntityData().get(ESCAPE_TICKS);
	}

	public void setEscapeDelay(int count) {
		this.getEntityData().set(ESCAPE_DELAY, count);
	}

	public int getEscapeDelay() {
		return this.getEntityData().get(ESCAPE_DELAY);
	}

	public void setObstruction1Ticks(int count) {
		this.getEntityData().set(OBSTRUCTION_TICKS_1, count);
	}

	public int getObstruction1Ticks() {
		return this.getEntityData().get(OBSTRUCTION_TICKS_1);
	}

	public void setObstruction2Ticks(int count) {
		this.getEntityData().set(OBSTRUCTION_TICKS_2, count);
	}

	public int getObstruction2Ticks() {
		return this.getEntityData().get(OBSTRUCTION_TICKS_2);
	}

	public void setObstruction3Ticks(int count) {
		this.getEntityData().set(OBSTRUCTION_TICKS_3, count);
	}

	public int getObstruction3Ticks() {
		return this.getEntityData().get(OBSTRUCTION_TICKS_3);
	}

	public void setObstruction4Ticks(int count) {
		this.getEntityData().set(OBSTRUCTION_TICKS_4, count);
	}

	public int getObstruction4Ticks() {
		return this.getEntityData().get(OBSTRUCTION_TICKS_4);
	}

	public void setHeadItem(ItemStack itemStack) {
		this.getEntityData().set(HEAD_ITEM, itemStack);
	}

	public ItemStack getHeadItem() {
		return this.getEntityData().get(HEAD_ITEM);
	}

	public void setBodyItem(ItemStack itemStack) {
		this.getEntityData().set(BODY_ITEM, itemStack);
	}

	public ItemStack getBodyItem() {
		return this.getEntityData().get(BODY_ITEM);
	}

	public void setTailItem(ItemStack itemStack) {
		this.getEntityData().set(TAIL_ITEM, itemStack);
	}

	public ItemStack getTailItem() {
		return this.getEntityData().get(TAIL_ITEM);
	}

	public void setIsTreasureFish(boolean treasure) {
		this.getEntityData().set(IS_TREASURE_FISH, treasure);
	}

	public boolean isTreasureFish() {
		return this.getEntityData().get(IS_TREASURE_FISH);
	}

	public void setTreasureTicks(int count) {
		this.getEntityData().set(TREASURE_TICKS, count);
	}

	public int getTreasureTicks() {
		return this.getEntityData().get(TREASURE_TICKS);
	}

	public void setTreasureUnlocked(boolean unlocked) {
		this.getEntityData().set(TREASURE_UNLOCKED, unlocked);
	}

	public boolean getTreasureUnlocked() {
		return this.getEntityData().get(TREASURE_UNLOCKED);
	}

	public int getNettableTimer() {
		return this.nettableTimer;
	}

	public void setGlowTimer(int time) {
		this.glowTimer = time;
	}

	public float getGlowTimer() {
		return this.glowTimer;
	}

	public void playTreasureCollectedSound(Player player) {
		this.level().playSound(null, player.blockPosition(), SoundRegistry.ANADIA_TREASURE_COLLECTED.get(), SoundSource.PLAYERS, 0.25F, 1F);
	}

	public void playAnadiaLostSound(Player player) {
		this.level().playSound(null, player.blockPosition(), SoundRegistry.ANADIA_LOST.get(), SoundSource.PLAYERS, 0.25F, 1F);
	}

	public void playAnadiaWonSound(Player player) {
		this.level().playSound(null, player.blockPosition(), SoundRegistry.ANADIA_WON.get(), SoundSource.PLAYERS, 0.25F, 1F);
	}

	public void playAnadiaCrab(Player player) {
		this.level().playSound(null, player.blockPosition(), SoundRegistry.FISHING_CRAB.get(), SoundSource.PLAYERS, 0.25F, 1F);
	}

	public float getSpeedModifier() {
		return this.speedMod;
	}

	public float getHealthModifier() {
		return this.healthMod;
	}

	public float getStrengthModifier() {
		return this.strengthMod;
	}

	public float getStaminaModifier() {
		return this.staminaMod;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putByte("head_type", (byte) this.getHeadType().ordinal());
		tag.putByte("body_type", (byte) this.getBodyType().ordinal());
		tag.putByte("tail_type", (byte) this.getTailType().ordinal());
		tag.putFloat("fish_size", this.getFishSize());
//		nbt.setInteger("hunger", this.getHungerCooldown());
		tag.putByte("fish_color", (byte) this.getFishColor().ordinal());
		tag.putBoolean("treasure_fish", this.isTreasureFish());
		tag.putBoolean("treasure_unlocked", this.getTreasureUnlocked());

		tag.put(HEAD_KEY, this.getHeadItem().saveOptional(this.registryAccess()));
		tag.put(BODY_KEY, this.getBodyItem().saveOptional(this.registryAccess()));
		tag.put(TAIL_KEY, this.getTailItem().saveOptional(this.registryAccess()));

		tag.putFloat(SPEED_MOD, this.speedMod);
		tag.putFloat(HEALTH_MOD, this.healthMod);
		tag.putFloat(STRENGTH_MOD, this.strengthMod);
		tag.putFloat(STAMINA_MOD, this.staminaMod);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		this.setHeadType(AnadiaParts.AnadiaHeadParts.get(tag.getByte("head_type")));
		this.setBodyType(AnadiaParts.AnadiaBodyParts.get(tag.getByte("body_type")));
		this.setTailType(AnadiaParts.AnadiaTailParts.get(tag.getByte("tail_type")));
		this.setFishSize(tag.getFloat("fish_size"));
//		this.setHungerCooldown(nbt.getInteger("hunger"));
		this.setFishColor(AnadiaParts.AnadiaColor.get(tag.getByte("fish_color")));
		this.setIsTreasureFish(tag.getBoolean("treasure_fish"));
		this.setTreasureUnlocked(tag.getBoolean("treasure_unlocked"));

		this.setHeadItem(ItemStack.parseOptional(this.registryAccess(), tag.getCompound(HEAD_KEY)));
		this.setBodyItem(ItemStack.parseOptional(this.registryAccess(), tag.getCompound(BODY_KEY)));
		this.setTailItem(ItemStack.parseOptional(this.registryAccess(), tag.getCompound(TAIL_KEY)));

		this.speedMod = tag.getFloat(SPEED_MOD);
		this.healthMod = tag.getFloat(HEALTH_MOD);
		this.strengthMod = tag.getFloat(STRENGTH_MOD);
		this.staminaMod = tag.getFloat(STAMINA_MOD);
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	public boolean canBeLeashed() {
		return false;
	}

	@Override
	public Component getName() {
		return createName(this.getEntityData().get(HEAD_TYPE), this.getEntityData().get(BODY_TYPE), this.getEntityData().get(TAIL_TYPE));
	}

	public static Component createName(int head, int body, int tail) {
		return Component.translatable("entity.thebetweenlands.anadia.body_" + body).append(" ")
			.append(Component.translatable("entity.thebetweenlands.anadia.tail_" + tail)).append(" ")
			.append(Component.translatable("entity.thebetweenlands.anadia.head_" + head));
	}
}
