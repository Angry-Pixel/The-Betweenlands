package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleSimple extends Particle implements IParticleSpriteReceiver {
	private float startAlpha = 1.0F;
	private boolean fade = false;

	public ParticleSimple(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int maxAge, float scale, boolean fade, float gravity, boolean exactMotion) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.particleMaxAge = maxAge;
		this.particleScale = scale;
		this.particleGravity = gravity;
		this.fade = fade;
		if(exactMotion) {
			this.motionX = xSpeedIn;
			this.motionY = ySpeedIn;
			this.motionZ = zSpeedIn;
		}
		if(fade) {
			this.particleAlpha = 0;
		}
	}

	@Override
	public void setAlphaF(float alpha) {
		super.setAlphaF(alpha);
		this.startAlpha = alpha;
		if(this.fade) {
			this.particleAlpha = 0;
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.fade) {
			int fadeOutTime = Math.max(1, Math.min(40, (this.particleMaxAge - 10) / 2));
			if(this.particleAge > this.particleMaxAge - fadeOutTime) {
				this.particleAlpha = (this.startAlpha * (this.particleMaxAge - this.particleAge) / (float)fadeOutTime);
			} else if(this.particleAge <= 10) {
				this.particleAlpha = this.startAlpha * this.particleAge / 10.0f;
			}
		}
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	public static final class GenericFactory extends ParticleFactory<GenericFactory, ParticleSimple> {
		public GenericFactory(ResourceLocation texture) {
			super(ParticleSimple.class, ParticleTextureStitcher.create(ParticleSimple.class, texture));
		}

		@Override
		public ParticleSimple createParticle(ImmutableParticleArgs args) {
			return new ParticleSimple(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale, args.data.getBool(1), args.data.getFloat(2), args.data.getBool(3));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(80, true, 0.0F, false);
		}
	}
}
