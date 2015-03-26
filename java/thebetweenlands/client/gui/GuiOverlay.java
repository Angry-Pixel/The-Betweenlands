package thebetweenlands.client.gui;

        import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
        import org.lwjgl.opengl.GL11;
        import thebetweenlands.manager.DecayManager;

        import java.util.Random;

@SideOnly(Side.CLIENT)
public class GuiOverlay extends Gui
{
    public ResourceLocation decayBarTexture = new ResourceLocation("thebetweenlands:textures/gui/decayBar.png");
    public Minecraft mc = Minecraft.getMinecraft();
    public Random random = new Random();

    public int updateCounter;

    @SubscribeEvent
    public void renderGui(RenderGameOverlayEvent.Post event)
    {
        updateCounter++;

        if (DecayManager.enableDecay(mc.thePlayer) && !mc.thePlayer.capabilities.isCreativeMode)
        {
            int width = event.resolution.getScaledWidth();
            int height = event.resolution.getScaledHeight();

            int startX = (width / 2) - (27 / 2) + 23;
            int startY = height - 49;

            int decayLevel = DecayManager.getDecayLevel(mc.thePlayer);

            if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR)
            {
                mc.getTextureManager().bindTexture(decayBarTexture);

                GL11.glEnable(GL11.GL_BLEND);
                for (int i = 0; i < 10; i++)
                {
                    int offsetY = mc.thePlayer.isInsideOfMaterial(Material.water) ? -10 : 0;

                    if (updateCounter % (decayLevel * 3 + 1) == 0) offsetY += random.nextInt(3) - 1;

                    GL11.glColor4f(1, 1, 1, 1);
                    
                    drawTexturedModalRect(startX + i * 8, startY + offsetY, 18, 0, 9, 9);
                    if (i * 2 + 1 < decayLevel) drawTexturedModalRect(startX + i * 8, startY + offsetY, 0, 0, 9, 9);
                    if (i * 2 + 1 == decayLevel) drawTexturedModalRect(startX + i * 8, startY + offsetY, 9, 0, 9, 9);
                }
            }
        }
    }
}
