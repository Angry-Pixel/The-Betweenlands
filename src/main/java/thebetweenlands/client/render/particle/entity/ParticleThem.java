package thebetweenlands.client.render.particle.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.render.particle.ParticleFactory;

public class ParticleThem extends Particle {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/them.png");
	public static final float TEXTURE_HEIGHT = 0.1279F;
	public static final int TEXTURE_COUNT = 5;

	private double startY;
	private float textureStart;

	public ParticleThem(World world, double x, double y, double z, float scale, int texture) {
		super(world, x, y, z, 0, 0, 0);
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.particleMaxAge = (int)1200;
		this.canCollide = false;
		this.particleScale = scale;
		this.startY = this.posY;
		this.textureStart = texture / (float) TEXTURE_COUNT;
	}

	@Override
	public void renderParticle(BufferBuilder vertexBuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

		float umin = 0;
		float umax = 1;
		float vmin = this.textureStart;
		float vmax = this.textureStart + TEXTURE_HEIGHT;
		float scale = this.particleScale;

		float ipx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
		float ipy = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
		float ipz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
		int brightness = this.getBrightnessForRender(partialTicks);
		int lightmapX = brightness >> 16 & 65535;
		int lightmapY = brightness & 65535;
		Vec3d[] rotation = new Vec3d[] {new Vec3d((double)(-rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(-rotationYZ * scale - rotationXZ * scale)), new Vec3d((double)(-rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(-rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(rotationYZ * scale - rotationXZ * scale))};

		if (this.particleAngle != 0.0F) {
			float interpolatedRoll = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
			float cos = MathHelper.cos(interpolatedRoll * 0.5F);
			float lookX = MathHelper.sin(interpolatedRoll * 0.5F) * (float)cameraViewDir.x;
			float lookY = MathHelper.sin(interpolatedRoll * 0.5F) * (float)cameraViewDir.y;
			float lookZ = MathHelper.sin(interpolatedRoll * 0.5F) * (float)cameraViewDir.z;
			Vec3d look = new Vec3d((double)lookX, (double)lookY, (double)lookZ);

			for (int l = 0; l < 4; ++l) {
				rotation[l] = look.scale(2.0D * rotation[l].dotProduct(look)).add(rotation[l].scale((double)(cos * cos) - look.dotProduct(look))).add(look.crossProduct(rotation[l]).scale((double)(2.0F * cos)));
			}
		}

		Vec3d look = new Vec3d(cameraViewDir.x, cameraViewDir.y, cameraViewDir.z).normalize();
		Vec3d diff = new Vec3d(this.posX - interpPosX, this.posY - interpPosY, this.posZ - interpPosZ).normalize();
		float angle = (float) Math.toDegrees(Math.acos(look.dotProduct(diff)));

		float fogEnd = FogHandler.getCurrentFogEnd();
		float fogStart = FogHandler.getCurrentFogStart();
		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		float particleDist = renderView == null ? 0.0F : (float)renderView.getDistance(this.posX, this.posY, this.posZ);
		float fadeStart = Math.max(fogStart + (fogEnd - fogStart) / 3.0F, 12.0F);
		float fadeEnd = 8.0F;

		float alpha = 1.0F;

		if(particleDist < fadeStart) {
			alpha = Math.max(1.0F - (fadeStart - particleDist) / (fadeStart - fadeEnd), 0.0F);
		}

		if(this.particleAge < 40) {
			alpha *= this.particleAge / 40.0F;
		} else if(this.particleAge > this.particleMaxAge - 40) {
			alpha *= (this.particleMaxAge - this.particleAge) / 40.0F;
		}

		alpha = Math.min(alpha * 1.75F, 1.0F);

		if(angle < 30.0F) {
			alpha *= Math.pow((angle / 30.0F), 2);
		}

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
		GlStateManager.depthMask(false);
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		vertexBuffer.pos((double)ipx + rotation[0].x, (double)ipy + rotation[0].y * 1.8F, (double)ipz + rotation[0].z).tex((double)umax, (double)vmax).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		vertexBuffer.pos((double)ipx + rotation[1].x, (double)ipy + rotation[1].y * 1.8F, (double)ipz + rotation[1].z).tex((double)umax, (double)vmin).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		vertexBuffer.pos((double)ipx + rotation[2].x, (double)ipy + rotation[2].y * 1.8F, (double)ipz + rotation[2].z).tex((double)umin, (double)vmin).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		vertexBuffer.pos((double)ipx + rotation[3].x, (double)ipy + rotation[3].y * 1.8F, (double)ipz + rotation[3].z).tex((double)umin, (double)vmax).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(lightmapX, lightmapY).endVertex();
		Tessellator.getInstance().draw();

		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.setPosition(this.posX, this.startY + Math.sin(this.particleAge / 150.0f) / 1.5F, this.posZ);

		Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
		if(renderView != null) {
			Vec3d diff = renderView.getPositionVector().subtract(this.posX, this.posY - renderView.getEyeHeight(), this.posZ);
			if(diff.length() < 2.0F) {
				this.setExpired();
			}
			Vec3d dir = diff.normalize();
			this.motionX = dir.x * 0.05D;
			this.motionZ = dir.z * 0.05D;
		}

		BlockPos checkPos = new BlockPos(this.posX, this.posY - 2, this.posZ);
		IBlockState blockStateBelow = this.world.getBlockState(checkPos);
		if(blockStateBelow.getBlock() == Blocks.AIR) {
			this.motionY = -0.01D;
		} else {
			if(this.world.getBlockState(checkPos.up()).getBlock() != Blocks.AIR) {
				this.motionY = 0.01D;
			}
		}

		this.move(this.motionX, this.motionY, this.motionZ);
		this.startY += this.motionY;

		this.motionX *= 0.96D;
		this.motionZ *= 0.96D;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}
	}

	@Override
	public int getFXLayer() {
		return 3;
	}

	@Override
	public boolean shouldDisableDepth() {
		return true;
	}


	public static final class Factory extends ParticleFactory<Factory, ParticleThem> {
		public Factory() {
			super(ParticleThem.class);
		}

		@Override
		public ParticleThem createParticle(ImmutableParticleArgs args) {
			return new ParticleThem(args.world, args.x, args.y, args.z, args.scale, args.data.getInt(0));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.8F).withData(0);
		}

		@Override
		protected void setDefaultArguments(World world, double x, double y, double z, ParticleArgs<?> args) {
			args.withData(world.rand.nextInt(TEXTURE_COUNT));
		}
	}
}
