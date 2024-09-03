package thebetweenlands.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.SmokingRackMenu;

public class SmokingRackScreen extends AbstractContainerScreen<SmokingRackMenu> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/smoking_rack.png");
	private static final ResourceLocation SMOKE = TheBetweenlands.prefix("container/smoking_rack/smoke");
	private static final ResourceLocation ARROW = TheBetweenlands.prefix("container/smoking_rack/arrow");

	public SmokingRackScreen(SmokingRackMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = 176;
		this.imageHeight = 183;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
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
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

		if (this.getMenu().isSmoking()) {
			int l = Mth.ceil(this.menu.getSmokingProgress() * 31.0F);
			graphics.blitSprite(SMOKE, 24, 31, 0, 31 - l, i + 22, j + 65 - l, 24, l);
		}

		for (int slot = 1; slot <= 3; slot++) {
			int progress = Mth.ceil(this.getMenu().getProgressForIndex(slot) * 18.0F);
			graphics.blitSprite(ARROW, 18, 8, 0, 0, i + 97, j + 20 + (18 * slot), progress, 8);
		}
	}
}
