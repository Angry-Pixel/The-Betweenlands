package thebetweenlands.common.entity.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;

public class SludgeWallJet extends ThrowableProjectile {

	private boolean playedSound = false;
	private static final byte EVENT_TRAIL_PARTICLES = 105;
	private static final byte EVENT_HIT_PARTICLES = 106;
	private float damage = 2.0F;

	public SludgeWallJet(EntityType<? extends ThrowableProjectile> type, Level level) {
		super(type, level);
	}

	public SludgeWallJet(LivingEntity shooter, Level level) {
		super(EntityRegistry.SLUDGE_WALL_JET.get(), shooter, level);
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putFloat("damage", this.damage);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.damage = compound.getFloat("damage");
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide())
			this.level().broadcastEntityEvent(this, EVENT_TRAIL_PARTICLES);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == EVENT_TRAIL_PARTICLES) {
			for (int i = 0; i < 8; ++i) {
				int motionX = this.getRandom().nextInt(2) * 2 - 1;
				int motionZ = this.getRandom().nextInt(2) * 2 - 1;
				double velY = (this.getRandom().nextFloat() - 0.5D) * 0.125D;
				double velZ = this.getRandom().nextFloat() * 0.1F * motionZ;
				double velX = this.getRandom().nextFloat() * 0.1F * motionX;
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, BlockRegistry.SLUDGY_MUD_BRICKS_3.toStack()), this.getX(), this.getY(), this.getZ(), velX, velY, velZ);
			}
		}

		if (id == EVENT_HIT_PARTICLES) {
			for (int i = 0; i < 16; ++i) {
				this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, BlockRegistry.SLUDGY_MUD_BRICKS_3.toStack()), this.getX() + (this.getRandom().nextDouble() - 0.5D), this.getY() + 2D + this.getRandom().nextDouble(), this.getZ() + (this.getRandom().nextDouble() - 0.5D), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected SoundEvent getSwimSplashSound() {
		return SoundEvents.FISHING_BOBBER_SPLASH;
	}

	@Override
	protected void onHit(HitResult result) {
		if (!this.playedSound) {
			this.playSound(this.getSwimSplashSound(), 0.25F, 2.0F);
			this.playedSound = true;
		}
		super.onHit(result);
		this.discard();
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		if (result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), this.damage)) {
			this.level().broadcastEntityEvent(this, EVENT_HIT_PARTICLES);
		}
	}

	@Override
	public boolean isPickable() {
		return false;
	}
}
