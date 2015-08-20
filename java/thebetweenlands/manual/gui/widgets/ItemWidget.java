package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thebetweenlands.manual.gui.GuiManualBase;

/**
 * Created by Bart on 20-8-2015.
 */
public class ItemWidget extends ManualWidgetsBase {

    public ItemStack stack;
    public float scale = 1f;


    public ItemWidget(GuiManualBase manual, int xStart, int yStart, ItemStack stack, float scale) {
        super(manual, xStart, yStart);
        this.stack = stack;
        this.scale = scale;
    }


    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        RenderItem render = new RenderItem();
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glScalef(scale, scale, scale);
        render.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), stack, xStart, yStart);
        render.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), stack, xStart, yStart);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

}
