package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.event.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticlePortalBL extends Particle implements IParticleSpriteReceiver {
	private TextureAnimation animation;

	public ParticlePortalBL(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale) {
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
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		this.animation.setFrames(frames[0]);
		if(this.particleTexture == null) {
			this.setParticleTexture(frames[0][0].getSprite());
		}
	}

	@Override
	public void onUpdate() {
		this.animation.update();
		this.setParticleTexture(this.animation.getCurrentSprite());

		super.onUpdate();
	}

	public static final class Factory extends ParticleFactory<Factory, ParticlePortalBL> {
		public Factory() {
			super(ParticlePortalBL.class, ParticleTextureStitcher.create(ParticlePortalBL.class, new ResourceLocation("thebetweenlands:particle/portal")).setSplitAnimations(true));
		}

		@Override
		public ParticlePortalBL createParticle(ImmutableParticleArgs args) {
			return new ParticlePortalBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getInt(0), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(40);
		}
	}
}
