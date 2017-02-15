package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleBlockProtection extends Particle implements IParticleSpriteReceiver {
	protected final EnumFacing face;

	protected ParticleBlockProtection(World world, double x, double y, double z, double mx, double my, double mz, EnumFacing face, float scale, int maxAge) {
		super(world, x, y, z, 0, 0, 0);
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.particleScale = scale;
		this.particleMaxAge = maxAge;
		this.face = face;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public boolean isTransparent() {
		return true;
	}

	@Override
	public void renderParticle(VertexBuffer buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
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

		Vec3d normal = new Vec3d(this.face.getDirectionVec());
		Vec3d perpendicular;
		switch(this.face) {
		case UP:
			perpendicular = new Vec3d(1, 0, 0);
			break;
		case DOWN:
			perpendicular = new Vec3d(-1, 0, 0);
			break;
		default:
			perpendicular = new Vec3d(0, 1, 0);
		}
		Vec3d perpendicular2 = perpendicular.crossProduct(normal);

		Vec3d[] vertices = new Vec3d[] {perpendicular.scale(-1).add(perpendicular2.scale(-1)).scale(scale), perpendicular.scale(-1).add(perpendicular2).scale(scale), perpendicular.add(perpendicular2).scale(scale), perpendicular.add(perpendicular2.scale(-1)).scale(scale)};

		if (this.field_190014_F != 0.0F) {
			float f8 = this.field_190014_F + (this.field_190014_F - this.field_190015_G) * partialTicks;
			float f9 = MathHelper.cos(f8 * 0.5F);
			float f10 = MathHelper.sin(f8 * 0.5F) * this.face.getFrontOffsetX();
			float f11 = MathHelper.sin(f8 * 0.5F) * this.face.getFrontOffsetY();
			float f12 = MathHelper.sin(f8 * 0.5F) * this.face.getFrontOffsetZ();
			Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

			for (int l = 0; l < 4; ++l) {
				vertices[l] = vec3d.scale(2.0D * vertices[l].dotProduct(vec3d)).add(vertices[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(vertices[l]).scale((double)(2.0F * f9)));
			}
		}

		buff.pos((double)rpx + vertices[0].xCoord, (double)rpy + vertices[0].yCoord, (double)rpz + vertices[0].zCoord).tex((double)maxU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[1].xCoord, (double)rpy + vertices[1].yCoord, (double)rpz + vertices[1].zCoord).tex((double)maxU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[2].xCoord, (double)rpy + vertices[2].yCoord, (double)rpz + vertices[2].zCoord).tex((double)minU, (double)minV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
		buff.pos((double)rpx + vertices[3].xCoord, (double)rpy + vertices[3].yCoord, (double)rpz + vertices[3].zCoord).tex((double)minU, (double)maxV).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleBlockProtection> {
		public Factory() {
			super(ParticleBlockProtection.class, ParticleTextureStitcher.create(ParticleBlockProtection.class, new ResourceLocation("thebetweenlands:particle/block_protection")));
		}

		@Override
		public ParticleBlockProtection createParticle(ImmutableParticleArgs args) {
			return new ParticleBlockProtection(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(EnumFacing.class, 0), args.scale, args.data.getInt(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(EnumFacing.UP, 20);
		}
	}
}

