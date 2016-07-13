package thebetweenlands.client.particle;

import javax.annotation.Nullable;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticleFactory.ParticleArgs;
import thebetweenlands.client.particle.entity.ParticlePortalBL;

public enum BLParticles {
	PORTAL(new ParticlePortalBL.Factory());

	private BLParticleFactory factory;

	private BLParticles(BLParticleFactory factory) {
		this.factory = factory;
	}

	public Class<? extends Particle> getType() {
		return this.factory.getType();
	}

	public BLParticleFactory getFactory() {
		return this.factory;
	}

	public Particle create(World world, double x, double y, double z, @Nullable ParticleArgs args) {
		return BLParticleManager.INSTANCE.create(this.getType(), world, x, y, z, args);
	}

	public Particle spawn(World world, double x, double y, double z, @Nullable ParticleArgs args) {
		return BLParticleManager.INSTANCE.spawn(this.getType(), world, x, y, z, args);
	}

	public Particle create(World world, double x, double y, double z) {
		return BLParticleManager.INSTANCE.create(this.getType(), world, x, y, z, null);
	}

	public Particle spawn(World world, double x, double y, double z) {
		return BLParticleManager.INSTANCE.spawn(this.getType(), world, x, y, z, null);
	}
}
