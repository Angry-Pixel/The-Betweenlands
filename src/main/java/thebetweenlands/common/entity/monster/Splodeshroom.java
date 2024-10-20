package thebetweenlands.common.entity.monster;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ProximitySpawnerEntity;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class Splodeshroom extends ProximitySpawnerEntity {

	private static final byte EVENT_EXPLODE_PARTICLES = 100;

	public final int MAX_SWELL = 40;
	public final int MIN_SWELL = 0;
	private static final EntityDataAccessor<Boolean> IS_SWELLING = SynchedEntityData.defineId(Splodeshroom.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SWELL_COUNT = SynchedEntityData.defineId(Splodeshroom.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> HAS_EXPLODED = SynchedEntityData.defineId(Splodeshroom.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> AOE_SIZE_XZ = SynchedEntityData.defineId(Splodeshroom.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> AOE_SIZE_Y = SynchedEntityData.defineId(Splodeshroom.class, EntityDataSerializers.FLOAT);

	public Splodeshroom(EntityType<? extends ProximitySpawnerEntity> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_SWELLING, false);
		builder.define(SWELL_COUNT, 0);
		builder.define(HAS_EXPLODED, false);
		builder.define(AOE_SIZE_XZ, 4F);
		builder.define(AOE_SIZE_Y, 0.5F);
	}

	@Override
	public void tick() {
		// super.tick();
		if (!this.level().isClientSide() && this.level().getGameTime() % 5 == 0) {
			if (!this.getHasExploded())
				this.checkArea();
			if (this.getHasExploded())
				this.checkAreaOfEffect();
		}

		if (!this.level().isClientSide()) {
			if (!this.getSwelling() && this.getSwellCount() > MIN_SWELL)
				this.setSwellCount(this.getSwellCount() - 1);

			if (this.getSwelling() && this.getSwellCount() < MAX_SWELL)
				this.setSwellCount(this.getSwellCount() + 1);

			if (this.getHasExploded()) {
				if (this.getSwellCount() < MAX_SWELL)
					this.setSwellCount(MAX_SWELL);
				if (this.getAOESizeXZ() > 0.5F)
					this.setAOESizeXZ(this.getAOESizeXZ() - 0.01F);
				if (this.getAOESizeXZ() <= 0.5F)
					this.discard();
			}
		}

		if (this.getHasExploded())
			this.setBoundingBoxSize();

		if (this.level().isClientSide())
			if (this.getHasExploded())
				this.spawnCloudParticle();
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		Entity entity = null;
		if (!this.level().isClientSide() && this.level().getDifficulty() != Difficulty.PEACEFUL) {
			List<Player> list = this.level().getEntitiesOfClass(Player.class, this.proximityBox());
			for (Player player : list) {
				entity = player;

				if (player != null) {
					if (!player.isSpectator() && !player.isCreative()) {
						if (this.canSneakPast() && player.isShiftKeyDown())
							return null;
						else if (this.checkSight() && !this.hasLineOfSight(entity))
							return null;
						else {
							if (!this.getSwelling())
								this.setSwelling(true);
						}
						if (!this.dead && this.isSingleUse() && this.getSwellCount() >= MAX_SWELL) {
							this.explode();
						}
					}
				}
			}
			if (entity == null) {
				if (this.getSwelling())
					this.setSwelling(false);
			}
		}
		return entity;
	}

	@Nullable
	protected Entity checkAreaOfEffect() {
		Entity entity = null;
		if (!this.level().isClientSide() && this.level().getDifficulty() != Difficulty.PEACEFUL) {
			List<Player> list = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox());
			for (Player player : list) {
				entity = player;
				if (entity != null) {
					if (!this.isWearingSilkMask(player)) {
						if (!player.isSpectator() && !player.isCreative()) {
							player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
							player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(40, 1));
						}
					}
				}
			}
		}
		return entity;
	}

	public boolean isWearingSilkMask(LivingEntity entity) {
		if (entity instanceof Player player) {
			ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
			return !helmet.isEmpty() && helmet.is(ItemRegistry.SILK_MASK);
		}
		return false;
	}

	private void explode() {
		this.level().broadcastEntityEvent(this, EVENT_EXPLODE_PARTICLES);
		this.playSound(SoundRegistry.SPLODESHROOM_POP.get(), 0.5F, 1.0F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.8F);
		this.setHasExploded(true);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_EXPLODE_PARTICLES) {
			for (int count = 0; count <= 200; ++count) {
				TheBetweenlands.createParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SNOWBALL)), this.level(), this.getX() + (this.getRandom().nextDouble() - 0.5D), this.getY() + 0.25f + this.getRandom().nextDouble(), this.getZ() + (this.getRandom().nextDouble() - 0.5D), ParticleFactory.ParticleArgs.get().withColor(199F / 255, 79F / 255, 123F / 255, 1.0F));
			}
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (AOE_SIZE_XZ.equals(key))
			this.setAOESizeXZ(this.getAOESizeXZ());
		if (AOE_SIZE_Y.equals(key))
			this.setAOESizeY(this.getAOESizeY());
		super.onSyncedDataUpdated(key);
	}

	protected void setBoundingBoxSize() {
		AABB axisalignedbb = new AABB(this.getX() - this.getAOESizeXZ() * 0.5D, this.getY(), this.getZ() - this.getAOESizeXZ() * 0.5D, this.getX() + this.getAOESizeXZ() * 0.5D, this.getY() + this.getAOESizeY(), this.getZ() + this.getAOESizeXZ() * 0.5D);
		this.setBoundingBox(axisalignedbb);
		this.fixupDimensions();
	}

	private void setSwelling(boolean swell) {
		this.getEntityData().set(IS_SWELLING, swell);
		//probably doesn't work
		if (swell)
			this.playSound(SoundRegistry.SPLODESHROOM_WINDUP.get(), 0.5F, 1.0F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.8F);
		else
			this.playSound(SoundRegistry.SPLODESHROOM_WINDDOWN.get(), 0.5F, 1.0F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.8F);
	}

	public boolean getSwelling() {
		return this.getEntityData().get(IS_SWELLING);
	}

	private void setSwellCount(int swellCountIn) {
		this.getEntityData().set(SWELL_COUNT, swellCountIn);
	}

	public int getSwellCount() {
		return this.getEntityData().get(SWELL_COUNT);
	}

	private void setHasExploded(boolean hasExploded) {
		this.getEntityData().set(HAS_EXPLODED, hasExploded);
	}

	public boolean getHasExploded() {
		return this.getEntityData().get(HAS_EXPLODED);
	}

	private void setAOESizeXZ(float aoeSizeXZ) {
		this.getEntityData().set(AOE_SIZE_XZ, aoeSizeXZ);
	}

	public float getAOESizeXZ() {
		return this.getEntityData().get(AOE_SIZE_XZ);
	}

	private void setAOESizeY(float aoeSizeY) {
		this.getEntityData().set(AOE_SIZE_Y, aoeSizeY);
	}

	public float getAOESizeY() {
		return this.getEntityData().get(AOE_SIZE_Y);
	}

	@Override
	protected boolean isImmobile() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return !this.getHasExploded();
	}

	@Override
	public void push(double x, double y, double z) {
		this.setDeltaMovement(0, this.getDeltaMovement().y() + y, 0);
	}

	@Override
	public boolean isInvulnerable() {
		return true;
	}

	@Override
	public void kill() {
		this.discard();
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return true;
		}
		if (source.getEntity() instanceof Player player) {
			if (!this.level().isClientSide()) {
				if (!this.getHasExploded())
					this.explode();
			}
			return true;
		}
		return false;
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		if (this.getHasExploded()) {
			return super.getDefaultDimensions(pose).withEyeHeight(this.getAOESizeY() * 0.8F);
		}
		return super.getDefaultDimensions(pose);
	}

	@Override
	protected float getProximityHorizontal() {
		return 3.0F;
	}

	@Override
	protected float getProximityVertical() {
		return 1.0F;
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		return null;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}

	private void spawnCloudParticle() {
		double x = this.getX() + (this.getRandom().nextFloat() - 0.5F) / 2.0F;
		double y = this.getY() + 0.1D;
		double z = this.getZ() + (this.getRandom().nextFloat() - 0.5F) / 2.0F;
		double mx = (this.getRandom().nextFloat() - 0.5F) / 12.0F;
		double my = (this.getRandom().nextFloat() - 0.5F) / 16.0F * 0.1F;
		double mz = (this.getRandom().nextFloat() - 0.5F) / 12.0F;
		int[] color = {100, 100, 0, 255};

//		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
//			.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
//				.withData(null)
//				.withMotion(mx, my, mz)
//				.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
//				.withScale(8f));
//
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
//
//		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
//			.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
//				.withData(null)
//				.withMotion(mx, my, mz)
//				.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
//				.withScale(4f));
//
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_TEXTURED, particle);
	}
}
