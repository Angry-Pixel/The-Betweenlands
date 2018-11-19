package thebetweenlands.client.render.particle.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.audio.GemSingerEchoSound;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.lib.ModInfo;

public class ParticleSoundRipple extends Particle {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/particle/sound_ripple.png");

	private boolean spawnMore;
	private int delay;

	public ParticleSoundRipple(World world, double x, double y, double z, float scale, int delay) {
		this(world, x, y, z, scale, delay, true);
	}

	protected ParticleSoundRipple(World world, double x, double y, double z, float scale, int delay, boolean spawnMore) {
		super(world, x, y, z, 0, 0, 0);
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.canCollide = false;
		this.particleScale = scale;
		this.setAlphaF(0);
		this.spawnMore = spawnMore;
		this.delay = delay;
		this.particleMaxAge = this.spawnMore ? (100 + this.delay) : 20;
	}

	@Override
	public void renderParticle(BufferBuilder vertexBuffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if(!this.spawnMore) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

			float umin = 0;
			float umax = 1;
			float vmin = 0;
			float vmax = 1;

			float scale = this.particleScale * ((this.particleAge + partialTicks) / (float)this.particleMaxAge);

			Vec3d camPos = ActiveRenderInfo.getCameraPosition();

			float ipx = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - (interpPosX + camPos.x));
			float ipy = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - (interpPosY + camPos.y));
			float ipz = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - (interpPosZ + camPos.z));

			float len = (float)Math.sqrt(ipx*ipx + ipy*ipy + ipz*ipz);

			if(len > 0) {
				ipx /= len;
				ipy /= len;
				ipz /= len;
			}

			ipx += camPos.x;
			ipy += camPos.y;
			ipz += camPos.z;

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

			float alpha = MathHelper.clamp((this.particleMaxAge - (this.particleAge + partialTicks)) / 10.0F, 0, 1);

			GlStateManager.color(1, 1, 1, 1);

			GlStateManager.disableLighting();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
			GlStateManager.disableDepth();
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

			vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			vertexBuffer.pos((double)ipx + rotation[0].x, (double)ipy + rotation[0].y, (double)ipz + rotation[0].z).tex((double)umax, (double)vmax).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(240, 240).endVertex();
			vertexBuffer.pos((double)ipx + rotation[1].x, (double)ipy + rotation[1].y, (double)ipz + rotation[1].z).tex((double)umax, (double)vmin).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(240, 240).endVertex();
			vertexBuffer.pos((double)ipx + rotation[2].x, (double)ipy + rotation[2].y, (double)ipz + rotation[2].z).tex((double)umin, (double)vmin).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(240, 240).endVertex();
			vertexBuffer.pos((double)ipx + rotation[3].x, (double)ipy + rotation[3].y, (double)ipz + rotation[3].z).tex((double)umin, (double)vmax).color(this.particleRed, this.particleGreen, this.particleBlue, alpha).lightmap(240, 240).endVertex();
			Tessellator.getInstance().draw();

			GlStateManager.disableBlend();
			GlStateManager.enableDepth();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		}
	}

	@Override
	public void onUpdate() {
		if(this.spawnMore && this.particleAge >= this.delay) {
			if(this.particleAge == this.delay && Minecraft.getMinecraft().player != null) {
				Minecraft.getMinecraft().getSoundHandler().playSound(new GemSingerEchoSound(new Vec3d(this.posX, this.posY, this.posZ)).setVolumeAndPitch(0.7f, 0.98f + this.world.rand.nextFloat() * 0.06f - 0.03f));
			}
			if((this.particleAge - this.delay) % 10 == 0) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.UNBATCHED, new ParticleSoundRipple(this.world, this.posX, this.posY, this.posZ, this.particleScale, 0, false));
			}
		}

		if(this.particleAge++ >= this.particleMaxAge) {
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

	public static final class Factory extends ParticleFactory<Factory, ParticleSoundRipple> {
		public Factory() {
			super(ParticleSoundRipple.class);
		}

		@Override
		public ParticleSoundRipple createParticle(ImmutableParticleArgs args) {
			return new ParticleSoundRipple(args.world, args.x, args.y, args.z, args.scale, args.data.getInt(0));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withScale(0.125F).withData(20);
		}
	}
}
