package thebetweenlands.client.particle;

import net.minecraft.client.particle.Particle;

public class ParticleHelper {
	public static final void setParticleColor(Particle particle, int color) {
		particle.setRBGColorF((float)(color >> 16 & 0xff) / 255F, (float)(color >> 8 & 0xff) / 255F, (float)(color & 0xff) / 255F);
		particle.setAlphaF((float)(color >> 24 & 0xff) / 255F);
	}
}
