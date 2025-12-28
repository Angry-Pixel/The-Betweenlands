package thebetweenlands.common.entity.creature;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.audio.GreeblingFallSoundInstance;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class GreeblingVolarpadFloater extends Entity {

	protected static final byte EVENT_START_DISAPPEARING = 40;
	protected static final byte EVENT_DISAPPEAR = 41;
	public int disappearTimer = 0;
	protected float prevFloatingRotationTicks = 0;
	protected float floatingRotationTicks = 0;

	public GreeblingVolarpadFloater(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		this.setInvulnerable(true);
	}

	public GreeblingVolarpadFloater(Level level, double x, double y, double z) {
		super(EntityRegistry.GREEBLING_VOLARPAD_FLOATER.get(), level);
		this.setPos(x, y, z);
		this.setInvulnerable(true);
		this.setDeltaMovement(0.25D - (this.getRandom().nextDouble() * 0.5D), 0.0D, 0.25D - (this.getRandom().nextDouble() * 0.5D));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	public void tick() {
		if (this.firstTick)
			if (this.level().isClientSide())
				BetweenlandsClient.playLocalSound(new GreeblingFallSoundInstance(this));
		super.tick();

		if (this.getDeltaMovement().y() < 0.0D) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D));
		}

		this.prevFloatingRotationTicks = this.floatingRotationTicks;
		this.floatingRotationTicks += 5;
		float wrap = Mth.wrapDegrees(this.floatingRotationTicks) - this.floatingRotationTicks;
		this.floatingRotationTicks += wrap;
		this.prevFloatingRotationTicks += wrap;

		if (this.disappearTimer > 0 && this.disappearTimer < 8)
			this.disappearTimer++;

		if (!this.level().isClientSide()) {
			if (this.disappearTimer == 5)
				this.level().broadcastEntityEvent(this, EVENT_DISAPPEAR);
			if (this.disappearTimer >= 8)
				this.discard();
			List<Player> nearPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(4.5, 5, 4.5), EntitySelector.NO_CREATIVE_OR_SPECTATOR);
			if (this.disappearTimer == 0 && (!nearPlayers.isEmpty() || this.tickCount > 80))
				this.startVanishEvent();

			HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, entity -> false);
			if (hitresult.getType() != HitResult.Type.MISS) {
				this.startVanishEvent();
			}
		}

		this.applyGravity();
		this.move(MoverType.SELF, this.getDeltaMovement());
	}

	public boolean isFloating() {
		return this.getDeltaMovement().y() < 0D;
	}

	public void startVanishEvent() {
		this.disappearTimer++;
		this.playSound(SoundRegistry.GREEBLING_VANISH.get(), 1, 1);
		this.level().broadcastEntityEvent(this, EVENT_START_DISAPPEARING);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == EVENT_START_DISAPPEARING)
			this.disappearTimer = 1;
		else if (id == EVENT_DISAPPEAR)
			this.doLeafEffects();
	}

	private void doLeafEffects() {
		if (this.level().isClientSide()) {
			int leafCount = 40;
			for (int i = 0; i < leafCount; i++) {
				float dx = this.getRandom().nextFloat() - 0.5F;
				float dy = this.getRandom().nextFloat() - 0.1F;
				float dz = this.getRandom().nextFloat() - 0.5F;
				float mag = 0.08F + this.getRandom().nextFloat() * 0.07F;
				this.level().addParticle(ParticleRegistry.LEAF.get(), this.getX(), this.getY(), this.getZ(), dx * mag, dy * mag, dz * mag);
			}
		}
	}

	public float smoothedAngle(float partialTicks) {
		return Mth.lerp(partialTicks, this.prevFloatingRotationTicks, this.floatingRotationTicks);
	}

	@Override
	protected double getDefaultGravity() {
		return 0.09F;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	protected boolean canRide(Entity vehicle) {
		return false;
	}

	@Override
	public boolean canUsePortal(boolean allowPassengers) {
		return false;
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}
}
