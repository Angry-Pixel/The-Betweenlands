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
			int i1 = purifier.getPurifyingProgress();
			//drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
			drawTexturedModalRect(xx + 62, yy + 36 + i1, 176, 0 + i1, 12, 14 - i1);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
	}
}
