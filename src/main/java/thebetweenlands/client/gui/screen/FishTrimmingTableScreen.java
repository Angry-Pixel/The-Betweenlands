package thebetweenlands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.FishTrimmingTableBlockEntity;
import thebetweenlands.common.inventory.FishTrimmingTableMenu;
import thebetweenlands.common.network.ChopFishPacket;

public class FishTrimmingTableScreen extends AbstractContainerScreen<FishTrimmingTableMenu> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/fish_trimming_table.png");
	private final FishTrimmingTableMenu menu;

	public FishTrimmingTableScreen(FishTrimmingTableMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.menu = menu;
		this.imageWidth = 176;
		this.imageHeight = 226;
	}

	@Override
	protected void init() {
		super.init();
		this.addRenderableWidget(Button.builder(Component.translatable("container.thebetweenlands.fish_trimming_table.butcher"), button -> PacketDistributor.sendToServer(ChopFishPacket.INSTANCE)).bounds(this.leftPos + 48, this.topPos + 111, 80, 20).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, 11, 4210752, false);
		graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.imageHeight - 93, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

		if (this.menu.getContainer() instanceof FishTrimmingTableBlockEntity table && table.getStoredRecipe() != null && table.hasChopper() && table.allResultSlotsEmpty()) {
			graphics.pose().pushPose();
			graphics.pose().translate(this.leftPos, this.topPos, 0);
			this.drawSlotAsBackground(graphics, table.getSlotResult(Minecraft.getInstance().level, 1, 0), this.menu.getSlot(1));
			this.drawSlotAsBackground(graphics, table.getSlotResult(Minecraft.getInstance().level, 2, 0), this.menu.getSlot(2));
			this.drawSlotAsBackground(graphics, table.getSlotResult(Minecraft.getInstance().level, 3, 0), this.menu.getSlot(3));
			graphics.pose().popPose();
		}
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
