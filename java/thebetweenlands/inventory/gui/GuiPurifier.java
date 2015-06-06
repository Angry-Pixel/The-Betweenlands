package thebetweenlands.inventory.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.inventory.container.ContainerPurifier;
import thebetweenlands.tileentities.TileEntityPurifier;

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
		drawTexturedModalRect(xx + 69, yy + 71 - water, 176, 74 - water, 11, water);

		if (purifier.isPurifying()) {
			int i1 = purifier.getPurifyingProgress();
			drawTexturedModalRect(xx + 94, yy + 49 - i1, 176, 13 - i1, 73,  i1 + 1);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
	}
}
