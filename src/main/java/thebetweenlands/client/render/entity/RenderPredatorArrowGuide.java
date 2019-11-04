package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.render.particle.entity.ParticleBeam;
import thebetweenlands.common.entity.projectiles.EntityPredatorArrowGuide;
import thebetweenlands.util.LightingUtil;

public class RenderPredatorArrowGuide extends Render<EntityPredatorArrowGuide> {
	private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/beam.png");

	public RenderPredatorArrowGuide(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityPredatorArrowGuide entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		Entity target = entity.getTarget();
		Entity ridingEntity = entity.getRidingEntity();

		if(target != null) {
			double ix = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
			double iy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
			double iz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

			double targetIx = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks;
			double targetIy = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks + target.getEyeHeight();
			double targetIz = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks;

			double diffX = targetIx - ix;
			double diffY = targetIy - iy;
			double diffZ = targetIz - iz;

			LightingUtil.INSTANCE.setLighting(255);

			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
			GlStateManager.disableCull();
			GlStateManager.depthMask(false);

			this.bindTexture(BEAM_TEXTURE);

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y - (ridingEntity != null ? (entity.posY - ridingEntity.posY) : 0), z);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

			ParticleBeam.buildBeam(diffX, diffY, diffZ, new Vec3d(-diffX, -diffY, -diffZ), 0.05F, 0, 2F,
					ActiveRenderInfo.getRotationX(), ActiveRenderInfo.getRotationZ(), ActiveRenderInfo.getRotationYZ(), ActiveRenderInfo.getRotationXY(), ActiveRenderInfo.getRotationXZ(),
					(vx, vy, vz, u, v) -> {
						buffer.pos(vx, vy, vz).tex(u, v).color(35, 80, 110, 255).endVertex();
					});

			tessellator.draw();

			GlStateManager.popMatrix();

			GlStateManager.depthMask(true);
			GlStateManager.enableCull();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();

			LightingUtil.INSTANCE.revert();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPredatorArrowGuide entity) {
		return null;
	}
}
