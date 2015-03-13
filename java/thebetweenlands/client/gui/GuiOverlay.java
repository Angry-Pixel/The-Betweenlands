package thebetweenlands.client.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class GuiOverlay extends Gui
{
    public ResourceLocation decayBarTexture = new ResourceLocation("thebetweenlands", "textures/gui/decayBar.png");
    public Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void renderGui(RenderGameOverlayEvent.Post event)
    {
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();

        if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) renderDecayBar((width / 2) - (27 / 2), (height / 2) - (9 / 2), 27, 9, decayBarTexture);
    }

    public void renderDecayBar(int x, int y, int width, int height, ResourceLocation texture)
    {
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glColor4f(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(texture);
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(x, y + height, -90d, 0d, 1d);
        tess.addVertexWithUV(x + width, y + height, -90d, 1d, 1d);
        tess.addVertexWithUV(x + width, y, -90d, 1d, 0d);
        tess.addVertexWithUV(x, y, -90d, 0d, 0d);
        tess.draw();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glColor4f(1f, 1f, 1f, 1f);
    }
}
