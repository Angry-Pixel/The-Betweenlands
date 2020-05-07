package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleChiromawFeathers extends Particle implements IParticleSpriteReceiver {
	private TextureAnimation animation;

	public ParticleChiromawFeathers(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale) {
		super(world, x, y, z);
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.particleMaxAge = maxAge;
		this.particleScale = scale;
		this.animation = new TextureAnimation().setRandomStart(this.rand);
		this.particleGravity = 0.16F;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		if (this.animation != null && frames != null) {
			this.animation.setFrames(frames[0]);
			if (this.particleTexture == null) {
				this.setParticleTexture(frames[0][0].getSprite());
			}
		}
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.04D * (double)this.particleGravity;
		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;
		this.isExpired = this.prevPosY == this.posY;

		if (this.particleMaxAge-- <= 40) {
			this.setAlphaF((float)this.particleMaxAge / 40.0F);

			if (this.particleMaxAge-- <= 0) {
				this.setExpired();
			}
		}

		if (this.isExpired) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;

			if(this.particleMaxAge > 120) {
				this.particleMaxAge = 120;
			}
		}

		if (!this.isExpired) {
			this.animation.update();
			this.setParticleTexture(this.animation.getCurrentSprite());
		}
	}

	@Override
	public boolean shouldDisableDepth() {
		return this.particleAlpha < 1.0F;
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleChiromawFeathers> {
		public Factory() {
			super(ParticleChiromawFeathers.class, ParticleTextureStitcher.create(ParticleChiromawFeathers.class, new ResourceLocation("thebetweenlands:particle/chiromaw_transform")).setSplitAnimations(true));
		}

		@Override
		public ParticleChiromawFeathers createParticle(ImmutableParticleArgs args) {
			return new ParticleChiromawFeathers(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400);
		}
	}
}
