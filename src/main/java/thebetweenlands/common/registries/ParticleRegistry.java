package thebetweenlands.common.registries;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

public class ParticleRegistry {

	// Particle list
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, TheBetweenlands.ID);

	// Generic particles
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SULFUR_GENERIC = PARTICLES.register("sulfur_generic_effect", () -> new SimpleParticleType(true));

	// Custom particles
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PORTAL_EFFECT = PARTICLES.register("portal_effect", () -> new SimpleParticleType(true));
}