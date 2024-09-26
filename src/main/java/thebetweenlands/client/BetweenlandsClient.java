package thebetweenlands.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.client.event.ClientRegistrationEvents;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.VanillaParticleFactory;

import javax.annotation.Nullable;

@SuppressWarnings("resource")
public class BetweenlandsClient {

	@Nullable
	public static ClientLevel getClientLevel() {
		return Minecraft.getInstance().level;
	}

	@Nullable
	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Nullable
	public static Player getCameraPlayer() {
		// PLEASE stop removing this method, there are actual cases where we need to use it
		return Minecraft.getInstance().getCameraEntity() instanceof Player player ? player : null;
	}

	public static void playLocalSound(SoundInstance instance) {
		Minecraft.getInstance().getSoundManager().play(instance);
	}

	public static RiftVariantReloadListener getRiftVariantLoader() {
		return ClientRegistrationEvents.riftVariantListener;
	}

	public static AspectIconTextureManager getAspectIconManager() {
		return ClientRegistrationEvents.aspectIcons;
	}

	/**
	 * DO NOT USE DIRECTLY. Call via TheBetweenlands.createParticle as that will send particles to the client if called on the server
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ParticleOptions> void createParticle(T options, Level level, double x, double y, double z, @Nullable ParticleFactory.ParticleArgs<?> args) {
		ResourceLocation location = BuiltInRegistries.PARTICLE_TYPE.getKey(options.getType());
		Particle particle;
		ParticleProvider<T> provider = (ParticleProvider<T>) Minecraft.getInstance().particleEngine.providers.get(location);
		if (provider instanceof ParticleFactory<?, T> factory) {
			particle = factory.create(options, (ClientLevel) level, x, y, z, args);
		} else {
			VanillaParticleFactory<T> factory = VanillaParticleFactory.create(provider);
			particle = factory.create(options, (ClientLevel) level, x, y, z, args);
		}

		if (particle != null) {
			Minecraft.getInstance().particleEngine.add(particle);
		}
	}
}
