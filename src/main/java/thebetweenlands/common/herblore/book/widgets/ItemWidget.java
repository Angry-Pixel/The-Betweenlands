package thebetweenlands.common.herblore.book.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemWidget extends ManualWidgetBase {
    public int width;
    public int height;


    public List<ItemStack> stacks = new ArrayList<>();
    public float scale = 1f;
    private int currentDisplayItem = 0;


    public ItemWidget(int xStart, int yStart, ItemStack stack, float scale) {
        super(xStart, yStart);
        this.stacks.add(stack);
        this.scale = scale;
        width = (int) (16 * scale);
        height = (int) (16 * scale);
    }

    public ItemWidget(int xStart, int yStart, List<ItemStack> stacks, float scale) {
        super(xStart, yStart);
        this.stacks = stacks;
        this.scale = scale;
        width = (int) (16 * scale);
        height = (int) (16 * scale);
    }


    @Override
	@SideOnly(Side.CLIENT)
    public void drawForeGround() {
        RenderItem render = Minecraft.getMinecraft().getRenderItem();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        GlStateManager.scale(scale, scale, scale);
        render.renderItemAndEffectIntoGUI(stacks.get(currentDisplayItem), (int) (xStart / scale), (int) (yStart / scale));
        render.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, stacks.get(currentDisplayItem), (int) (xStart / scale), (int) (yStart / scale), null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.scale(1f, 1f, 1f);
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();

        if (mouseX >= xStart && mouseX <= xStart + 16 * scale && mouseY >= yStart && mouseY <= yStart + 16 * scale) {
            if (stacks.get(currentDisplayItem) != null) {
                List<String> tooltipData = stacks.get(currentDisplayItem).getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
                List<String> parsedTooltip = new ArrayList();
                boolean first = true;

                for (String s : tooltipData) {
                    String s_ = s;
                    if (!first)
                        s_ = TextFormatting.GRAY + s;
                    parsedTooltip.add(s_);
                    first = false;
                }
                renderTooltip(mouseX, mouseY, parsedTooltip, 0xffffff, 0xf0100010);
            }
        }
    }

    @Override
    public void mouseClicked(int x, int y, int mouseButton) {
        super.mouseClicked(x, y, mouseButton);
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
