package thebetweenlands.client.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import thebetweenlands.manager.DecayManager;

@SideOnly(Side.CLIENT)
public class GuiOverlay extends Gui
{
    public ResourceLocation decayBarTexture = new ResourceLocation("thebetweenlands:textures/gui/decayBar.png");
    public Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void renderGui(RenderGameOverlayEvent.Post event)
    {
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();

        int startX = (width / 2) - (27 / 2) + 23;
        int startY = height - 49;
        int offsetY = mc.thePlayer.isInsideOfMaterial(Material.water) ? -10 : 0;

        int decayLevel = DecayManager.getDecayLevel(mc.thePlayer);

        if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR)
        {
            mc.getTextureManager().bindTexture(decayBarTexture);
            for (int i = 0; i < decayLevel; i++) drawTexturedModalRect(startX + i * 9, startY + offsetY, 0, 0, 9, 9);
        }
    }
}
