package thebetweenlands.client.gui.inventory;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerBLFurnace;
import thebetweenlands.common.tile.TileEntityBLFurnace;



@SideOnly(Side.CLIENT)
public class GuiBLFurnace extends GuiContainer {
	private static final ResourceLocation furnaceGuiTextures = new ResourceLocation("thebetweenlands:textures/gui/sulfurFurnace.png");
	private TileEntityBLFurnace tileFurnace;

	public GuiBLFurnace(InventoryPlayer inventory, TileEntityBLFurnace tile) {
		super(new ContainerBLFurnace(inventory, tile));
		this.tileFurnace = tile;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String s = this.tileFurnace.hasCustomName() ? this.tileFurnace.getName() : I18n.format(this.tileFurnace.getName(), new Object[0]);
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		if (this.tileFurnace.isBurning()) {
			int i1 = this.tileFurnace.getBurnTimeRemainingScaled(13);
			this.drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
			i1 = this.tileFurnace.getCookProgressScaled(24);
			this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
		}

	}
}
