package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerCrabPotFilter;
import thebetweenlands.common.tile.TileEntityCrabPotFilter;

@SideOnly(Side.CLIENT)
public class GuiCrabPotFilter extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/crab_pot_filter.png");
	private TileEntityCrabPotFilter crab_pot_filter;

	public GuiCrabPotFilter(EntityPlayer inventory, TileEntityCrabPotFilter tile) {
		super(new ContainerCrabPotFilter(inventory, tile));
		crab_pot_filter = tile;
		ySize = 182;
		xSize = 174;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String s = crab_pot_filter.hasCustomName() ? crab_pot_filter.getName() : I18n.format(crab_pot_filter.getName(), new Object[0]);
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1, 1, 1, 1);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
		
		if (crab_pot_filter.getBaitProgress() > 0 ) {
			int progressBait = crab_pot_filter.getBaitProgressScaled(7);
			this.drawTexturedModalRect(k + 43, l + 46 + progressBait, 174, 0 + progressBait, 17, 11 -progressBait);
		}

			int progressArrow = crab_pot_filter.getFilteringProgressScaled(24);
			this.drawTexturedModalRect(k + 72, l + 43, 174, 11, progressArrow, 17);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
