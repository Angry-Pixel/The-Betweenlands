package thebetweenlands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.DruidAltarMenu;
import thebetweenlands.common.registries.ItemRegistry;

public class DruidAltarScreen extends AbstractContainerScreen<DruidAltarMenu> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/druid_altar.png");
	private static final Item[] PIECES = new Item[] {
		ItemRegistry.SWAMP_TALISMAN_PIECE_1.get(),
		ItemRegistry.SWAMP_TALISMAN_PIECE_2.get(),
		ItemRegistry.SWAMP_TALISMAN_PIECE_3.get(),
		ItemRegistry.SWAMP_TALISMAN_PIECE_4.get()
	};
	private int iconCountTool = 1;

	public DruidAltarScreen(DruidAltarMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		if (this.minecraft.level.getGameTime() % 40 == 0) {
			this.iconCountTool++;
			if (this.iconCountTool > 4) {
				this.iconCountTool = 1;
			}
		}
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		//no-op
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

		graphics.pose().pushPose();
		graphics.pose().translate(this.leftPos, this.topPos, 0);
		for (int slot = 1; slot < 5; slot++) {
			if (!this.getMenu().getSlot(slot).hasItem()) {
				this.drawSlotAsBackground(graphics, new ItemStack(PIECES[(slot + this.iconCountTool) % 4]), this.getMenu().getSlot(slot));
			}
		}
		graphics.pose().popPose();
	}

	private void drawSlotAsBackground(GuiGraphics graphics, ItemStack stack, Slot slot) {
		graphics.renderFakeItem(stack, slot.x, slot.y);

		// draw 50% gray rectangle over the item
		RenderSystem.disableDepthTest();
		graphics.pose().pushPose();
		graphics.pose().translate(0.0D, 0.0D, 200.0D);
		graphics.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x9f8b8b8b);
		graphics.pose().popPose();
		RenderSystem.enableDepthTest();
		graphics.renderItemDecorations(this.font, stack, slot.x, slot.y);
	}
}
