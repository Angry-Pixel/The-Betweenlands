package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.sprite.TextureAnimation;

public class ParticleWaterRipple extends Particle implements IParticleSpriteReceiver {
	protected TextureAnimation animation;

	protected ParticleWaterRipple(World worldIn, double posXIn, double posYIn, double posZIn, float scale) {
		super(worldIn, posXIn, posYIn, posZIn);
		this.particleScale = scale;
		this.particleGravity = 0.0f;
		this.animation = new TextureAnimation();
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		if(this.animation != null) {
			this.animation.setFrames(frames[0]);
			if(this.particleTexture == null) {
				this.setParticleTexture(frames[0][0].getSprite());
			}
			this.particleMaxAge = this.animation.getTotalDuration() - 1;
		}
	}

	@Override
	public void onUpdate() {
		if(this.animation != null) {
			this.animation.update();
			this.setParticleTexture(this.animation.getCurrentSprite());
		}

		super.onUpdate();
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		float minU = (float)this.particleTextureIndexX / 16.0F;
		float maxU = minU + 0.0624375F;
		float minV = (float)this.particleTextureIndexY / 16.0F;
		float maxV = minV + 0.0624375F;
		float scale = 0.1F * this.particleScale;

		if (this.particleTexture != null) {
			minU = this.particleTexture.getMinU();
			maxU = this.particleTexture.getMaxU();
			minV = this.particleTexture.getMinV();
			maxV = this.particleTexture.getMaxV();
		}

		float rpx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float rpy = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float rpz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		int brightness = this.getBrightnessForRender(partialTicks);
		int lightmapX = brightness >> 16 & 65535;
		int lightmapY = brightness & 65535;

		buff.pos(rpx - scale, rpy, rpz - scale).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos(rpx - scale, rpy, rpz + scale).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos(rpx + scale, rpy, rpz + scale).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos(rpx + scale, rpy, rpz - scale).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleWaterRipple> {
		public Factory() {
			super(ParticleWaterRipple.class, ParticleTextureStitcher.create(ParticleWaterRipple.class, new ResourceLocation("thebetweenlands:particle/water_ripple")).setSplitAnimations(true));
		}

		@Override
		public ParticleWaterRipple createParticle(ImmutableParticleArgs args) {
			return new ParticleWaterRipple(args.world, args.x, args.y, args.z, args.scale);
		}
	}
}
