package thebetweenlands.client.gui.inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerBLDualFurnace;
import thebetweenlands.common.tile.TileEntityBLDualFurnace;

@SideOnly(Side.CLIENT)
public class GuiBLDualFurnace extends GuiContainer {
    private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("thebetweenlands:textures/gui/dual_furnace.png");
    private TileEntityBLDualFurnace tileFurnace;

    public GuiBLDualFurnace(InventoryPlayer inventory, TileEntityBLDualFurnace tile) {
        super(new ContainerBLDualFurnace(inventory, tile));
        tileFurnace = tile;
		xSize = 176;
		ySize = 245;
    }

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
        String s = tileFurnace.hasCustomName() ? tileFurnace.getName() : I18n.format(tileFurnace.getName(), new Object[0]);
        fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(furnaceGuiTextures);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

        if (tileFurnace.isBurning(0)) {
            int i1 = tileFurnace.getBurnTimeRemainingScaled(0, 13);
            drawTexturedModalRect(k + 56, l + 40 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
            i1 = tileFurnace.getCookProgressScaled(0, 24);
            drawTexturedModalRect(k + 79, l + 38, 176, 14, i1 + 1, 16);
        }
        
        if (tileFurnace.isBurning(1)) {
            int i1 = tileFurnace.getBurnTimeRemainingScaled(1, 13);
            drawTexturedModalRect(k + 56, l + 111 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
            i1 = tileFurnace.getCookProgressScaled(1, 24);
            drawTexturedModalRect(k + 79, l + 109, 176, 14, i1 + 1, 16);
        }
	}
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
