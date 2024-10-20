package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.material.Fluid;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ParticleRegistry;

public class TarBeastDripParticle extends DripParticle {
	protected TarBeastDripParticle(ClientLevel level, double x, double y, double z, Fluid type) {
		super(level, x, y, z, type);
	}

	public static TextureSheetParticle createTarHangParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		DripParticle particle = new TarBeastDripParticle.TarBeastDripHangParticle(level, x, y, z, FluidRegistry.TAR_STILL.get(), ParticleRegistry.FALLING_TAR.get());
		particle.setLifetime(5);
		particle.setColor(0.0F, 0.0F, 0.0F);
		return particle;
	}

	public static TextureSheetParticle createTarFallParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		DripParticle particle = new TarBeastDripParticle.TarBeastDripFallParticle(level, x, y, z, FluidRegistry.TAR_STILL.get(), ParticleRegistry.LANDING_TAR.get());
		particle.setColor(0.0F, 0.0F, 0.0F);
		return particle;
	}

	public static TextureSheetParticle createTarLandParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		DripParticle particle = new TarBeastDripParticle.TarBeastDripLandParticle(level, x, y, z, FluidRegistry.TAR_STILL.get());
		particle.setColor(0.0F, 0.0F, 0.0F);
		particle.setLifetime(40 + (int)(Math.random() * 40));
		return particle;
	}

	//pain
	public static class TarBeastDripHangParticle extends DripParticle.DripHangParticle {

		protected TarBeastDripHangParticle(ClientLevel level, double x, double y, double z, Fluid type, ParticleOptions particle) {
			super(level, x, y, z, type, particle);
			this.gravity = 0.01F;
		}
	}

	public static class TarBeastDripFallParticle extends DripParticle.FallAndLandParticle {

		protected TarBeastDripFallParticle(ClientLevel level, double x, double y, double z, Fluid type, ParticleOptions particle) {
			super(level, x, y, z, type, particle);
			this.gravity = 0.01F;
		}
	}

	public static class TarBeastDripLandParticle extends TarBeastDripParticle {

		protected TarBeastDripLandParticle(ClientLevel level, double x, double y, double z, Fluid type) {
			super(level, x, y, z, type);
		}
	}
}
