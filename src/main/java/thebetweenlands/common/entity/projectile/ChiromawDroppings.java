package thebetweenlands.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.RotSmellData;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ChiromawDroppings extends ThrowableProjectile {

	private static final EntityDataAccessor<Boolean> HAS_EXPLODED = SynchedEntityData.defineId(ChiromawDroppings.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> AOE_SIZE_XZ = SynchedEntityData.defineId(ChiromawDroppings.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> AOE_SIZE_Y = SynchedEntityData.defineId(ChiromawDroppings.class, EntityDataSerializers.FLOAT);

	private float prevRotationTicks;
	private float rotationTicks;
	@Nullable
	protected LivingEntity thrower;

	public ChiromawDroppings(EntityType<? extends ThrowableProjectile> type, Level level) {
		super(type, level);
		this.setInvulnerable(true);
	}

	public ChiromawDroppings(Level level, @Nullable LivingEntity thrower, double x, double y, double z) {
		super(EntityRegistry.CHIROMAW_DROPPINGS.get(), level);
		this.setPos(x, y, z);
		this.setInvulnerable(true);
		this.thrower = thrower;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(HAS_EXPLODED, false);
		builder.define(AOE_SIZE_XZ, 4F);
		builder.define(AOE_SIZE_Y, 0.5F);
	}

	private void spawnDroppingsParticles() {
		double d0 = this.getX() - 0.075F;
		double d1 = this.getY() + this.getDeltaMovement().y();
		double d2 = this.getZ() - 0.075F;
		double d3 = this.getX() + 0.075F;
		double d4 = this.getZ() + 0.075F;
		double d5 = this.getX();
		double d6 = this.getY() + this.getDeltaMovement().y() + 0.25F;
		double d7 = this.getZ();

		TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d0, d1, d4, ParticleFactory.ParticleArgs.get().withMotion(0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F), this.getDeltaMovement().y() + 0.1F * (this.getRandom().nextFloat() - 0.5F), 0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F)).withColor(0xFF694628));
		TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d3, d1, d2, ParticleFactory.ParticleArgs.get().withMotion(0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F), this.getDeltaMovement().y() + 0.1F * (this.getRandom().nextFloat() - 0.5F), 0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F)).withColor(0xFF694628));
		TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d3, d1, d4, ParticleFactory.ParticleArgs.get().withMotion(0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F), this.getDeltaMovement().y() + 0.1F * (this.getRandom().nextFloat() - 0.5F), 0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F)).withColor(0xFF694628));
		TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d0, d1, d2, ParticleFactory.ParticleArgs.get().withMotion(0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F), this.getDeltaMovement().y() + 0.1F * (this.getRandom().nextFloat() - 0.5F), 0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F)).withColor(0xFF694628));
		TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d5, d6, d7, ParticleFactory.ParticleArgs.get().withMotion(0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F), this.getDeltaMovement().y() + 0.1F * (this.getRandom().nextFloat() - 0.5F), 0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F)).withColor(0xFF694628));
		TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d0, d1, d2, ParticleFactory.ParticleArgs.get().withMotion(0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F), this.getDeltaMovement().y() + 0.1F * (this.getRandom().nextFloat() - 0.5F), 0.08F * (d1) * (this.getRandom().nextFloat() - 0.5F)).withColor(0xFF694628));
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide()) {
			if (this.hasExploded()) {
				if (this.level().getGameTime() % 5 == 0)
					this.checkAreaOfEffect();
				if (this.getAOESizeXZ() > 0.5F)
					this.setAOESizeXZ(this.getAOESizeXZ() - 0.01F);
				if (this.getAOESizeXZ() <= 0.5F)
					this.discard();
			}
		} else {
			this.prevRotationTicks = this.rotationTicks;
			this.rotationTicks += 15;
			float wrap = Mth.wrapDegrees(this.rotationTicks) - this.rotationTicks;
			this.rotationTicks +=wrap;
			this.prevRotationTicks += wrap;

			if (!this.hasExploded() && this.tickCount % 4 == 0) {
				this.spawnDroppingsParticles();
			}

			if (this.hasExploded())
				this.spawnCloudParticle();
		}

		if (this.hasExploded())
			this.setBoundingBoxSize();

		super.tick();
	}

	protected void checkAreaOfEffect() {
		if (!this.level().isClientSide() && this.level().getDifficulty() != Difficulty.PEACEFUL) {
			List<Player> list = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox(), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
			for (Player player : list) {
				player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
				player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(40, 1));
				RotSmellData smellData = player.getData(AttachmentRegistry.ROT_SMELL);
				if (!smellData.isSmellingBad(player))
					smellData.setSmellingBad(player, Math.max(smellData.getRemainingSmellyTicks(player), 24000));
			}
		}
	}

	@Override
	protected double getDefaultGravity() {
		if (!this.hasExploded())
			return 0.03F;
		return 0.0F;
	}

	public float getAnimationRotation(float partialTicks) {
		return Mth.lerp(partialTicks, this.prevRotationTicks, this.rotationTicks);
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	protected boolean canRide(Entity entity) {
		return false;
	}

	@Override
	protected void onHit(HitResult result) {
		if (!this.hasExploded() && result.getType() != HitResult.Type.MISS) {
			if (!this.level().isClientSide()) {
				this.setExploded();
				this.playSound(SoundRegistry.CHIROMAW_MATRIARCH_SPLAT.get(), 1F, 1F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.8F);
			}
		}
	}

	@Override
	protected boolean canHitEntity(Entity target) {
		return super.canHitEntity(target) && !(target instanceof ChiromawDroppings);
	}

	protected void setBoundingBoxSize() {
		AABB axisalignedbb = new AABB(this.getX() - this.getAOESizeXZ() * 0.5D, this.getY(), this.getZ() - this.getAOESizeXZ() * 0.5D, this.getX() + this.getAOESizeXZ() * 0.5D, this.getY() + this.getAOESizeY(), this.getZ() + this.getAOESizeXZ() * 0.5D);
		this.setBoundingBox(axisalignedbb);
		this.fixupDimensions();
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (AOE_SIZE_XZ.equals(key))
			this.setAOESizeXZ(this.getAOESizeXZ());
		if (AOE_SIZE_Y.equals(key))
			this.setAOESizeY(this.getAOESizeY());
		super.onSyncedDataUpdated(key);
	}

	private void setExploded() {
		this.getEntityData().set(HAS_EXPLODED, true);
	}

	public boolean hasExploded() {
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

	private void spawnCloudParticle() {
		double x = this.getX() + (this.getRandom().nextFloat() - 0.5F) / 2.0F;
		double y = this.getY() + 0.1D;
		double z = this.getZ() + (this.getRandom().nextFloat() - 0.5F) / 2.0F;
		double mx = (this.getRandom().nextFloat() - 0.5F) / 12.0F;
		double my = (this.getRandom().nextFloat() - 0.5F) / 16.0F * 0.1F;
		double mz = (this.getRandom().nextFloat() - 0.5F) / 12.0F;

//		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
//			.create(this.level(), x, y, z, ParticleFactory.ParticleArgs.get()
//				.withData(null)
//				.withMotion(mx, my, mz)
//				.withColor(0xFF646400)
//				.withScale(8.0F));
//
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
//
//		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
//			.create(this.level(), x, y, z, ParticleFactory.ParticleArgs.get()
//				.withData(null)
//				.withMotion(mx, my, mz)
//				.withColor(0xFF646400)
//				.withScale(4.0F));
//
//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_TEXTURED, particle);
	}
}
