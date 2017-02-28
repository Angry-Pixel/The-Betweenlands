package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.entity.mobs.EntityVolatileSoul;

public class RenderVolatileSoul extends Render<EntityVolatileSoul> {
	public RenderVolatileSoul(RenderManager renderManager) {
		super(renderManager);
	}

	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/wight_face.png");

	@Override
	public void doRender(EntityVolatileSoul entity, double x, double y, double z, float yaw, float partialTicks) {
		float rx = ActiveRenderInfo.getRotationX();
		float rxz = ActiveRenderInfo.getRotationXZ();
		float rz = ActiveRenderInfo.getRotationZ();
		float ryz = ActiveRenderInfo.getRotationYZ();
		float rxy = ActiveRenderInfo.getRotationXY();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();

		float scale = 0.25F;

		float minU = 0;
		float maxU = 1;
		float minV = 0;
		float maxV = 1;

		this.bindTexture(TEXTURE);

		y += 0.2D;

		int i = 61680;
		int j = i % 65536;
		int k = i / 65536;

		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		buffer.pos((double)(x - rx * scale - ryz * scale), (double)(y - rxz * scale), (double)(z - rz * scale - rxy * scale)).tex((double)maxU, (double)maxV).color(255, 255, 255, 255).endVertex();
		buffer.pos((double)(x - rx * scale + ryz * scale), (double)(y + rxz * scale), (double)(z - rz * scale + rxy * scale)).tex((double)maxU, (double)minV).color(255, 255, 255, 255).endVertex();
		buffer.pos((double)(x + rx * scale + ryz * scale), (double)(y + rxz * scale), (double)(z + rz * scale + rxy * scale)).tex((double)minU, (double)minV).color(255, 255, 255, 255).endVertex();
		buffer.pos((double)(x + rx * scale - ryz * scale), (double)(y - rxz * scale), (double)(z + rz * scale - rxy * scale)).tex((double)minU, (double)maxV).color(255, 255, 255, 255).endVertex();
		tessellator.draw();

		i = entity.getBrightnessForRender(partialTicks);
		j = i % 65536;
		k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityVolatileSoul entity) {
		return null;
	}
}
