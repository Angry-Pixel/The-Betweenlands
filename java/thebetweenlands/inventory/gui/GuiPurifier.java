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
		drawTexturedModalRect(xx + 34, yy + 72 - water, 176, 74 - water, 11, water);

		if (purifier.isPurifying()) {
			int count = purifier.getPurifyingProgress();
			drawTexturedModalRect(xx + 62, yy + 36 + count, 176, count, 12, 14 - count);
			drawTexturedModalRect(xx + 84, yy + 34, 176, 74, count * 2, 16);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
	}
}
