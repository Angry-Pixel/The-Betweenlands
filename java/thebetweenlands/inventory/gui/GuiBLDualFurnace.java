package thebetweenlands.inventory.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.inventory.container.ContainerBLDualFurnace;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.tileentities.TileEntityBLDualFurnace;

@SideOnly(Side.CLIENT)
public class GuiBLDualFurnace extends GuiContainer {
    private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("thebetweenlands:textures/gui/dualFurnace.png");
    private TileEntityBLDualFurnace tileFurnace;

    public GuiBLDualFurnace(InventoryPlayer inventory, TileEntityBLDualFurnace tile) {
        super(new ContainerBLDualFurnace(inventory, tile));
        tileFurnace = tile;
		xSize = 176;
		ySize = 245;
    }

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
        String s = tileFurnace.hasCustomInventoryName() ? tileFurnace.getInventoryName() : I18n.format(tileFurnace.getInventoryName(), new Object[0]);
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, ySize - 96 + 2, 4210752);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(furnaceGuiTextures);
        int k = (width - xSize) / 2;
        int l = (height - ySize) / 2;
        drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

        if (tileFurnace.isBurning()) {
            int i1 = tileFurnace.getBurnTimeRemainingScaled(13);
            drawTexturedModalRect(k + 56, l + 40 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
            i1 = tileFurnace.getCookProgressScaled(24);
            drawTexturedModalRect(k + 79, l + 38, 176, 14, i1 + 1, 16);
        }
        
        if (tileFurnace.isBurning2()) {
            int i1 = tileFurnace.getBurnTimeRemainingScaled2(13);
            drawTexturedModalRect(k + 56, l + 111 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
            i1 = tileFurnace.getCookProgressScaled2(24);
            drawTexturedModalRect(k + 79, l + 109, 176, 14, i1 + 1, 16);
        }
        if (tileFurnace.getStackInSlot(6) == null)
			renderSlot(ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX).getIconIndex(), 26, 39);
        if (tileFurnace.getStackInSlot(7) == null)
			renderSlot(ItemGeneric.createStack(EnumItemGeneric.LIMESTONE_FLUX).getIconIndex(), 26, 110);
	}

	private void renderSlot(IIcon icon, int iconX, int iconY) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1f, 1f, 1f, 0.2f);
		mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);
		drawTexturedModelRectFromIcon(guiLeft + iconX, guiTop + iconY, icon, 16, 16);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
}
