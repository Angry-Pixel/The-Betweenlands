package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thebetweenlands.common.inventory.container.ContainerWeedwoodWorkbench;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;

@SideOnly(Side.CLIENT)
public class GuiWeedwoodWorkbench extends GuiContainer {
    private static final ResourceLocation craftingTableGuiTextures = new ResourceLocation("textures/gui/container/crafting_table.png");

    public GuiWeedwoodWorkbench(InventoryPlayer playerInventory, TileEntityWeedwoodWorkbench table) {
        super(new ContainerWeedwoodWorkbench(playerInventory, table));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        fontRenderer.drawString(I18n.format("container.crafting"), 28, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(craftingTableGuiTextures);
        int centerX = (width - xSize) / 2;
        int centerY = (height - ySize) / 2;
        drawTexturedModalRect(centerX, centerY, 0, 0, xSize, ySize);
    }
}
