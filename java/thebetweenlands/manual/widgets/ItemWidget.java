package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thebetweenlands.manual.IManualEntryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bart on 20-8-2015.
 */
public class ItemWidget extends ManualWidgetsBase {

    public int width;
    public int height;


    public ArrayList<ItemStack> stacks = new ArrayList<>();
    public float scale = 1f;
    private int currentDisplayItem = 0;


    public ItemWidget(int xStart, int yStart, ItemStack stack, float scale) {
        super( xStart, yStart);
        this.stacks.add(stack);
        this.scale = scale;
        width = (int) (16 * scale);
        height = (int) (16 * scale);
    }

    public ItemWidget(int xStart, int yStart, IManualEntryItem entry, float scale) {
        super(xStart, yStart);

        for (int i = 0; i <= entry.metas(); i++)
            this.stacks.add(new ItemStack(entry.getItem(), 1, i));
        this.scale = scale;
        width = (int) (16 * scale);
        height = (int) (16 * scale);
    }

    public ItemWidget( int xStart, int yStart, ArrayList<ItemStack> stacks, float scale) {
        super(xStart, yStart);
        this.stacks = stacks;
        this.scale = scale;
        width = (int) (16 * scale);
        height = (int) (16 * scale);
    }

    public ItemWidget(int xStart, int yStart, float scale, ArrayList<IManualEntryItem> items) {
        super(xStart, yStart);
        for (IManualEntryItem entry : items) {
            for (int i = 0; i <= entry.metas(); i++)
                this.stacks.add(new ItemStack(entry.getItem(), 1, i));
        }
        this.scale = scale;
        width = (int) (16 * scale);
        height = (int) (16 * scale);
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
        render.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), stacks.get(currentDisplayItem), (int) (xStart / scale), (int) (yStart / scale));
        render.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), stacks.get(currentDisplayItem), (int) (xStart / scale), (int) (yStart / scale));
        RenderHelper.disableStandardItemLighting();
        GL11.glScalef(1f, 1f, 1f);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

        if (mouseX >= xStart && mouseX <= xStart + 16 * scale && mouseY >= yStart && mouseY <= yStart + 16 * scale) {
            if (stacks.get(currentDisplayItem) != null) {
                List<String> tooltipData = stacks.get(currentDisplayItem).getTooltip(Minecraft.getMinecraft().thePlayer, false);
                List<String> parsedTooltip = new ArrayList();
                boolean first = true;

                for (String s : tooltipData) {
                    String s_ = s;
                    if (!first)
                        s_ = EnumChatFormatting.GRAY + s;
                    parsedTooltip.add(s_);
                    first = false;
                }
                renderTooltip(mouseX, mouseY, parsedTooltip, 0xffffff, 0xf0100010);
            }
        }
    }

    @Override
    public void mouseClicked(int x, int y, int mouseButton) {
        if (mouseButton == 2 && x >= xStart && x <= xStart + width && y >= yStart && y <= yStart + height) {
            if (currentDisplayItem + 1 < stacks.size()) {
                currentDisplayItem++;
            } else
                currentDisplayItem = 0;
            drawForeGround();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (manual != null) {
            if (manual.untilUpdate % 200 == 0) {
                if (currentDisplayItem + 1 < stacks.size()) {
                    currentDisplayItem++;
                } else
                    currentDisplayItem = 0;
                drawForeGround();
            }
        }
    }

}
