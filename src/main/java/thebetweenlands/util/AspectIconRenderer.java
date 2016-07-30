package thebetweenlands.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class AspectIconRenderer {
    public static final ResourceLocation ASPECT_MAP = new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/aspect_map.png");

    public static final int TEX_WIDTH = 64, TEX_HEIGHT = 64, ICON_WIDTH = 16, ICON_HEIGHT = 16, ICONS_H = TEX_WIDTH / ICON_WIDTH;

    private static final double REL_ICON_WIDTH = (double) ICON_WIDTH / (double) TEX_WIDTH, REL_ICON_HEIGHT = (double) ICON_HEIGHT / (double) TEX_HEIGHT;

    public static void renderIcon(int x, int y, int width, int height, int id) {
        int iconH = id % ICONS_H;
        int iconV = id / ICONS_H;
        double u = iconH * REL_ICON_WIDTH;
        double v = iconV * REL_ICON_WIDTH;
        Minecraft.getMinecraft().renderEngine.bindTexture(ASPECT_MAP);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexBuffer.pos(0, 0, 0).tex(u, v).endVertex();
        vertexBuffer.pos(0, height, 0).tex(u, v + REL_ICON_HEIGHT).endVertex();
        vertexBuffer.pos(width, height, 0).tex(u + REL_ICON_WIDTH, v + REL_ICON_HEIGHT).endVertex();
        vertexBuffer.pos(width, 0, 0).tex(u + REL_ICON_WIDTH, v).endVertex();
        tessellator.draw();
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }
}
