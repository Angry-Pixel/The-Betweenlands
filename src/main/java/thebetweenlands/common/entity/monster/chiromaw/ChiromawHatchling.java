package thebetweenlands.common.entity.monster.chiromaw;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.options.EntitySwirlParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.misc.OctineBlock;
import thebetweenlands.common.entity.ProximitySpawner;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.registries.*;

import javax.annotation.Nullable;
import java.util.*;

public class ChiromawHatchling extends PathfinderMob implements OwnableEntity, ProximitySpawner, IEntityWithComplexSpawn {
	private static final byte EVENT_HATCH_PARTICLES = 100;
	private static final byte EVENT_FLOAT_UP_PARTICLES = 101;
	private static final byte EVENT_NEW_SPAWN = 102;

	public static final int MAX_EATING_COOLDOWN = 3000; // set to whatever time between hunger cycles 3000 = 2.5 minutes
	public static final int MIN_EATING_COOLDOWN = 0;
	public static final int MAX_RISE = 40;
	public static final int MIN_RISE = 0;
	public static final int MAX_FOOD_NEEDED = 8; // amount of times needs to be fed
	public float feederRotation, prevFeederRotation, headPitch, prevHeadPitch;
	public int prevHatchAnimation, hatchAnimation, riseCount, prevRise, prevTransformTick, flapArmsCount, blinkCount;
	public boolean flapArms = false;
	private Direction facing;

	protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.OPTIONAL_UUID);
	private static final EntityDataAccessor<Boolean> HATCHED = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_RISING = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_HUNGRY = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> EATING_COOLDOWN = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> FOOD_COUNT = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> IS_CHEWING = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> TRANSFORM = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> TRANSFORM_COUNT = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> HATCH_COUNT = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<ItemStack> FOOD_CRAVED = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> ELECTRIC = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_WILD = SynchedEntityData.defineId(ChiromawHatchling.class, EntityDataSerializers.BOOLEAN);

	public ChiromawHatchling(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.facing = Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(OWNER_UNIQUE_ID, Optional.empty());
		builder.define(HATCHED, false);
		builder.define(IS_RISING, false);
		builder.define(IS_HUNGRY, false);
		builder.define(EATING_COOLDOWN, 0);
		builder.define(FOOD_COUNT, 0);
		builder.define(IS_CHEWING, false);
		builder.define(TRANSFORM, false);
		builder.define(TRANSFORM_COUNT, 0);
		builder.define(HATCH_COUNT, 0);
		builder.define(FOOD_CRAVED, ItemStack.EMPTY);
		builder.define(ELECTRIC, false);
		builder.define(IS_WILD, false);
	}

	@Override
	public void tick() {
		super.tick();
		//DEBUG: Insta hatch
		if (!this.level().isClientSide()) {
			this.setHasHatched(true);
			this.setIsTransforming(true);
			this.setTransformCount(60);
			this.setEatingCooldown(0);
			this.setAmountEaten(MAX_FOOD_NEEDED);
		}

		//Wild Eggs
		if (!this.level().isClientSide() && this.getIsWild()) {
			this.setHasHatched(true);
			this.setEatingCooldown(0);
			this.setAmountEaten(MAX_FOOD_NEEDED);
		}

		if (!this.getHasHatched()) {
			// STAGE 1
			if (!this.level().isClientSide()) {
				if (this.tickCount % 200 == 0) { // 200 = 10 seconds (no need to count this every second)
					if (this.level().getBlockState(this.blockPosition().below()).getBlock() instanceof OctineBlock) //TODO tag
						this.setHatchTick(this.getHatchTick() + 1); // increment whilst on an octine block.
				}
				if (this.getHatchTick() >= 60) { // how many increments before hatching 60 = 10 minutes
					this.level().broadcastEntityEvent(this, EVENT_HATCH_PARTICLES);
					this.setIsHungry(true);
					this.setHasHatched(true);
					this.playSound(SoundRegistry.CHIROMAW_HATCH.get());
				}
			}

			if (this.level().isClientSide()) {
				if (this.getHatchTick() >= 1) { // animation
					this.prevHatchAnimation = this.hatchAnimation;
					this.hatchAnimation++;
				}
				if (this.getElectricBoogaloo())
					this.spawnLightningArcs();
			}
		} else {
			//STAGE 2
			this.prevFeederRotation = this.feederRotation;
			this.prevHeadPitch = this.headPitch;
			this.prevTransformTick = this.getTransformCount();

			this.prevRise = this.getRiseCount();
			if (!this.getRising() && this.getRiseCount() > MIN_RISE) {
				this.setRiseCount(this.getRiseCount() - 4);
			} else if (this.getRising() && this.getRiseCount() < MAX_RISE) {
				this.setRiseCount(this.getRiseCount() + 4);
			}

			this.checkArea(this, Player.class);

			if (this.level().isClientSide()) {
				if (this.getIsTransforming()) {
					if (this.getOwner() != null)
						this.lookAtFeeder(this.getOwner(), 30F);
				}

				if (this.getRising() && this.getRiseCount() >= MAX_RISE) {
					if (!this.getIsHungry())
						if (this.headPitch < 40)
							this.headPitch += 8;
					if (this.getIsHungry())
						if (this.headPitch > 0)
							this.headPitch -= 8;
				}

				if (!this.getRising() && this.getRiseCount() < MAX_RISE)
					this.headPitch = this.getRiseCount();

				if (this.getAmountEaten() >= MAX_FOOD_NEEDED && !this.getIsChewing())
					; // TODO maybe else something to show this is ready to transform/transforming (1.12)

				if (this.getElectricBoogaloo())
					this.spawnLightningArcs();

				if (this.getIsChewing())
					if (this.getTransformCount() < 60)
						this.spawnEatingParticles();

				if (!this.getIsHungry() && this.getRiseCount() >= MAX_RISE && !this.getIsChewing()) {
					if (!this.flapArms && this.flapArmsCount <= 0) {
						if (this.getRandom().nextInt(200) == 0) {
							this.flapArms = true;
							this.flapArmsCount = 30;
						}
					}
					if (this.blinkCount <= 0)
						if (this.getRandom().nextInt(200) == 0)
							this.blinkCount = this.getRandom().nextBoolean() ? 10 : 5;
				}

				if (this.flapArmsCount >= 0)
					this.flapArmsCount--;

				if (this.flapArms && this.flapArmsCount <= 0)
					this.flapArms = false;

				if (this.blinkCount >= 0)
					this.blinkCount--;

			} else {
				if (!this.getIsHungry()) {
					this.setEatingCooldown(getEatingCooldown() - 1);
					if (this.getEatingCooldown() <= MAX_EATING_COOLDOWN && this.getEatingCooldown() > MAX_EATING_COOLDOWN - 60 && !this.getIsChewing())
						this.setIsChewing(true);
					if (this.getEatingCooldown() < MAX_EATING_COOLDOWN - 60 && this.getIsChewing())
						this.setIsChewing(false);
					if (this.getEatingCooldown() <= MIN_EATING_COOLDOWN && this.getAmountEaten() < MAX_FOOD_NEEDED) {
						this.setIsHungry(true);
						this.setFoodCraved(this.chooseNewFoodFromLootTable((ServerLevel) this.level()));
					}
				}

				if (this.getIsTransforming()) {
					if (this.getTransformCount() == 1)
						this.playSound(SoundRegistry.CHIROMAW_HATCHLING_TRANSFORM.get());
					if (this.getTransformCount() <= 60) {
						this.setTransformCount(this.getTransformCount() + 1);
						this.level().broadcastEntityEvent(this, EVENT_FLOAT_UP_PARTICLES);
					}
					if (this.getOwner() != null)
						this.lookAtFeeder(this.getOwner(), 30F);
				}

				if (this.getRiseCount() >= MAX_RISE) {
					if (this.getIsHungry() && this.tickCount % 20 == 0) {
						this.level().playSound(null, this.blockPosition(), SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_LONG.get(), SoundSource.HOSTILE, 1F, 1F + this.getRandom().nextFloat() * 0.125F - this.getRandom().nextFloat() * 0.125F);
					}
					if (this.getAmountEaten() >= MAX_FOOD_NEEDED && this.getEatingCooldown() <= 0) {
						if (!this.getIsTransforming())
							this.setIsTransforming(true);
						if (this.getTransformCount() >= 60) {
							Entity spawn = this.getEntitySpawned();
							if (spawn != null) {
								if (!spawn.isRemoved()) { // just in case
									this.level().broadcastEntityEvent(this, EVENT_NEW_SPAWN);
									this.level().playSound(null, this.blockPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE.get(), SoundSource.HOSTILE, 1F, 1F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.8F);
									this.level().addFreshEntity(spawn);
								}
								this.discard();
							}
						}
					}
				}
			}
		}

		this.setYRot(this.facing.toYRot());
		this.yRotO = this.getYRot();
		this.yHeadRot = this.getYRot();
		this.yHeadRotO = this.getYRot();
		this.yBodyRot = this.getYRot();
		this.yBodyRotO = this.getYRot();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if (this.getIsHungry() && this.getRiseCount() < MAX_RISE)
			return SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_SHORT.get();
		if (!this.getHasHatched())
			return SoundRegistry.CHIROMAW_HATCHLING_INSIDE_EGG.get();
		return SoundRegistry.CHIROMAW_HATCHLING_LIVING.get();
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		int thunderColor = 0xFF3A518D;
		int normalColor = 0xFF6B908D;

		if (id == EVENT_HATCH_PARTICLES) {
			for (int count = 0; count <= 100; ++count) {
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, ItemRegistry.CHIROMAW_HATCHLING.toStack()), this.getX() + (this.getRandom().nextDouble() - 0.5D), this.getY() + 2D + this.getRandom().nextDouble(), this.getZ() + (this.getRandom().nextDouble() - 0.5D), 0.0D, 0.0D, 0.0D);
			}
		}

		if (id == EVENT_FLOAT_UP_PARTICLES) {
			ParticleFactory.ParticleArgs<?> args = ParticleFactory.ParticleArgs.get().withDataBuilder().setData(2, this).buildData()
				.withColor(this.getElectricBoogaloo() ? thunderColor : normalColor)
				.withScale(0.5F + this.getRandom().nextFloat() * 0.5f);
			TheBetweenlands.createParticle(new EntitySwirlParticleOptions(ParticleRegistry.CHIROMAW_TRANSFORM.get(),
					new Vec3(0, -1.3D, 0),
					new Vec3(0, 1.3D, 0),
					Vec3.ZERO, Vec3.ZERO, 4.0D, false),
				this.level(), this.getX(), this.getY() + 2.6D, this.getZ(), args);
		}

		if (id == EVENT_NEW_SPAWN) {
			int leafCount = 40;
			float x = (float) (this.getX());
			float y = (float) (this.getY() + 1.1F);
			float z = (float) (this.getZ());
			while (leafCount-- > 0) {
				float dx = this.getRandom().nextFloat() - 0.5f;
				float dy = this.getRandom().nextFloat() - 0.1F;
				float dz = this.getRandom().nextFloat() - 0.5f;
				float mag = 0.08F + this.getRandom().nextFloat() * 0.07F;
				TheBetweenlands.createParticle(ParticleRegistry.SCRAP.get(), this.level(), x, y, z, ParticleFactory.ParticleArgs.get().withColor(this.getElectricBoogaloo() ? thunderColor : normalColor).withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	private void spawnEatingParticles() {
		double angle = Math.toRadians(this.feederRotation + this.getYRot());
		double offSetX = -Math.sin(angle) * 0.35D;
		double offSetZ = Math.cos(angle) * 0.35D;
		this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getFoodCraved()), this.getX() + (float) offSetX + (this.getRandom().nextDouble() * 0.25D - 0.125D), this.getY() + 0.75F, this.getZ() + (float) offSetZ + (this.getRandom().nextDouble() * 0.25D - 0.125D), 0.0D, 0.0D, 0.0D);
	}

	private void spawnLightningArcs() {
		if (this.getRandom().nextInt(2) == 0) {
			float ox = (this.getRandom().nextFloat() - 0.5f) * 2;
			float oy = (this.getRandom().nextFloat() - 0.5f) * 2;
			float oz = (this.getRandom().nextFloat() - 0.5f) * 2;

			//TODO
//			ParticleLightningArc particle = (ParticleLightningArc) BLParticles.LIGHTNING_ARC.create(this.world, this.getX(), this.getY() + 0.5F + getTransformCount() * 0.02F, this.getZ(),
//				ParticleArgs.get()
//					.withMotion(this.motionX, this.motionY, this.motionZ)
//					.withColor(0.3f, 0.5f, 1.0f, 0.9f)
//					.withData(new Vec3d(this.getX() + ox, this.getY() + oy, this.getZ() + oz)));
//			particle.setLighting(false);
//
//			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);
		}
	}

	@Override
	public <T extends LivingEntity> boolean canEntityBeDetected(LivingEntity spawner, T entity) {
		if (this.getIsWild() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity)) return false;
		if (this.canSneakPast() && entity.isShiftKeyDown())
			return false;
		return !this.checkSight() || spawner.hasLineOfSight(entity);
	}

	@Override
	public <T extends LivingEntity> void performDetectionLogic(T detected) {
		if (this.level().isClientSide()) {
			if (this.getRiseCount() >= MAX_RISE) {
				this.lookAtFeeder(detected, 30F);
			}
		} else {
			if (!this.getRising()) {
				this.setRising(true);
			}
		}
	}

	@Override
	public void performIdlingLogic() {
		if (this.getRiseCount() > MIN_RISE) {
			if (!this.getIsTransforming()) {
				if (!this.level().isClientSide()) {
					if (this.getRising()) {
						this.setRising(false);
					}
				} else {
					this.feederRotation = this.updateFeederRotation(this.feederRotation, 0F, 30F);
				}
			}
		}
	}

	public void lookAtFeeder(Entity entity, float maxYawIncrease) {
		double distanceX = entity.getX() - this.getX();
		double distanceZ = entity.getZ() - this.getZ();
		float angle = (float) (Mth.atan2(distanceZ, distanceX) * (180D / Math.PI)) - 90.0F;
		this.feederRotation = this.updateFeederRotation(this.feederRotation, angle - this.getYRot(), maxYawIncrease);
	}

	private float updateFeederRotation(float angle, float targetAngle, float maxIncrease) {
		float f = Mth.wrapDegrees(targetAngle - angle);
		if (f > maxIncrease)
			f = maxIncrease;
		if (f < -maxIncrease)
			f = -maxIncrease;
		return angle + f;
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!this.getIsTransforming() && this.getHasHatched()) {
			if (!stack.isEmpty() && !this.checkFoodEqual(stack, this.getFoodCraved())) {
				this.playSound(SoundRegistry.CHIROMAW_HATCHLING_NO.get());
				return InteractionResult.FAIL;
			}
			if (!stack.isEmpty() && this.getIsHungry()) {
				if (this.checkFoodEqual(stack, this.getFoodCraved())) {
					stack.consume(this.getFoodCraved().getCount(), player);
					this.setEatingCooldown(MAX_EATING_COOLDOWN);
					this.setAmountEaten(this.getAmountEaten() + 1);
					this.playSound(SoundRegistry.CHIROMAW_HATCHLING_EAT.get());
					this.setIsHungry(false);
					return InteractionResult.sidedSuccess(this.level().isClientSide());
				}
			}
		}
		return super.mobInteract(player, hand);
	}

	private boolean checkFoodEqual(ItemStack stack, ItemStack foodCraved) {
		if (ItemStack.isSameItem(stack, foodCraved)) {
			if (stack.getItem() instanceof MobItem<?> mob) {
				ResourceLocation cravedEntity = mob.getCapturedEntityId(foodCraved);
				ResourceLocation stackEntity = mob.getCapturedEntityId(stack);

				return Objects.equals(cravedEntity, stackEntity);
			}

			return stack.getCount() >= foodCraved.getCount();
		}
		return false;
	}

	protected ResourceKey<LootTable> getFoodCravingLootTable() {
		return LootTableRegistry.CHIROMAW_HATCHLING_FEED_ITEMS;
	}

	public ItemStack chooseNewFoodFromLootTable(ServerLevel level) {
		LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(this.getFoodCravingLootTable());
		if (lootTable != LootTable.EMPTY) {
			LootParams lootparams = new LootParams.Builder(level)
				.withOptionalParameter(LootContextParams.THIS_ENTITY, this)
				.withParameter(LootContextParams.ORIGIN, this.position())
				.create(LootContextParamSets.PIGLIN_BARTER);
			List<ItemStack> loot = lootTable.getRandomItems(lootparams);
			if (!loot.isEmpty()) {
				Collections.shuffle(loot); // mix it up a bit
				return loot.getFirst();
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected Component getTypeName() {
		if (this.getElectricBoogaloo()) {
			return Component.translatable("entity.thebetweenlands.lightning_chiromaw_hatchling");
		}
		return super.getTypeName();
	}

	@Override
	public void thunderHit(ServerLevel level, LightningBolt lightning) {
		this.clearFire();
		if (!this.getHasHatched() && this.getHatchTick() < 1)
			this.setElectricBoogaloo(true);
		else
			super.thunderHit(level, lightning);
	}

	public void setElectricBoogaloo(boolean electric) {
		this.getEntityData().set(ELECTRIC, electric);
	}

	public boolean getElectricBoogaloo() {
		return this.getEntityData().get(ELECTRIC);
	}

	private void setHasHatched(boolean hatched) {
		this.getEntityData().set(HATCHED, hatched);
	}

	public boolean getHasHatched() {
		return this.getEntityData().get(HATCHED);
	}

	private void setRising(boolean rise) {
		this.getEntityData().set(IS_RISING, rise);
	}

	public boolean getRising() {
		return this.getEntityData().get(IS_RISING);
	}

	private void setRiseCount(int riseCountIn) {
		this.riseCount = riseCountIn;
	}

	public int getRiseCount() {
		return this.riseCount;
	}

	private void setAmountEaten(int foodIn) {
		this.getEntityData().set(FOOD_COUNT, foodIn);
	}

	private int getAmountEaten() {
		return this.getEntityData().get(FOOD_COUNT);
	}

	private void setEatingCooldown(int cooldown) {
		this.getEntityData().set(EATING_COOLDOWN, cooldown);
	}

	public int getEatingCooldown() {
		return this.getEntityData().get(EATING_COOLDOWN);
	}

	private void setIsHungry(boolean hungry) {
		this.getEntityData().set(IS_HUNGRY, hungry);
	}

	public boolean getIsHungry() {
		return this.getEntityData().get(IS_HUNGRY);
	}

	private void setIsChewing(boolean chewing) {
		this.getEntityData().set(IS_CHEWING, chewing);
	}

	public boolean getIsChewing() {
		return this.getEntityData().get(IS_CHEWING);
	}

	private void setIsTransforming(boolean transform) {
		this.getEntityData().set(TRANSFORM, transform);
	}

	public boolean getIsTransforming() {
		return this.getEntityData().get(TRANSFORM);
	}

	private void setTransformCount(int transformCountIn) {
		this.getEntityData().set(TRANSFORM_COUNT, transformCountIn);
	}

	public int getTransformCount() {
		return this.getEntityData().get(TRANSFORM_COUNT);
	}

	private void setHatchTick(int hatchCount) {
		this.getEntityData().set(HATCH_COUNT, hatchCount);
	}

	public int getHatchTick() {
		return this.getEntityData().get(HATCH_COUNT);
	}

	public void setFoodCraved(ItemStack itemStack) {
		this.getEntityData().set(FOOD_CRAVED, itemStack);
	}

	public ItemStack getFoodCraved() {
		return this.getEntityData().get(FOOD_CRAVED);
	}

	public void setIsWild(boolean wild) {
		this.getEntityData().set(IS_WILD, wild);
	}

	public boolean getIsWild() {
		return this.getEntityData().get(IS_WILD);
	}

	@Override
	public void kill() {
		this.discard();
	}

	@Override
	public boolean isPersistenceRequired() {
		return true;
	}

	@Override
	protected boolean isImmobile() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public boolean isPickable() {
		return !this.getIsTransforming();
	}

	@Override
	public boolean isInvulnerable() {
		return false;
	}

	@Override
	public float getProximityHorizontal() {
		return 5F;
	}

	@Override
	public float getProximityVertical() {
		return 1F;
	}

	@Override
	public boolean canSneakPast() {
		return true;
	}

	@Override
	public boolean checkSight() {
		return true;
	}

	@Nullable
	public Entity getEntitySpawned() {
		@Nullable
		Mob entity;
		if (this.getIsWild()) {
			entity = new Chiromaw(EntityRegistry.CHIROMAW.get(), this.level());
		} else {
			entity = new TameChiromaw(EntityRegistry.TAME_CHIROMAW.get(), this.level());
			((TameChiromaw) entity).setOwnerUUID(this.getOwnerUUID());
			if (this.hasCustomName())
				entity.setCustomName(this.getCustomName());
			if (this.getElectricBoogaloo())
				((TameChiromaw) entity).setElectricBoogaloo(true);
		}
		if (entity != null) {
			entity.moveTo(this.getX(), this.getY() + 1F, this.getZ(), this.feederRotation + this.getYRot(), 0.0F);
			entity.yHeadRot = entity.getYRot();
			entity.yBodyRot = entity.getYRot();
			entity.setZza(0.1F);
		}
		return entity;
	}

	@Override
	public boolean isSingleUse() {
		return true;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (this.getOwnerUUID() != null) {
			tag.putUUID("owner", this.getOwnerUUID());
		}

		tag.putBoolean("hatched", this.getHasHatched());
		tag.putInt("hatch_tick", this.getHatchTick());
		tag.putBoolean("rising", this.getRising());
		tag.putInt("rising_count", this.getRiseCount());
		tag.putBoolean("hungry", this.getIsHungry());
		tag.putInt("food_eaten", this.getAmountEaten());
		tag.putInt("eating_cooldown", this.getEatingCooldown());
		tag.putBoolean("transforming", this.getIsTransforming());
		tag.putInt("transform_count", this.getTransformCount());
		tag.putInt("facing", this.facing.ordinal());
		tag.putBoolean("electric", this.getElectricBoogaloo());
		tag.putBoolean("wild", this.getIsWild());

		if (!this.getFoodCraved().isEmpty()) {
			tag.put("craved", this.getFoodCraved().save(this.registryAccess()));
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		UUID uuid;
		if (tag.hasUUID("Owner")) {
			uuid = tag.getUUID("Owner");
		} else {
			String s = tag.getString("Owner");
			uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
		}

		if (uuid != null) {
			try {
				this.setOwnerUUID(uuid);
			} catch (Throwable throwable) {
			}
		}

		this.setHasHatched(tag.getBoolean("hatched"));
		this.setHatchTick(tag.getInt("hatch_tick"));
		this.setRising(tag.getBoolean("rising"));
		this.setRiseCount(tag.getInt("risign_count"));
		this.setIsHungry(tag.getBoolean("hungry"));
		this.setAmountEaten(tag.getInt("food_eaten"));
		this.setEatingCooldown(tag.getInt("eating_cooldown"));
		this.setIsTransforming(tag.getBoolean("transforming"));
		this.setTransformCount(tag.getInt("transform_count"));
		this.facing = Direction.values()[tag.getInt("facing")];
		this.setElectricBoogaloo(tag.getBoolean("electric"));
		this.setIsWild(tag.getBoolean("wild"));

		if (tag.contains("craved")) {
			this.setFoodCraved(ItemStack.parse(this.registryAccess(), tag.get("craved")).orElse(ItemStack.EMPTY));
		}
	}

	@Override
	public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.setRot(0.0F, 0.0F);
		this.setOldPosAndRot();
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return this.getEntityData().get(OWNER_UNIQUE_ID).orElse(null);
	}

	public void setOwnerUUID(@Nullable UUID uuid) {
		this.getEntityData().set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buf) {
		buf.writeInt(this.facing.ordinal());
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf buf) {
		this.facing = Direction.values()[buf.readInt()];
	}
}
