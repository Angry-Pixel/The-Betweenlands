package thebetweenlands.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.CrabPotFilterMenu;

public class CrabPotFilterScreen extends AbstractContainerScreen<CrabPotFilterMenu> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/crab_pot_filter.png");
	private static final ResourceLocation ANADIA = TheBetweenlands.prefix("container/filter/fuel");
	private static final ResourceLocation ARROW = TheBetweenlands.prefix("container/filter/filter_progress");

	public CrabPotFilterScreen(CrabPotFilterMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = 174;
		this.imageHeight = 182;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, 8, 4210752, false);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.imageHeight - 93, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = this.topPos;
		graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
		if (this.getMenu().isBaited()) {
			int l = Mth.ceil(this.getMenu().getBaitProgress() * 10.0F) + 1;
			graphics.blitSprite(ANADIA, 17, 11, 0, 11 - l, i + 43, j + 46 + 11 - l, 17, l);
		}

		int j1 = Mth.ceil(this.getMenu().getFilterProgress() * 24.0F);
		graphics.blitSprite(ARROW, 24, 16, 0, 0, i + 72, j + 42, j1, 16);
	}
}
