package thebetweenlands.inventory.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.inventory.container.ContainerPestleAndMortar;
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
		drawTexturedModalRect(xx + 45, yy + 56, 0, 166, progress, 6);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
	}
}
