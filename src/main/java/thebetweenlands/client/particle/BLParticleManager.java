package thebetweenlands.client.particle;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticleFactory.ImmutableParticleArgs;
import thebetweenlands.client.particle.BLParticleFactory.ParticleArgs;

public enum BLParticleManager {
	INSTANCE;

	private final Map<Class<? extends Particle>, BLParticleFactory<? extends Particle>> particleTypes = Maps.<Class<? extends Particle>, BLParticleFactory<? extends Particle>>newHashMap();

	/**
	 * Registers all particles from {@link BLParticles#values()}
	 */
	public void registerParticles() {
		BLParticles[] particles = BLParticles.values();
		for(BLParticles particle : particles) {
			this.registerParticle(particle.getType(), particle.getFactory());
		}
	}

	/**
	 * Registers a particle
	 * @param type
	 * @param particleFactory
	 */
	public void registerParticle(Class<? extends Particle> type, BLParticleFactory<? extends Particle> particleFactory) {
		this.particleTypes.put(type, particleFactory);
	}

	/**
	 * Creates an instance of a particle
	 * @param type
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param args
	 * @return
	 */
	public <T extends Particle> Particle create(Class<T> type, World world, double x, double y, double z, @Nullable ParticleArgs args) {
		if(args == null)
			args = ParticleArgs.get();

		BLParticleFactory<T> particleFactory = (BLParticleFactory<T>)this.particleTypes.get(type);

		if (particleFactory != null) {
			Particle particle = particleFactory.getParticle(new ImmutableParticleArgs(world, x, y, z, particleFactory.getArguments(args)));
			return particle;
		}

		return null;
	}

	/**
	 * Spawns a particle
	 * @param type
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param args
	 * @return
	 */
	public <T extends Particle> Particle spawn(Class<T> type, World world, double x, double y, double z, @Nullable ParticleArgs args) {
		Particle particle = this.create(type, world, x, y, z, args);
		if(particle != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		return particle;
	}
}
