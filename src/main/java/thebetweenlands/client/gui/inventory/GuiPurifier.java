package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerPurifier;
import thebetweenlands.common.tile.TileEntityPurifier;

@SideOnly(Side.CLIENT)
public class GuiPurifier extends GuiContainer {
    private TileEntityPurifier purifier;
    private static final ResourceLocation PURIFIER_GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/purifier.png");

    public GuiPurifier(InventoryPlayer inv, TileEntityPurifier tile) {
        super(new ContainerPurifier(inv, tile));
        purifier = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
    	GlStateManager.color(1, 1, 1, 1);
    	mc.renderEngine.bindTexture(PURIFIER_GUI_TEXTURE);
        int xx = (width - xSize) / 2;
        int yy = (height - ySize) / 2;
        drawTexturedModalRect(xx, yy, 0, 0, xSize, ySize);


        int water = purifier.getScaledWaterAmount(65);
        drawTexturedModalRect(xx + 34, yy + 10 + 65 - water, 206, 65 - water, 12, water);

        if (purifier.isPurifying()) {
            int count = purifier.getPurifyingProgress();
            drawTexturedModalRect(xx + 62, yy + 36 + count, 176, count, 12, 14 - count);
            drawTexturedModalRect(xx + 84, yy + 34, 176, 74, count * 2, 16);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
