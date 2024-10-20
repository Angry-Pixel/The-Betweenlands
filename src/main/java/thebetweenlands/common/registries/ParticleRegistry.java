package thebetweenlands.common.registries;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.client.particle.options.DripParticleOptions;
import thebetweenlands.client.particle.options.EntitySwirlParticleOptions;
import thebetweenlands.common.TheBetweenlands;

import java.util.function.Function;

public class ParticleRegistry {

	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SULFUR_GENERIC = PARTICLES.register("sulfur_generic_effect", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PORTAL_EFFECT = PARTICLES.register("portal_effect", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ANIMATOR = PARTICLES.register("animator", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLY = PARTICLES.register("fly", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MOSQUITO = PARTICLES.register("mosquito", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MOTH = PARTICLES.register("moth", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SWARM = PARTICLES.register("swarm", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SILK_MOTH = PARTICLES.register("silk_moth", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPIRIT_BUTTERFLY = PARTICLES.register("spirit_butterfly", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WATER_BUG = PARTICLES.register("water_bug", () -> new SimpleParticleType(false));

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FANCY_BUBBLE = PARTICLES.register("fancy_bubble", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, ParticleType<DripParticleOptions>> FANCY_DRIP = register("fancy_drip", false, type -> DripParticleOptions.CODEC, type -> DripParticleOptions.STREAM_CODEC);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RAIN = PARTICLES.register("rain", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> URCHIN_SPIKE = PARTICLES.register("urchin_spike", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, ParticleType<EntitySwirlParticleOptions>> FISH_VORTEX = register("fish_vortex", false, type -> EntitySwirlParticleOptions.CODEC, type -> EntitySwirlParticleOptions.STREAM_CODEC);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> INFUSER_BUBBLE = PARTICLES.register("infuser_bubble", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PURIFIER_BUBBLE = PARTICLES.register("purifier_bubble", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> TAR_BUBBLE = PARTICLES.register("tar_bubble", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WATER_BUBBLE = PARTICLES.register("water_bubble", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SONIC_SCREAM = PARTICLES.register("sonic_scream", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WEEDWOOD_LEAF = PARTICLES.register("weedwood_leaf", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SLEEPING = PARTICLES.register("sleeping", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, ParticleType<EntitySwirlParticleOptions>> EMBER_SWIRL = register("ember_swirl", false, type -> EntitySwirlParticleOptions.CODEC, type -> EntitySwirlParticleOptions.STREAM_CODEC);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DRUID_CASTING = PARTICLES.register("druid_casting", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DRIPPING_TAR = PARTICLES.register("dripping_tar", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FALLING_TAR = PARTICLES.register("falling_tar", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LANDING_TAR = PARTICLES.register("landing_tar", () -> new SimpleParticleType(false));

	private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(String name, boolean overrideLimiter, final Function<ParticleType<T>, MapCodec<T>> codecGetter, final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
		return PARTICLES.register(name, () -> new ParticleType<T>(overrideLimiter) {
			@Override
			public MapCodec<T> codec() {
				return codecGetter.apply(this);
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return streamCodecGetter.apply(this);
			}
		});
	}


}