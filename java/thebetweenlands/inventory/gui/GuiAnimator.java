package thebetweenlands.inventory.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.inventory.container.ContainerAnimator;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityAnimator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAnimator extends GuiContainer {
	private static final ResourceLocation GUI_ANIMATOR = new ResourceLocation("thebetweenlands:textures/gui/animator.png");
	private final TileEntityAnimator tile;

	public GuiAnimator(InventoryPlayer playerInventory, TileEntityAnimator tile) {
		super(new ContainerAnimator(playerInventory, tile));
		this.tile = tile;
		allowUserInput = false;
		ySize = 168;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_ANIMATOR);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);

		if (tile.getStackInSlot(1) == null)
			renderSlot(new ItemStack(BLItemRegistry.materialsBL, 1, 11).getIconIndex(), 43, 54);
		else
			this.drawTexturedModalRect(k + 45, l + 10, 176, 0, 16, 48);

		if (tile.getStackInSlot(2) == null)
			renderSlot(new ItemStack(BLItemRegistry.materialsBL, 1, 23).getIconIndex(), 116, 54);

		if (tile.progress > 0) {
			int i1 = tile.progress;
			this.drawTexturedModalRect(k + 118, l + 10 + i1, 176, i1, 16, 48);
		}

	}

	private void renderSlot(IIcon icon, int iconX, int iconY) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1f, 1f, 1f, 0.2f);
		mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
		drawTexturedModelRectFromIcon(guiLeft + iconX, guiTop + iconY, icon, 16, 16);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}
}
