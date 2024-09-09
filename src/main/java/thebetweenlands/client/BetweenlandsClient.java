package thebetweenlands.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.event.ClientRegistrationEvents;
import thebetweenlands.client.particle.ParticleFactory;

import javax.annotation.Nullable;

public class BetweenlandsClient {

	@Nullable
	public static ClientLevel getClientLevel() {
		return Minecraft.getInstance().level;
	}

	@Nullable
	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	public static void playLocalSound(SoundInstance instance) {
		Minecraft.getInstance().getSoundManager().play(instance);
	}

	public static RiftVariantReloadListener getRiftVariantLoader() {
		return ClientRegistrationEvents.riftVariantListener;
	}

	/**
	 * DO NOT USE DIRECTLY. Call via TheBetweenlands.createParticle as that will send particles to the client if called on the server
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ParticleOptions> void createParticle(T options, Level level, double x, double y, double z, @Nullable ParticleFactory.ParticleArgs<?> args) {
		ResourceLocation location = BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType());
		Particle particle;
		ParticleProvider<T> provider = (ParticleProvider<T>) Minecraft.getInstance().particleEngine.providers.get(location);
		if (provider instanceof ParticleFactory<?, ?> factory) {
			particle = factory.create((ClientLevel) level, x, y, z, args);
		} else {
			Vec3 motion = args != null ? new Vec3(args.getMotionX(), args.getMotionY(), args.getMotionZ()) : Vec3.ZERO;
			particle = provider.createParticle(options, (ClientLevel) level, x, y, z, motion.x(), motion.y(), motion.z());
		}

		if (particle != null) {
			Minecraft.getInstance().particleEngine.add(particle);
		}
	}
}
