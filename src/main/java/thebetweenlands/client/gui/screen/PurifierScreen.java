package thebetweenlands.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.PurifierMenu;

public class PurifierScreen extends AbstractContainerScreen<PurifierMenu> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/purifier.png");
	private static final ResourceLocation ARROW = TheBetweenlands.prefix("container/purifier/arrow");
	private static final ResourceLocation FLAME = TheBetweenlands.prefix("container/purifier/flame");
	private static final ResourceLocation TANK = TheBetweenlands.prefix("container/purifier/tank");

	public PurifierScreen(PurifierMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		//no-op
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

		int water = Mth.ceil(this.getMenu().getTankFill() * 64.0F) + 1;
		graphics.blitSprite(TANK, 12, 65, 0, 65 - water, i + 34, j + 10 + 65 - water, 12, water);

		if (this.getMenu().isPurifying()) {
			int arrowProgress = Mth.ceil(this.getMenu().getPurifyingProgress() * 24.0F);
			graphics.blitSprite(ARROW, 24, 16, 0, 0, i + 84, j + 34, arrowProgress, 16);

			int flameProgress = Mth.ceil(this.getMenu().getPurifyingProgress() * 11.0F);
			graphics.blitSprite(FLAME, 14, 14, 0, flameProgress, i + 62, j + 35 + flameProgress, 14, 14 - flameProgress);
		}
	}
}
