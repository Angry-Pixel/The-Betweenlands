package thebetweenlands.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.MortarMenu;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.RenderUtils;

public class MortarScreen extends AbstractContainerScreen<MortarMenu> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/pestle_and_mortar.png");
	private static final ResourceLocation PROGRESS_BAR = TheBetweenlands.prefix("container/mortar/progress_bar");

	public MortarScreen(MortarMenu menu, Inventory playerInventory, Component title) {
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

		int progress = this.getMenu().getProgress();
		graphics.blitSprite(PROGRESS_BAR, 84, 6, 0, 0, this.leftPos + 45, this.topPos + 69, progress, 6);

		if (!this.getMenu().getSlot(3).hasItem()) {
			graphics.pose().pushPose();
			graphics.pose().translate(this.leftPos, this.topPos, 0);
			RenderUtils.drawGhostItemAtSlot(graphics, new ItemStack(ItemRegistry.LIFE_CRYSTAL.get()), this.getMenu().getSlot(3));
			graphics.pose().popPose();
		}
	}
}
