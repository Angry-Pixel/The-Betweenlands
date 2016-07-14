package thebetweenlands.client.particle;

import javax.annotation.Nullable;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import thebetweenlands.client.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.particle.entity.ParticlePortalBL;

public enum BLParticles {

	PORTAL(new ParticlePortalBL.Factory());





	private ParticleFactory factory;

	private BLParticles(ParticleFactory factory) {
		this.factory = factory;
	}

	public Class<? extends Particle> getType() {
		return this.factory.getType();
	}

	public ParticleFactory getFactory() {
		return this.factory;
	}

	/**
	 * Creates a new instance of this particle
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param args
	 * @return
	 */
	public Particle create(World world, double x, double y, double z, @Nullable ParticleArgs args) {
		return this.getFactory().create(world, x, y, z, args);
	}

	/**
	 * Spawns this particle
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param args
	 * @return
	 */
	public Particle spawn(World world, double x, double y, double z, @Nullable ParticleArgs args) {
		return this.getFactory().spawn(world, x, y, z, args);
	}

	/**
	 * Creates a new instance of this particle
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Particle create(World world, double x, double y, double z) {
		return this.getFactory().create(world, x, y, z, null);
	}

	/**
	 * Spawns this particle
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Particle spawn(World world, double x, double y, double z) {
		return this.getFactory().spawn(world, x, y, z, null);
	}
}
