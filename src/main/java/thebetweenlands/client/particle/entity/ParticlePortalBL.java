package thebetweenlands.client.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;
import thebetweenlands.client.particle.ParticleHelper;
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
	public void setSprite(TextureAtlasSprite sprite) {
		this.setParticleTexture(sprite);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}
}
