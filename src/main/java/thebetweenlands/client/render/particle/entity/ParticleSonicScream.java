package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleSonicScream extends ParticleAnimated implements IParticleSpriteReceiver {
	protected final Vec3d dir;
	protected final Vec3d up;

	protected float initialAlpha;

	protected int initialFrame;

	protected ParticleSonicScream(World world, double x, double y, double z, double mx, double my, double mz, float scale, int maxAge, int initialFrame) {
		super(world, x, y, z, 0, 0, 0, maxAge, scale, false);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.dir = new Vec3d(mx, my, mz).normalize();
		this.up = new Vec3d(0, 1, 0);
		this.initialFrame = initialFrame;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		super.setStitchedSprites(frames);
		this.animation.setFrameCounter(this.initialFrame);
	}

	@Override
	public void setAlphaF(float alpha) {
		super.setAlphaF(alpha);
		this.initialAlpha = alpha;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.particleAlpha = this.initialAlpha * (1.0f - this.particleAge / (float)this.particleMaxAge);
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		float minU = (float)this.particleTextureIndexX / 16.0F;
		float maxU = minU + 0.0624375F;
		float minV = (float)this.particleTextureIndexY / 16.0F;
		float maxV = minV + 0.0624375F;

		float scale = 0.1F * this.particleScale * 2 * (this.particleAge / (float)this.particleMaxAge);

		if (this.particleTexture != null) {
			minU = this.particleTexture.getMinU();
			maxU = this.particleTexture.getMaxU();
			minV = this.particleTexture.getMinV();
			maxV = this.particleTexture.getMaxV();
		}

		float tw = maxU - minU;
		float th = maxV - minV;
		float oneTexelU = tw / 128.0f;
		float oneTexelV = th / 128.0f;

		minU += oneTexelU;
		maxU -= oneTexelU;
		minV += oneTexelV;
		maxV -= oneTexelV;

		float rpx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float rpy = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float rpz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		int brightness = this.getBrightnessForRender(partialTicks);
		int lightmapX = brightness >> 16 & 65535;
		int lightmapY = brightness & 65535;

		Vec3d normal = this.dir;
		Vec3d perpendicular = this.up;
		Vec3d perpendicular2 = perpendicular.crossProduct(normal);

		double yOffset = 0.125D;
		Vec3d[] vertices = new Vec3d[] {perpendicular.add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2.scale(-1)).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.scale(-1).add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale), perpendicular.add(perpendicular2).add(perpendicular.scale(yOffset)).scale(scale)};

		if (this.particleAngle != 0.0F) {
			float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float f9 = MathHelper.cos(f8 * 0.5F);
			float f10 = MathHelper.sin(f8 * 0.5F) * (float)this.dir.x;
			float f11 = MathHelper.sin(f8 * 0.5F) * (float)this.dir.y;
			float f12 = MathHelper.sin(f8 * 0.5F) * (float)this.dir.z;
			Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

			for (int l = 0; l < 4; ++l) {
				vertices[l] = vec3d.scale(2.0D * vertices[l].dotProduct(vec3d)).add(vertices[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(vertices[l]).scale((double)(2.0F * f9)));
			}
		}

		buff.pos((double)rpx + vertices[0].x, (double)rpy + vertices[0].y, (double)rpz + vertices[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[1].x, (double)rpy + vertices[1].y, (double)rpz + vertices[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[2].x, (double)rpy + vertices[2].y, (double)rpz + vertices[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[3].x, (double)rpy + vertices[3].y, (double)rpz + vertices[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();

		buff.pos((double)rpx + vertices[1].x, (double)rpy + vertices[1].y, (double)rpz + vertices[1].z).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[0].x, (double)rpy + vertices[0].y, (double)rpz + vertices[0].z).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[3].x, (double)rpy + vertices[3].y, (double)rpz + vertices[3].z).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[2].x, (double)rpy + vertices[2].y, (double)rpz + vertices[2].z).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleSonicScream> {
		public Factory() {
			super(ParticleSonicScream.class, ParticleTextureStitcher.create(ParticleSonicScream.class, new ResourceLocation("thebetweenlands:particle/sonic_scream")).setSplitAnimations(true));
		}

		@Override
		public ParticleSonicScream createParticle(ImmutableParticleArgs args) {
			return new ParticleSonicScream(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.scale, args.data.getInt(0), args.data.getInt(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(-1, 0);
		}
	}
}

