package thebetweenlands.client.render.particle;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;

public final class VanillaParticleFactory<T extends Particle> extends ParticleFactory<VanillaParticleFactory<T>, T> {
	private final IParticleFactory factory;

	private VanillaParticleFactory(Class<T> type, IParticleFactory factory) {
		super(type);
		this.factory = factory;
	}

	/**
	 * Creates a vanilla particle factory that builds particles from an {@link IParticleFactory}.
	 * <p><b>Do note that the first value in the additional arguments must be the particle ID (default is 0)!</b>
	 * @param type
	 * @param factory
	 */
	public static <T extends Particle> VanillaParticleFactory<T> create(Class<T> type, IParticleFactory factory) {
		return new VanillaParticleFactory<T>(type, factory);
	}

	@Override
	protected T createParticle(ImmutableParticleArgs args) {
		Object[] data = args.data.getAll();
		if(data.length > 1) {
			int[] additionalArgs = ArrayUtils.toPrimitive(Arrays.copyOfRange(data, 1, data.length - 1, Integer[].class));
			return (T) this.factory.createParticle((int)data[0], args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, additionalArgs);
		} else {
			return (T) this.factory.createParticle((int)data[0], args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ);
		}
	}

	@Override
	protected void setBaseArguments(ParticleArgs args) {
		args.withData(0);
	}
}
