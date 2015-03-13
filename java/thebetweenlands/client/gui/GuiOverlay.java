package thebetweenlands.client.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

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

       if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR)
        {
            renderDecayBar((width / 2) - (27 / 2) + 23, height - 49, 27, 9, decayBarTexture, mc.thePlayer.isInsideOfMaterial(Material.water));
        }
    }

    public void renderDecayBar(int x, int y, int width, int height, ResourceLocation texture, boolean offset)
    {
        int i = offset ? -10 : 0;

        mc.getTextureManager().bindTexture(texture);

        drawTexturedModalRect(x, y, 0, 0, width, height);

        /*Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(x, y + height + i, -90d, 0d, 1d);
        tess.addVertexWithUV(x + width, y + height + i, -90d, 1d, 1d);
        tess.addVertexWithUV(x + width, y + i, -90d, 1d, 0d);
        tess.addVertexWithUV(x, y + i, -90d, 0d, 0d);
        tess.draw();*/
    }
}
