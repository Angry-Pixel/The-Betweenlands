package thebetweenlands.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ProximitySpawnerEntity;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class SludgeWormEggSac extends ProximitySpawnerEntity {
	private static final byte EVENT_GOOP_PARTICLES = 100;

	public SludgeWormEggSac(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide() && this.level().getGameTime() % 5 == 0)
			this.checkArea();
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
	public void setDeltaMovement(double x, double y, double z) {
		this.setDeltaMovement(new Vec3(x, y, z).multiply(0.0D, 1.0D, 0.0D));
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
	public boolean hurt(DamageSource source, float amount) {
		if (source.isCreativePlayer()) {
			this.discard();
		}
		return false;
	}

	@Override
	protected void performPostSpawnaction(Entity targetEntity, @Nullable Entity entitySpawned, boolean firstSpawn) {
		if (firstSpawn && !this.level().isClientSide()) {
			this.level().broadcastEntityEvent(this, EVENT_GOOP_PARTICLES);
		}
		this.playSound(this.getDeathSound(), 0.5F, 1.0F);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_GOOP_PARTICLES) {
			for (int count = 0; count <= 100; ++count) {
				TheBetweenlands.createParticle(ParticleTypes.ITEM_SNOWBALL, this.level(),
					this.getX() + (this.getRandom().nextDouble() - 0.5D),
					this.getY() + (this.getRandom().nextDouble() * 0.25D),
					this.getZ() + (this.getRandom().nextDouble() - 0.5D),
					ParticleFactory.ParticleArgs.get().withColor(0xFFE9DBBA));
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.WORM_EGG_SAC_LIVING.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.WORM_EGG_SAC_SQUISH.get();
	}

	@Override
	protected float getProximityHorizontal() {
		return 3F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
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
		return new TinySludgeWorm(EntityRegistry.TINY_SLUDGE_WORM.get(), this.level());
	}

	@Override
	protected int getEntitySpawnCount() {
		return 4;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}
}
