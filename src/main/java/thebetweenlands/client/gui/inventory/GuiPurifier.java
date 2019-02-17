package thebetweenlands.client.gui.inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.inventory.container.ContainerPurifier;
import thebetweenlands.common.tile.TileEntityPurifier;

@OnlyIn(Dist.CLIENT)
public class GuiPurifier extends GuiContainer {
    private TileEntityPurifier purifier;
    private static final ResourceLocation PURIFIER_GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/purifier.png");

    public GuiPurifier(InventoryPlayer inv, TileEntityPurifier tile) {
        super(new ContainerPurifier(inv, tile));
        purifier = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(PURIFIER_GUI_TEXTURE);
        int xx = (width - xSize) / 2;
        int yy = (height - ySize) / 2;
        drawTexturedModalRect(xx, yy, 0, 0, xSize, ySize);


        int water = purifier.getScaledWaterAmount(60);
        drawTexturedModalRect(xx + 34, yy + 72 - water, 176, 74 - water, 11, water);

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
