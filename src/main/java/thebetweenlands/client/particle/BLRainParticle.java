package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class BLRainParticle extends WaterDropParticle {
	protected BLRainParticle(ClientLevel level, double x, double y, double z) {
		super(level, x, y, z);
	}

	public static final class Factory extends ParticleFactory<Factory, SimpleParticleType> {

		private final SpriteSet sprite;

		public Factory(SpriteSet sprites) {
			this.sprite = sprites;
		}

		@Override
		public BLRainParticle createParticle(SimpleParticleType type, ImmutableParticleArgs args) {
			var rain = new BLRainParticle(args.level, args.x, args.y, args.z);
			rain.pickSprite(this.sprite);
			return rain;
		}
	}
}
