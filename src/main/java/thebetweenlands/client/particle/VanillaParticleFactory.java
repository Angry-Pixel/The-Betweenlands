package thebetweenlands.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import org.jetbrains.annotations.Nullable;

public class VanillaParticleFactory<T extends ParticleOptions> extends ParticleFactory<VanillaParticleFactory<T>, T> {
	private final ParticleProvider<T> factory;

	private VanillaParticleFactory(ParticleProvider<T> factory) {
		this.factory = factory;
	}

	/**
	 * Creates a vanilla particle factory that builds particles from an {@link ParticleProvider}.
	 * <p><b>Do note that the first value in the additional arguments must be the particle ID (default is 0)!</b>
	 * @param factory
	 */
	public static <T extends ParticleOptions> VanillaParticleFactory<T> create(ParticleProvider<T> factory) {
		return new VanillaParticleFactory<>(factory);
	}

	@Nullable
	@Override
	protected Particle createParticle(T type, ImmutableParticleArgs args) {
		return this.factory.createParticle(type, args.level, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ);
	}

	@Override
	protected void setBaseArguments(ParticleArgs<?> args) {
		args.withData(0);
	}
}
