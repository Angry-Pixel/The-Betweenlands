package thebetweenlands.client.render.entity;

import java.util.AbstractMap.SimpleEntry;
import java.util.Random;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.event.handler.WorldRenderHandler;
import thebetweenlands.common.entity.mobs.EntityFirefly;

@SideOnly(Side.CLIENT)
public class RenderFirefly extends Render<EntityFirefly> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/wisp.png");
	private static final Random RANDOM = new Random();

	public RenderFirefly(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityFirefly entity, double x, double y, double z, float yaw, float partialTicks) {
		WorldRenderHandler.fireflies.add(new SimpleEntry<>(new SimpleEntry<>(this, entity), new Vector3d(x, y, z)));
	}

	public void renderFirefly(EntityFirefly entity, double x, double y, double z, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
		this.bindTexture(TEXTURE);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		float rx = ActiveRenderInfo.getRotationX();
		float rxz = ActiveRenderInfo.getRotationXZ();
		float rz = ActiveRenderInfo.getRotationZ();
		float ryz = ActiveRenderInfo.getRotationYZ();
		float rxy = ActiveRenderInfo.getRotationXY();

		double ipx = x;
		double ipy = y + 0.25D;
		double ipz = z;
		double scale = 1.0D;

		float red = 0.4F;
		float green = 0.2F;
		float blue = 0.0F;
		float alpha = 0.2F;

		if (RANDOM.nextInt(10) <= 2) {
			red = RANDOM.nextInt(10) / 20.0F + 0.25F;
		}

		double cScale = scale;

		for (int i = 0; i < (RANDOM.nextInt(10) <= 2 ? RANDOM.nextInt(10) : 10); i++) {
			cScale -= 0.15D;
			buffer.pos(ipx - rx * cScale - ryz * cScale, ipy - rxz * cScale, ipz - rz * cScale - rxy * cScale).tex(0.0, 1.0).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx - rx * cScale + ryz * cScale, ipy + rxz * cScale, ipz - rz * cScale + rxy * cScale).tex(1.0D, 1.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx + rx * cScale + ryz * cScale, ipy + rxz * cScale, ipz + rz * cScale + rxy * cScale).tex(1.0D, 0.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx + rx * cScale - ryz * cScale, ipy - rxz * cScale, ipz + rz * cScale - rxy * cScale).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
		}

		red = 0.6F;
		green = 0.6F;
		blue = 0.6F;

		cScale = scale / 4.0D;
		for (int i = 0; i < 10; i++) {
			cScale -= 0.15D / 4.0D;
			buffer.pos(ipx - rx * cScale - ryz * cScale, ipy - rxz * cScale, ipz - rz * cScale - rxy * cScale).tex(0.0D, 1.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx - rx * cScale + ryz * cScale, ipy + rxz * cScale, ipz - rz * cScale + rxy * cScale).tex(1.0D, 1.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx + rx * cScale + ryz * cScale, ipy + rxz * cScale, ipz + rz * cScale + rxy * cScale).tex(1.0D, 0.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx + rx * cScale - ryz * cScale, ipy - rxz * cScale, ipz + rz * cScale - rxy * cScale).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
		}

		tessellator.draw();

		GlStateManager.depthMask(false);
		GlStateManager.popMatrix();

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFirefly entity) {
		return null;
	}
}
