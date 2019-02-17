package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityFortressBossBlockade;
import thebetweenlands.util.LightingUtil;

public class RenderFortressBossBlockade extends Render<EntityFortressBossBlockade> {
	private static final ResourceLocation SHIELD_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");

	public RenderFortressBossBlockade(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityFortressBossBlockade entity, double x, double y, double z, float yaw, float partialTicks) {
		EntityFortressBossBlockade blockade = (EntityFortressBossBlockade) entity;

		Vec3d[] vertices = blockade.getTriangleVertices(partialTicks);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.2F, z);

		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		LightingUtil.INSTANCE.setLighting(255);

		float ticks = (float)entity.ticksExisted + partialTicks;
		this.bindTexture(SHIELD_TEXTURE);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.pushMatrix();
		float uOffset = (ticks * 0.01F) % 1.0F;
		float vOffset = (ticks * 0.01F) % 1.0F;
		GlStateManager.translate(uOffset, vOffset, 0.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableTexture2D();

		GlStateManager.depthMask(false);
		buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR);
		double textureScale = 4.0D;
		double cu = textureScale / 2.0D;
		double cv = textureScale * Math.sqrt(2) / 2.0D;
		int layers = 8;
		for(int l = 0; l < layers; l++) {
			double cos = Math.cos(2.0D * Math.PI / layers * l);
			double sin = 1+Math.sin(2.0D * Math.PI / layers * l);
			double tu1 = 0.0D - cu;
			double tv1 = 0.0D - cv;
			double tu2 = textureScale / 2.0D / layers * l - cu;
			double tv2 = textureScale / layers * l * Math.sqrt(2) - cv;
			double tu3 = textureScale / layers * l - cu;
			double tv3 = 0.0D - cv;
			tu1 = cu + tu1 * sin;
			tv1 = cv + tv1 * cos;
			tu2 = cu + tu2 * sin;
			tv2 = cv + tv2 * cos;
			tu3 = cu + tu3 * sin;
			tv3 = cv + tv3 * cos;
			buffer.pos(vertices[0].x, vertices[0].y, vertices[0].z).tex(tu1, tv1).color(0.5F, 0.6F, 1F, 0.5F).endVertex();
			buffer.pos(vertices[1].x, vertices[1].y, vertices[1].z).tex(tu2, tv2).color(0.5F, 0.6F, 1F, 0.5F).endVertex();
			buffer.pos(vertices[2].x, vertices[2].y, vertices[2].z).tex(tu3, tv3).color(0.5F, 0.6F, 1F, 0.5F).endVertex();
		}
		tessellator.draw();
		GlStateManager.depthMask(true);

		GlStateManager.disableTexture2D();
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(vertices[0].x, vertices[0].y, vertices[0].z).color(0.5F, 0.6F, 1F, 0.5F).endVertex();
		buffer.pos(vertices[1].x, vertices[1].y, vertices[1].z).color(0.5F, 0.6F, 1F, 0.5F).endVertex();
		buffer.pos(vertices[2].x, vertices[2].y, vertices[2].z).color(0.5F, 0.6F, 1F, 0.5F).endVertex();
		tessellator.draw();

		GlStateManager.cullFace(CullFace.BACK);
		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GlStateManager.glLineWidth(1.0F);

		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(vertices[0].x, vertices[0].y, vertices[0].z).color(0.5F, 0.75F, 1F, 1.0F).endVertex();
		buffer.pos(vertices[1].x, vertices[1].y, vertices[1].z).color(0.5F, 0.75F, 1F, 1.0F).endVertex();
		buffer.pos(vertices[1].x, vertices[1].y, vertices[1].z).color(0.5F, 0.75F, 1F, 1.0F).endVertex();
		buffer.pos(vertices[2].x, vertices[2].y, vertices[2].z).color(0.5F, 0.75F, 1F, 1.0F).endVertex();
		buffer.pos(vertices[2].x, vertices[2].y, vertices[2].z).color(0.5F, 0.75F, 1F, 1.0F).endVertex();
		buffer.pos(vertices[0].x, vertices[0].y, vertices[0].z).color(0.5F, 0.75F, 1F, 1.0F).endVertex();
		tessellator.draw();

		GlStateManager.popMatrix();

		LightingUtil.INSTANCE.revert();

		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GlStateManager.enableLighting();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFortressBossBlockade p_110775_1_) {
		return null;
	}
}
