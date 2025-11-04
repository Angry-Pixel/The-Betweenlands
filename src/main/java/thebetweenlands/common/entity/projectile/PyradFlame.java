package thebetweenlands.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.ParticleRegistry;

public class PyradFlame extends SmallFireball {

	public PyradFlame(EntityType<? extends SmallFireball> type, Level level) {
		super(type, level);
	}

	public PyradFlame(Level level, LivingEntity owner, Vec3 movement) {
		super(level, owner, movement);
	}

	public void setY(double y) {
		this.setPos(this.getX(), y, this.getZ());
	}

	@Override
	public EntityType<?> getType() {
		return EntityRegistry.PYRAD_FLAME.get();
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide())
			if (this.tickCount >= 1200)
				this.discard();

		if (this.level().isClientSide())
			this.trailParticles(this.level(), this.xo, this.yo, this.zo, this.getRandom());
	}

	public void trailParticles(Level level, double x, double y, double z, RandomSource rand) {
		int motionX = rand.nextInt(2) * 2 - 1;
		int motionZ = rand.nextInt(2) * 2 - 1;
		double velY = (rand.nextFloat() - 0.5D) * 0.125D;
		double velZ = rand.nextFloat() * 0.1F * motionZ;
		double velX = rand.nextFloat() * 0.1F * motionX;
		if (rand.nextInt(4) == 0) {
			level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, velX, velY, velZ);
		}
		TheBetweenlands.createParticle(ParticleRegistry.LEAF.get(), level, x, y + this.getBbHeight() / 2.0F, z, ParticleFactory.ParticleArgs.get().withMotion(velX, velY, velZ).withColor(1F, 0.25F, 0.0F, 1.0F).withData(40));
	}

	@Override
	public ItemStack getItem() {
		return ItemRegistry.PYRAD_FLAME.toStack();
	}
}
