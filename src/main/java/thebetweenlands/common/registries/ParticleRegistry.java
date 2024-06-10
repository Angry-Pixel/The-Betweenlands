package thebetweenlands.common.registries;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.common.TheBetweenlands;

public class ParticleRegistry {
	
	// Particle list
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TheBetweenlands.ID);
	
	// Generic particles
	public static final RegistryObject<SimpleParticleType> SULFUR_GENERIC = PARTICLES.register("sulfur_generic_effect", () -> new SimpleParticleType(true));
	
	// Custom particles
	public static final RegistryObject<SimpleParticleType> PORTAL_EFFECT = PARTICLES.register("portal_effect", () -> new SimpleParticleType(true));
	
	// Register particle list
	public static void register(IEventBus event) {
		PARTICLES.register(event);
	}
}