package thebetweenlands.client.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticleFactory;
import thebetweenlands.client.particle.ParticleHelper;
import thebetweenlands.client.particle.ParticleTextureStitcher;
import thebetweenlands.client.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticlePortalBL extends Particle implements IParticleSpriteReceiver {
	public ParticlePortalBL(World world, double x, double y, double z, double mx, double my, double mz, int maxAge, float scale, int color) {
		super(world, x, y, z);
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.particleMaxAge = maxAge;
		//this.noClip = false;
		ParticleHelper.setParticleColor(this, color);
		this.particleScale = scale;
	}

	@Override
	public void setStitchedSprites(TextureAtlasSprite[] sprites) {
		this.setParticleTexture(sprites[0]);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	public static final class Factory extends BLParticleFactory {
		public Factory() {
			super(ParticlePortalBL.class, ParticleTextureStitcher.create(ParticlePortalBL.class, new ResourceLocation[]{new ResourceLocation("thebetweenlands:particle/portal")}));
		}

		@Override
		public Particle createParticle(ImmutableParticleArgs args) {
			return new ParticlePortalBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, (int)args.data[0], args.scale, args.color);
		}

		@Override
		protected void setDefaultArguments(ParticleArgs args) {
			args.withData(new Object[]{ 40 });
		}
	}
}
