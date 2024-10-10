package thebetweenlands.common.entity.creature;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.WaterStrollGoal;
import thebetweenlands.common.entity.movement.AboveWaterPathNavigation;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class GreeblingCoracle extends PathfinderMob implements BLEntity {

	protected static final byte EVENT_DISAPPEAR = 41;
	protected static final byte EVENT_SPOUT = 42;

	private static final EntityDataAccessor<Integer> SINKING_TICKS = SynchedEntityData.defineId(GreeblingCoracle.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> LOOT_CLICKS = SynchedEntityData.defineId(GreeblingCoracle.class, EntityDataSerializers.INT);

	private boolean hasSetAIForEmptyBoat = false;
	private boolean looted = false;
	private NonNullList<ItemStack> loot = NonNullList.create();
	private int shutUpFFSTime;
	public int rowTicks;
	public float rowSpeed = 0.5F;

	public GreeblingCoracle(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		this.setPathfindingMalus(PathType.WALKABLE, -1.0F);
		this.setPathfindingMalus(PathType.BLOCKED, -1.0F);
		this.setPathfindingMalus(PathType.LAVA, -1.0F);
		this.setPathfindingMalus(PathType.WATER_BORDER, -1.0F);
		this.setPathfindingMalus(PathType.WATER, 0.0F);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SINKING_TICKS, 0);
		builder.define(LOOT_CLICKS, 0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Player.class, 16.0F, 4.0D, 8.0D));
		this.goalSelector.addGoal(1, new WaterStrollGoal(this, 0.5D, 30));
		this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return GreeblingCoracle.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new AboveWaterPathNavigation(this, level);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		return this.level().getBlockState(pos).liquid() ? 10.0F + this.level().getMaxLocalRawBrightness(pos) - 0.5F : super.getWalkTargetValue(pos);
	}

	@Override
	public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
		int y = Mth.floor(this.getBoundingBox().minY);
		if(y <= TheBetweenlands.LAYER_HEIGHT + 1 && y > TheBetweenlands.CAVE_START)
			return this.level().noCollision(this.getBoundingBox()) && this.level().getEntityCollisions(this, this.getBoundingBox()).isEmpty() && this.level().containsAnyLiquid(this.getBoundingBox().move(0D, -0.5D, 0D));
		return false;
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide() && this.getSinkingTicks() <= 0) {
			this.rowTicks++;

			if (!this.isSilent() && this.getX() != this.xOld && this.getZ() != this.zOld) {
				float rowAngle1 = Mth.cos(this.rowTicks * rowSpeed);
				float rowAngle2 = Mth.cos((this.rowTicks + 1) * rowSpeed);
				if(rowAngle1 <= 0.8f && rowAngle2 > 0.8f) {
					this.playSound(SoundEvents.GENERIC_SWIM, 0.2F, 0.8F + 0.4F * this.getRandom().nextFloat());
				}
			}
		}

		if(this.shutUpFFSTime > 0) {
			this.shutUpFFSTime--;
			this.ambientSoundTime = -this.getAmbientSoundInterval();
		}

		if (this.level().containsAnyLiquid(this.getBoundingBox()) && this.getSinkingTicks() <= 200) {
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.06D, 0.0D));
		}

		if (this.getSinkingTicks() > 0) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(0.975D, 1.0D, 0.975D));
		}

		if (this.isGreeblingAboveWater() && this.getSinkingTicks() <= 200) {
			if (this.getDeltaMovement().y() < 0.0D) {
				this.setDeltaMovement(this.getDeltaMovement().x(), 0.0D, this.getDeltaMovement().z());
			}
			this.fallDistance = 0.0F;
		} else {
			this.setDeltaMovement(this.getDeltaMovement().x(), -0.0075D, this.getDeltaMovement().z());
		}

		this.walkAnimation.position += 0.5F;
		if (this.getX() != this.xOld && this.getZ() != this.zOld)
			this.walkAnimation.setSpeed(this.walkAnimation.speed() + 0.5F);

		if(!this.level().isClientSide()) {
			if (this.getSinkingTicks() == 200 && this.isGreeblingAboveWater())
				this.level().playSound(null, this.blockPosition(), SoundRegistry.GREEBLING_CORACLE_SINK.get(), SoundSource.NEUTRAL, 1F, 1F);

			if (this.getSinkingTicks() > 0 && this.getSinkingTicks() < 400)
				this.setSinkingTicks(this.getSinkingTicks() + 1);

			if (this.getSinkingTicks() == 5)
				this.level().broadcastEntityEvent(this, EVENT_DISAPPEAR);

			if (this.getSinkingTicks() >= 200 && this.getSinkingTicks() <= 400 && this.isGreeblingAboveWater())
				this.level().broadcastEntityEvent(this, EVENT_SPOUT);

			if (this.getSinkingTicks() > 0 && !this.hasSetAIForEmptyBoat) {
				this.goalSelector.removeAllGoals(goal -> true);
				if(this.getNavigation().getPath() != null) {
					this.getNavigation().stop();
				}
				this.hasSetAIForEmptyBoat = true;
			}

			if (this.getSinkingTicks() >= 400)
				this.discard();

			List<Player> nearPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(2.5, 2.5, 2.5), e -> !e.isCreative() && !e.isInvisible());
			if (this.getSinkingTicks() == 0 && !nearPlayers.isEmpty()) {
				this.setSinkingTicks(this.getSinkingTicks() + 1);
				this.level().playSound(null, this.blockPosition(), SoundRegistry.GREEBLING_VANISH.get(), SoundSource.NEUTRAL, 1F, this.getVoicePitch());
			}
		}
	}

	public boolean isGreeblingAboveWater() {
		AABB floatingBox = new AABB(this.getBoundingBox().minX + 0.25D, this.getBoundingBox().minY + 0.12F, this.getBoundingBox().minZ + 0.25D, this.getBoundingBox().maxX - 0.25D, this.getBoundingBox().minY + 0.0625D, this.getBoundingBox().maxZ - 0.25D);
		return this.level().containsAnyLiquid(floatingBox);
	}

	public void setSinkingTicks(int count) {
		this.getEntityData().set(SINKING_TICKS, count);
	}

	public int getSinkingTicks() {
		return this.getEntityData().get(SINKING_TICKS);
	}

	public void setLootClicks(int count) {
		this.getEntityData().set(LOOT_CLICKS, count);
	}

	public int getLootClicks() {
		return this.getEntityData().get(LOOT_CLICKS);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("sinking_ticks", this.getSinkingTicks());
		tag.putBoolean("looted", this.looted);
		tag.putInt("loot_count", this.loot.size());
		tag.putInt("loot_clicks", this.getLootClicks());
		tag.put("loot", ContainerHelper.saveAllItems(new CompoundTag(), this.loot, this.level().registryAccess()));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setSinkingTicks(tag.getInt("sinking_ticks"));
		this.looted = tag.getBoolean("looted");
		this.loot = NonNullList.withSize(tag.getInt("loot_count"), ItemStack.EMPTY);
		this.setLootClicks(tag.getInt("loot_clicks"));
		ContainerHelper.loadAllItems(tag.getCompound("loot"), this.loot, this.level().registryAccess());
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if(id == EVENT_DISAPPEAR)
			this.doLeafEffects();
		if(id == EVENT_SPOUT)
			this.doSpoutEffects();
	}

	private void doSpoutEffects() {
		if(this.level().isClientSide()) {
			int count = this.getSinkingTicks() <= 240 ? 40 : 10;
			double x = this.getX();
			double y = this.getY() + 0.25D;
			double z = this.getZ();

			while (count-- > 0) {
				float dx = this.level().getRandom().nextFloat() * 0.25F - 0.1255f;
				float dy = this.level().getRandom().nextFloat() * 0.25F - 0.1255f;
				float dz = this.level().getRandom().nextFloat() * 0.25F - 0.1255f;
				float mag = 0.08F + this.level().getRandom().nextFloat() * 0.07F;

				int waterColor = BiomeColors.getAverageWaterColor(this.level(), this.blockPosition());

				float r = (waterColor >> 16 & 255) / 255.0f;
				float g = (waterColor >> 8 & 255) / 255.0f;
				float b = (waterColor & 255) / 255.0f;

				if(this.getSinkingTicks() <= 240) {
					TheBetweenlands.createParticle(ParticleRegistry.RAIN.get(), this.level(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag).withColor(r, g, b + 0.075f, 1.0f));
				} else if(this.getSinkingTicks() > 240 && this.getSinkingTicks() <= 400 && this.getSinkingTicks()%5 == 0) {
					TheBetweenlands.createParticle(ParticleRegistry.WATER_BUBBLE.get(), this.level(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag).withColor(r + 0.05f, g + 0.15f, b + 0.05f, 1.0f));
				}
			}
		}
	}

	private void doLeafEffects() {
		if(this.level().isClientSide()) {
			int leafCount = 40;
			double x = this.getX();
			double y = this.getY() + 0.75D;
			double z = this.getZ();
			while (leafCount-- > 0) {
				float dx = this.level().getRandom().nextFloat() - 0.5F;
				float dy = this.level().getRandom().nextFloat() - 0.1F;
				float dz = this.level().getRandom().nextFloat() - 0.5F;
				float mag = 0.08F + this.level().getRandom().nextFloat() * 0.07F;
				TheBetweenlands.createParticle(ParticleRegistry.WEEDWOOD_LEAF.get(), this.level(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	@Override
	protected boolean shouldDropLoot() {
		return false;
	}

	@Override
	public void kill() {
		this.discard();
	}

	@Override
	protected float getSoundVolume() {
		return 0.75F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if(this.getSinkingTicks() <= 0) {
			if(this.getRandom().nextInt(4) == 0 && this.shutUpFFSTime <= 0) {
				this.shutUpFFSTime = 120;
				return SoundRegistry.GREEBLING_HUM.get();
			}
			else
				return SoundRegistry.GREEBLING_GIGGLE.get();
		}
		return null;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 1;
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		if (source.getEntity() instanceof LivingEntity)
			if (this.getSinkingTicks() == 0)
				if (!this.level().isClientSide()) {
					this.setSinkingTicks(getSinkingTicks() + 1);
					this.level().playSound(null, this.blockPosition(), SoundRegistry.GREEBLING_VANISH.get(), SoundSource.NEUTRAL, 1F, this.getVoicePitch());
				}
		return false;
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (this.getSinkingTicks() > 0 && this.getSinkingTicks() < 200 && hand == InteractionHand.MAIN_HAND) {
			if (!this.level().isClientSide()) {
				this.dropLoot(player);
				this.setLootClicks(getLootClicks() +1);
				if(this.getLootClicks() >= this.loot.size())
					this.setSinkingTicks(200);
				SoundType soundType = SoundType.WOOD;
				this.level().playSound(null, this.blockPosition(), soundType.getHitSound(), SoundSource.NEUTRAL, (soundType.getVolume() + 1.0F) / 4.0F, soundType.getPitch() * 0.5F);
			}
			return InteractionResult.sidedSuccess(this.level().isClientSide());
		}
		return super.mobInteract(player, hand);
	}

	public void dropLoot(Player player) {
		if(!this.level().isClientSide()) {
			if(!this.looted) {
				this.looted = true;

				LootTable loot = this.level().getServer().reloadableRegistries().getLootTable(this.getLootTable());
				LootParams params = new LootParams.Builder((ServerLevel) this.level())
					.withParameter(LootContextParams.THIS_ENTITY, this)
					.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
					.withParameter(LootContextParams.ORIGIN, this.position())
					.withParameter(LootContextParams.DAMAGE_SOURCE, this.damageSources().generic())
					.withLuck(player.getLuck())
					.create(LootContextParamSets.ENTITY);

				this.loot = NonNullList.copyOf(loot.getRandomItems(params));
			}

			ItemStack stack = this.loot.get(this.getLootClicks());
			if (!stack.isEmpty()) {
				this.spawnAtLocation(stack, 0.0F);
			}
		}
	}
}
