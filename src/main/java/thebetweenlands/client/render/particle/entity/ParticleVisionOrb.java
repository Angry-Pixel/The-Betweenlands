package thebetweenlands.client.render.particle.entity;

import java.util.function.Supplier;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.GlStateManager.TexGen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.BatchedParticleRenderer.ParticleBatch;
import thebetweenlands.client.render.particle.ParticleBatchTypeBuilder;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;
import thebetweenlands.client.render.particle.entity.ParticleSwarm.ResourceLocationWithScale;

public class ParticleVisionOrb extends ParticleAnimated implements IParticleSpriteReceiver {
	public static final VertexFormat FORMAT = new VertexFormat();
	public static final VertexFormatElement TEX_2_2F = new VertexFormatElement(2, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.UV, 2);

	static {
		FORMAT.addElement(DefaultVertexFormats.POSITION_3F);
		FORMAT.addElement(DefaultVertexFormats.COLOR_4UB);
		FORMAT.addElement(DefaultVertexFormats.TEX_2F);
		FORMAT.addElement(DefaultVertexFormats.TEX_2S);
		FORMAT.addElement(TEX_2_2F);
		FORMAT.addElement(DefaultVertexFormats.NORMAL_3B);
		FORMAT.addElement(DefaultVertexFormats.PADDING_1B);
	}

	protected double cx, cy, cz;

	protected int lightmapX, lightmapY;

	protected float prevDistortion1X, prevDistortion1Y, distortion1X, distortion1Y;
	protected float prevDistortion2X, prevDistortion2Y, distortion2X, distortion2Y;
	protected float prevDistortion3X, prevDistortion3Y, distortion3X, distortion3Y;
	protected float prevDistortion4X, prevDistortion4Y, distortion4X, distortion4Y;

	protected Supplier<Float> alphaSupplier = null;

	protected ParticleVisionOrb(World world, double x, double y, double z, double mx, double my, double mz, double cx, double cy, double cz, float scale, int maxAge) {
		super(world, x, y, z, 0, 0, 0, maxAge, scale, false);
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.particleScale = scale;
		this.canCollide = false;
		this.particleAlpha = 0;
		this.particleMaxAge = maxAge;
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
		this.prevParticleAngle = this.particleAngle = world.rand.nextFloat() * 360.0f;
	}

	public ParticleVisionOrb setAlphaFunction(Supplier<Float> alpha) {
		this.alphaSupplier = alpha;
		return this;
	}

	@Override
	public void setStitchedSprites(Frame[][] frames) {
		if (this.animation != null && frames != null) {
			int variant = this.rand.nextInt(frames.length);

			this.animation.setFrames(frames[variant]);

			ResourceLocation location = frames[variant][0].getLocation();
			if(location instanceof ResourceLocationWithScale) {
				this.particleScale *= ((ResourceLocationWithScale) location).scale;
			}

			if(this.particleMaxAge < 0) {
				this.particleMaxAge = this.animation.getTotalDuration() - 1;
			}
			if (this.particleTexture == null) {
				this.setParticleTexture(frames[variant][0].getSprite());
			}
		}
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.alphaSupplier != null) {
			this.particleAlpha = this.alphaSupplier.get();
		}

		int brightness = this.getBrightnessForRender(1);
		this.lightmapX = (brightness >> 16) & 65535;
		this.lightmapY = brightness & 65535;

		float distortion = 0.02f + MathHelper.sin(this.particleAge * 0.01f) * MathHelper.cos(this.particleAge * 0.03f) * MathHelper.cos(this.particleAge * 0.1f) * 0.05f;

		this.prevDistortion1X = this.distortion1X;
		this.prevDistortion1Y = this.distortion1Y;
		this.distortion1X = MathHelper.cos(this.particleAge * 0.1f) * distortion;
		this.distortion1Y = MathHelper.sin(this.particleAge * 0.1f) * distortion;

		this.prevDistortion2X = this.distortion2X;
		this.prevDistortion2Y = this.distortion2Y;
		this.distortion2X = MathHelper.cos(this.particleAge * 0.12f) * distortion;
		this.distortion2Y = MathHelper.sin(this.particleAge * 0.12f) * distortion;


		this.prevDistortion3X = this.distortion3X;
		this.prevDistortion3Y = this.distortion3Y;
		this.distortion3X = MathHelper.cos(this.particleAge * 0.14f) * distortion;
		this.distortion3Y = MathHelper.sin(this.particleAge * 0.14f) * distortion;


		this.prevDistortion4X = this.distortion4X;
		this.prevDistortion4Y = this.distortion4Y;
		this.distortion4X = MathHelper.cos(this.particleAge * 0.16f) * distortion;
		this.distortion4Y = MathHelper.sin(this.particleAge * 0.16f) * distortion;
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rx, float rz, float ryz, float rxy, float rxz) {
		float minU = (float)this.particleTextureIndexX / 16.0F;
		float maxU = minU + 0.0624375F;
		float minV = (float)this.particleTextureIndexY / 16.0F;
		float maxV = minV + 0.0624375F;
		float scale = 0.1F * this.particleScale * 2;

		if(this.particleTexture != null) {
			minU = this.particleTexture.getMinU();
			maxU = this.particleTexture.getMaxU();
			minV = this.particleTexture.getMinV();
			maxV = this.particleTexture.getMaxV();
		}

		//remove 1px border to avoid artifacts from smooth filtering
		float borderU = (maxU - minU) / this.particleTexture.getIconWidth();
		float borderV = (maxV - minV) / this.particleTexture.getIconHeight();

		minU += borderU;
		maxU -= borderU;
		minV += borderV;
		maxV -= borderV;

		float rpx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float rpy = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float rpz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);

		double lcx = this.cx - interpPosX;
		double lcy = this.cy - interpPosY;
		double lcz = this.cz - interpPosZ;

		float normalX = (float)(rpx - lcx);
		float normalY = (float)(rpy - lcy);
		float normalZ = (float)(rpz - lcz);
		float invLen = (float) MathHelper.fastInvSqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
		normalX *= invLen;
		normalY *= invLen;
		normalZ *= invLen;

		float pp1x = 0, pp1y = 0, pp1z = 0;
		switch(EnumFacing.getFacingFromVector(normalX, normalY, normalZ)) {
		case UP:
			pp1x = 1;
			break;
		case DOWN:
			pp1x = -1;
			break;
		default:
			pp1y = 1;
			break;
		}

		float pp2x = crossX(pp1x, pp1y, pp1z, normalX, normalY, normalZ);
		float pp2y = crossY(pp1x, pp1y, pp1z, normalX, normalY, normalZ);
		float pp2z = crossZ(pp1x, pp1y, pp1z, normalX, normalY, normalZ);

		pp1x = -crossX(pp2x, pp2y, pp2z, normalX, normalY, normalZ);
		pp1y = -crossY(pp2x, pp2y, pp2z, normalX, normalY, normalZ);
		pp1z = -crossZ(pp2x, pp2y, pp2z, normalX, normalY, normalZ);

		float v1x = (pp1x - pp2x) * scale;
		float v1y = (pp1y - pp2y) * scale;
		float v1z = (pp1z - pp2z) * scale;
		float v2x = (-pp1x - pp2x) * scale;
		float v2y = (-pp1y - pp2y) * scale;
		float v2z = (-pp1z - pp2z) * scale;
		float v3x = (-pp1x + pp2x) * scale;
		float v3y = (-pp1y + pp2y) * scale;
		float v3z = (-pp1z + pp2z) * scale;
		float v4x = (pp1x + pp2x) * scale;
		float v4y = (pp1y + pp2y) * scale;
		float v4z = (pp1z + pp2z) * scale;

		if(this.particleAngle != 0.0F) {
			float angle = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float cos = MathHelper.cos(angle * 0.5F);
			float rdx = MathHelper.sin(angle * 0.5F) * normalX;
			float rdy = MathHelper.sin(angle * 0.5F) * normalY;
			float rdz = MathHelper.sin(angle * 0.5F) * normalZ;

			float dotrdrd = cos * cos - dot(rdx, rdy, rdz, rdx, rdy, rdz);

			float dotvrd = 2 * dot(v1x, v1y, v1z, rdx, rdy, rdz);
			float nx = rdx * dotvrd + v1x * dotrdrd + crossX(rdx, rdy, rdz, v1x, v1y, v1z) * 2 * cos;
			float ny = rdy * dotvrd + v1y * dotrdrd + crossY(rdx, rdy, rdz, v1x, v1y, v1z) * 2 * cos;
			float nz = rdz * dotvrd + v1z * dotrdrd + crossZ(rdx, rdy, rdz, v1x, v1y, v1z) * 2 * cos;
			v1x = nx;
			v1y = ny;
			v1z = nz;

			dotvrd = 2 * dot(v2x, v2y, v2z, rdx, rdy, rdz);
			nx = rdx * dotvrd + v2x * dotrdrd + crossX(rdx, rdy, rdz, v2x, v2y, v2z) * 2 * cos;
			ny = rdy * dotvrd + v2y * dotrdrd + crossY(rdx, rdy, rdz, v2x, v2y, v2z) * 2 * cos;
			nz = rdz * dotvrd + v2z * dotrdrd + crossZ(rdx, rdy, rdz, v2x, v2y, v2z) * 2 * cos;
			v2x = nx;
			v2y = ny;
			v2z = nz;

			dotvrd = 2 * dot(v3x, v3y, v3z, rdx, rdy, rdz);
			nx = rdx * dotvrd + v3x * dotrdrd + crossX(rdx, rdy, rdz, v3x, v3y, v3z) * 2 * cos;
			ny = rdy * dotvrd + v3y * dotrdrd + crossY(rdx, rdy, rdz, v3x, v3y, v3z) * 2 * cos;
			nz = rdz * dotvrd + v3z * dotrdrd + crossZ(rdx, rdy, rdz, v3x, v3y, v3z) * 2 * cos;
			v3x = nx;
			v3y = ny;
			v3z = nz;

			dotvrd = 2 * dot(v4x, v4y, v4z, rdx, rdy, rdz);
			nx = rdx * dotvrd + v4x * dotrdrd + crossX(rdx, rdy, rdz, v4x, v4y, v4z) * 2 * cos;
			ny = rdy * dotvrd + v4y * dotrdrd + crossY(rdx, rdy, rdz, v4x, v4y, v4z) * 2 * cos;
			nz = rdz * dotvrd + v4z * dotrdrd + crossZ(rdx, rdy, rdz, v4x, v4y, v4z) * 2 * cos;
			v4x = nx;
			v4y = ny;
			v4z = nz;
		}

		float n1x = -(float)(rpx + v1x - lcx);
		float n1y = -(float)(rpy + v1y - lcy);
		float n1z = -(float)(rpz + v1z - lcz);

		float n2x = -(float)(rpx + v2x - lcx);
		float n2y = -(float)(rpy + v2y - lcy);
		float n2z = -(float)(rpz + v2z - lcz);

		float n3x = -(float)(rpx + v3x - lcx);
		float n3y = -(float)(rpy + v3y - lcy);
		float n3z = -(float)(rpz + v3z - lcz);

		float n4x = -(float)(rpx + v4x - lcx);
		float n4y = -(float)(rpy + v4y - lcy);
		float n4z = -(float)(rpz + v4z - lcz);

		float dx = this.prevDistortion1X + (this.distortion1X - this.prevDistortion1X) * partialTicks;
		float dy = this.prevDistortion1Y + (this.distortion1Y - this.prevDistortion1Y) * partialTicks;
		v1x += (v1x * dx + v2x * dy);
		v1y += (v1y * dx + v2y * dy);
		v1z += (v1z * dx + v2z * dy);

		dx = this.prevDistortion2X + (this.distortion2X - this.prevDistortion2X) * partialTicks;
		dy = this.prevDistortion2Y + (this.distortion2Y - this.prevDistortion2Y) * partialTicks;
		v2x += (v1x * dx + v2x * dy);
		v2y += (v1y * dx + v2y * dy);
		v2z += (v1z * dx + v2z * dy);

		dx = this.prevDistortion3X + (this.distortion3X - this.prevDistortion3X) * partialTicks;
		dy = this.prevDistortion3Y + (this.distortion3Y - this.prevDistortion3Y) * partialTicks;
		v3x += (v1x * dx + v2x * dy);
		v3y += (v1y * dx + v2y * dy);
		v3z += (v1z * dx + v2z * dy);

		dx = this.prevDistortion4X + (this.distortion4X - this.prevDistortion4X) * partialTicks;
		dy = this.prevDistortion4Y + (this.distortion4Y - this.prevDistortion4Y) * partialTicks;
		v4x += (v1x * dx + v2x * dy);
		v4y += (v1y * dx + v2y * dy);
		v4z += (v1z * dx + v2z * dy);

		float alpha;
		if(this.particleAge >= this.particleMaxAge - 40) {
			alpha = this.particleAlpha * (this.particleMaxAge - this.particleAge) / 40.0f;
		} else if(this.particleAge <= 40) {
			alpha = this.particleAlpha * this.particleAge / 40.0f;
		} else {
			alpha = this.particleAlpha;
		}

		emit(buff, rpx + v1x, rpy + v1y, rpz + v1z, this.particleRed, this.particleGreen, this.particleBlue, alpha, maxU, maxV, this.lightmapX, this.lightmapY, n1x, n1y, n1z);
		emit(buff, rpx + v2x, rpy + v2y, rpz + v2z, this.particleRed, this.particleGreen, this.particleBlue, alpha, maxU, minV, this.lightmapX, this.lightmapY, n2x, n2y, n2z);
		emit(buff, rpx + v3x, rpy + v3y, rpz + v3z, this.particleRed, this.particleGreen, this.particleBlue, alpha, minU, minV, this.lightmapX, this.lightmapY, n3x, n3y, n3z);
		emit(buff, rpx + v4x, rpy + v4y, rpz + v4z, this.particleRed, this.particleGreen, this.particleBlue, alpha, minU, maxV, this.lightmapX, this.lightmapY, n4x, n4y, n4z);
	}

	private static void emit(BufferBuilder buff, double x, double y, double z, float r, float g, float b, float a, float u, float v, int lmx, int lmy, float nx, float ny, float nz) {
		float scale = (float) MathHelper.fastInvSqrt(nx * nx + ny * ny + nz * nz);
		buff.pos(x, y, z).color(r, g, b, a).tex(u, v).lightmap(lmx, lmy).tex(0, 0).normal(scale * nx, scale * ny, scale * nz).endVertex();
	}

	private static float crossX(float x1, float y1, float z1, float x2, float y2, float z2) {
		return y1 * z2 - z1 * y2;
	}

	private static float crossY(float x1, float y1, float z1, float x2, float y2, float z2) {
		return z1 * x2 - x1 * z2;
	}

	private static float crossZ(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * y2 - y1 * x2;
	}

	private static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	public static ParticleBatch createParticleBatch(Supplier<ResourceLocation> texture) {
		return createParticleBatch(() -> Minecraft.getMinecraft().getTextureManager().bindTexture(texture.get()));
	}

	public static ParticleBatch createParticleBatch(Runnable textureBinder) {
		return BatchedParticleRenderer.INSTANCE.createBatchType(new ParticleBatchTypeBuilder().pass()
				.format(ParticleVisionOrb.FORMAT)
				.depthMask(false)
				.blur(true)
				.setBlend(true)
				.blend(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA)
				.preRenderPassCallback(() -> {
					GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
					GlStateManager.enableTexture2D();

					GlStateManager.matrixMode(GL11.GL_TEXTURE);
					GlStateManager.rotate(180, 1, 0, 0);
					GlStateManager.matrixMode(GL11.GL_MODELVIEW);

					textureBinder.run();

					GlStateManager.enableTexGenCoord(TexGen.S);
					GlStateManager.enableTexGenCoord(TexGen.T);
					GlStateManager.texGen(TexGen.S, GL11.GL_SPHERE_MAP);
					GlStateManager.texGen(TexGen.T, GL11.GL_SPHERE_MAP);

					GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
					GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
				})
				.postRenderPassCallback(() -> {
					GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
					GlStateManager.matrixMode(GL11.GL_TEXTURE);
					GlStateManager.loadIdentity();
					GlStateManager.matrixMode(GL11.GL_MODELVIEW);
					GlStateManager.disableTexGenCoord(TexGen.S);
					GlStateManager.disableTexGenCoord(TexGen.T);

					GlStateManager.disableTexture2D();
					GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
					GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
				})
				.end().build());
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleVisionOrb> {
		public Factory(ResourceLocation... textures) {
			this(ParticleTextureStitcher.create(ParticleVisionOrb.class, textures));
		}

		public Factory(ParticleTextureStitcher<ParticleVisionOrb> stitcher) {
			super(ParticleVisionOrb.class, stitcher);
		}

		@Override
		public ParticleVisionOrb createParticle(ImmutableParticleArgs args) {
			return new ParticleVisionOrb(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getDouble(0), args.data.getDouble(1), args.data.getDouble(2), args.scale, args.data.getInt(3));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(0, 0, 0, 40);
		}
	}
}

