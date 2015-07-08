package thebetweenlands.inventory.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.inventory.container.ContainerAnimator;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.tileentities.TileEntityAnimator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAnimator extends GuiContainer {
	private final ResourceLocation GUI_ANIMATOR = new ResourceLocation("thebetweenlands:textures/gui/animator.png");
	private final TileEntityAnimator tile;
	private EntityPlayer playerSent;

	public GuiAnimator(EntityPlayer player, TileEntityAnimator tile) {
		super(new ContainerAnimator(player.inventory, tile));
		this.tile = tile;
		allowUserInput = false;
		xSize = 174;
		ySize = 164;
		playerSent = player;
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
		if (tile.getStackInSlot(1) != null) {
			int i1 = 40 - tile.life * 10;
			this.drawTexturedModalRect(k + 39, l + 8 + i1, 175, 2 + i1, 6, 42);
		}
		if (tile.progress > 0) {
			int i1 = tile.progress;
			drawTexturedModalRect(k + 129, l + 8 + i1, 175, 2 + i1, 6, 42);
			if (tile.itemsConsumed <= 16) {
				int i2 = tile.itemsConsumed;
				drawTexturedModalRect(k + 51, l + 65, 182, 18, i2 * 2, 2);
				drawTexturedModalRect(k + 123 - i2 * 2, l + 65, 254 - i2 * 2, 18, i2 * 2, 2);
			}
			if (tile.itemsConsumed > 16 && tile.itemsConsumed <= 32) {
				int i2 = tile.itemsConsumed;
				drawTexturedModalRect(k + 51, l + 65 - i2 + 16, 182, 18 - i2 + 16, 72, 2 + i2 - 16);
			}
		}
		if (tile.getStackInSlot(1) == null)
			renderSlot(ItemMaterialsBL.createStack(BLItemRegistry.lifeCrystal, 1, 0).getIconIndex(), 34, 57);
		if (tile.getStackInSlot(2) == null)
			renderSlot(ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR).getIconIndex(), 124, 57);
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

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (tile.lifeDepleted)
			playerSent.closeScreen();
	}
}
