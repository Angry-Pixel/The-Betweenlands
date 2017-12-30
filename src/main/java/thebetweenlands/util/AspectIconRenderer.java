package thebetweenlands.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class AspectIconRenderer {
	public static void renderIcon(int x, int y, int width, int height, ResourceLocation icon) {
		Minecraft.getMinecraft().renderEngine.bindTexture(icon);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		GlStateManager.disableBlend();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexBuffer.pos(0, 0, 0).tex(0, 0).endVertex();
		vertexBuffer.pos(0, height, 0).tex(0, 1).endVertex();
		vertexBuffer.pos(width, height, 0).tex(1, 1).endVertex();
		vertexBuffer.pos(width, 0, 0).tex(1, 0).endVertex();
		tessellator.draw();
		GlStateManager.enableBlend();
		GlStateManager.popMatrix();
	}
}
