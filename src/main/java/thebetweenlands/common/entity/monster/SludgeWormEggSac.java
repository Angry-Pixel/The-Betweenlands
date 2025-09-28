package thebetweenlands.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class SludgeWormEggSac extends BasicProximitySpawner {
	private static final byte EVENT_GOOP_PARTICLES = 100;

	public SludgeWormEggSac(EntityType<? extends BasicProximitySpawner> type, Level level) {
		super(type, level);
	}

	@Override
	public void setDeltaMovement(double x, double y, double z) {
		this.setDeltaMovement(new Vec3(x, y, z).multiply(0.0D, 1.0D, 0.0D));
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
	public float getProximityHorizontal() {
		return 3F;
	}

	@Override
	public float getProximityVertical() {
		return 1F;
	}

	@Override
	public <T extends LivingEntity> void performDetectionLogic(T detected) {
		for (int count = 0; count < 4; count++) {
			TinySludgeWorm spawnEntity = new TinySludgeWorm(EntityRegistry.TINY_SLUDGE_WORM.get(), this.level());
			spawnEntity.setPos(this.blockPosition().getBottomCenter());
			this.level().addFreshEntity(spawnEntity);
			this.playSound(this.getDeathSound(), 0.5F, 1.0F);
		}
		this.level().broadcastEntityEvent(this, EVENT_GOOP_PARTICLES);
		this.discard();
	}
}
