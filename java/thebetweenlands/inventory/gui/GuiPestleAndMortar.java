package thebetweenlands.inventory.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.inventory.container.ContainerPestleAndMortar;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.tileentities.TileEntityPestleAndMortar;

public class GuiPestleAndMortar extends GuiContainer {

	private TileEntityPestleAndMortar pestleAndMortar;
	private static final ResourceLocation PESTLE_AND_MORTAR_GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/pestleAndMortar.png");

	public GuiPestleAndMortar(InventoryPlayer inv, TileEntityPestleAndMortar tile) {
		super(new ContainerPestleAndMortar(inv, tile));
		pestleAndMortar = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(PESTLE_AND_MORTAR_GUI_TEXTURE);
		int xx = (width - xSize) / 2;
		int yy = (height - ySize) / 2;
		drawTexturedModalRect(xx, yy, 0, 0, xSize, ySize);

		int progress = pestleAndMortar.progress;
		drawTexturedModalRect(xx + 45, yy + 69, 0, 166, progress, 6);
		
		if (pestleAndMortar.getStackInSlot(3) == null)
			renderSlot(ItemMaterialsBL.createStack(BLItemRegistry.lifeCrystal, 1, 0).getIconIndex(), 79, 8);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
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
