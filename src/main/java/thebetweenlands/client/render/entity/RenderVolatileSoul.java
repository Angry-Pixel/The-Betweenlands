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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityVolatileSoul;

public class RenderVolatileSoul extends Render<EntityVolatileSoul> {
	public RenderVolatileSoul(RenderManager renderManager) {
		super(renderManager);
	}

	protected static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/wight_face.png");
	protected static final ResourceLocation TEXTURE_TRAIL = new ResourceLocation("thebetweenlands:textures/entity/volatile_soul_trail.png");

	@Override
	public void doRender(EntityVolatileSoul entity, double x, double y, double z, float yaw, float partialTicks) {
		float rx = ActiveRenderInfo.getRotationX();
		float rxz = ActiveRenderInfo.getRotationXZ();
		float rz = ActiveRenderInfo.getRotationZ();
		float ryz = ActiveRenderInfo.getRotationYZ();
		float rxy = ActiveRenderInfo.getRotationXY();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		float scale = 0.25F;

		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;

		this.bindTexture(TEXTURE);

		y += 0.2D;

		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		buffer.pos((double)(x - rx * scale - ryz * scale), (double)(y - rxz * scale), (double)(z - rz * scale - rxy * scale)).tex((double)maxU, (double)maxV).color(255, 255, 255, 255).endVertex();
		buffer.pos((double)(x - rx * scale + ryz * scale), (double)(y + rxz * scale), (double)(z - rz * scale + rxy * scale)).tex((double)maxU, (double)minV).color(255, 255, 255, 255).endVertex();
		buffer.pos((double)(x + rx * scale + ryz * scale), (double)(y + rxz * scale), (double)(z + rz * scale + rxy * scale)).tex((double)minU, (double)minV).color(255, 255, 255, 255).endVertex();
		buffer.pos((double)(x + rx * scale - ryz * scale), (double)(y - rxz * scale), (double)(z + rz * scale - rxy * scale)).tex((double)minU, (double)maxV).color(255, 255, 255, 255).endVertex();

		tessellator.draw();

		this.bindTexture(TEXTURE_TRAIL);

		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.004F);
		GlStateManager.color(1, 1, 1, 1);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		double trailOffsetX = (x - (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks));
		double trailOffsetY = (y - (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks));
		double trailOffsetZ = (z - (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks));
		int interpSteps = 10;
		float alpha = 1.0F;
		float alphaDecr = alpha / entity.getTrail().size() / (float)interpSteps;
		scale = 0.08F;
		float scaleDecr = scale / entity.getTrail().size() / (float)interpSteps;
		Vec3d prevTrailPos = null;
		for(Vec3d trailPos : entity.getTrail()) {
			if(prevTrailPos != null) {
				for(int s = 0; s < interpSteps; s++) {
					double tx = prevTrailPos.x + (trailPos.x - prevTrailPos.x) / (float)interpSteps * s + trailOffsetX;
					double ty = prevTrailPos.y + (trailPos.y - prevTrailPos.y) / (float)interpSteps * s + trailOffsetY;
					double tz = prevTrailPos.z + (trailPos.z - prevTrailPos.z) / (float)interpSteps * s + trailOffsetZ;
					buffer.pos((double)(tx - rx * scale - ryz * scale), (double)(ty - rxz * scale), (double)(tz - rz * scale - rxy * scale)).tex((double)maxU, (double)maxV).color(alpha, alpha, alpha, 1).endVertex();
					buffer.pos((double)(tx - rx * scale + ryz * scale), (double)(ty + rxz * scale), (double)(tz - rz * scale + rxy * scale)).tex((double)maxU, (double)minV).color(alpha, alpha, alpha, 1).endVertex();
					buffer.pos((double)(tx + rx * scale + ryz * scale), (double)(ty + rxz * scale), (double)(tz + rz * scale + rxy * scale)).tex((double)minU, (double)minV).color(alpha, alpha, alpha, 1).endVertex();
					buffer.pos((double)(tx + rx * scale - ryz * scale), (double)(ty - rxz * scale), (double)(tz + rz * scale - rxy * scale)).tex((double)minU, (double)maxV).color(alpha, alpha, alpha, 1).endVertex();
					scale -= scaleDecr;
					alpha -= alphaDecr;
				}
			}
			prevTrailPos = trailPos;
		}

		tessellator.draw();

		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityVolatileSoul entity) {
		return null;
	}
}
