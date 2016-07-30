package thebetweenlands.common.herblore.book.widgets;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.herblore.aspect.IAspectType;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class AspectWidget extends ManualWidgetBase {
    public IAspectType aspect;
    public float scale = 1.0f;

    public AspectWidget(int xStart, int yStart, IAspectType aspect, float scale) {
        super(xStart, yStart);
        this.aspect = aspect;
        this.scale = scale;
    }

    @Override
    public void drawForeGround() {
        super.drawForeGround();
        renderIcon(xStart, yStart, (int) (16 * scale), (int) (16 * scale), aspect.getIconIndex());
    }

    @Override
    public void drawToolTip() {
        super.drawToolTip();
        if (mouseX >= xStart && mouseX <= xStart + 16 * scale && mouseY >= yStart && mouseY <= yStart + 16 * scale) {
            List<String> tooltipData = new ArrayList<>();
            tooltipData.add(aspect.getName());
            tooltipData.add(ChatFormatting.GRAY + aspect.getType());
            renderTooltip(mouseX, mouseY, tooltipData, 0xffffff, 0xf0100010);
        }
    }

    public static void renderIcon(int x, int y, int width, int height, int id) {
        final ResourceLocation ASPECT_MAP = new ResourceLocation("thebetweenlands:textures/items/strictly_herblore/misc/aspect_map.png");
        final int TEX_WIDTH = 64, TEX_HEIGHT = 64, ICON_WIDTH = 16, ICON_HEIGHT = 16, ICONS_H = TEX_WIDTH / ICON_WIDTH;
        final double REL_ICON_WIDTH = (double) ICON_WIDTH / (double) TEX_WIDTH, REL_ICON_HEIGHT = (double) ICON_HEIGHT / (double) TEX_HEIGHT;
        int iconH = id % ICONS_H;
        int iconV = id / ICONS_H;
        double u = iconH * REL_ICON_WIDTH;
        double v = iconV * REL_ICON_WIDTH;
        Minecraft.getMinecraft().renderEngine.bindTexture(ASPECT_MAP);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexBuffer.pos(0, 0, 0).tex(u, v).endVertex();
        vertexBuffer.pos(0, height, 0).tex(u, v + REL_ICON_HEIGHT).endVertex();
        vertexBuffer.pos(width, height, 0).tex(u + REL_ICON_WIDTH, v + REL_ICON_HEIGHT).endVertex();
        vertexBuffer.pos(width, 0, 0).tex(u + REL_ICON_WIDTH, v).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
