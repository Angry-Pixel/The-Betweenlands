package thebetweenlands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.serverbound.RenameItemPacket;

public class ItemRenameScreen extends Screen {

	private static final ResourceLocation BASE_TEXTURE = TheBetweenlands.prefix("textures/gui/item_renaming.png");
	private static final ResourceLocation[] BUTTON_TEXTURE = new ResourceLocation[] {
		TheBetweenlands.prefix("container/item_naming/save_button"),
		TheBetweenlands.prefix("container/item_naming/save_button_selected")
	};
	private int leftPos;
	private int topPos;
	private EditBox name;

	public ItemRenameScreen(Component title) {
		super(title);
	}

	@Override
	protected void init() {
		this.leftPos = (this.width - 181) / 2;
		this.topPos = (this.height - 55) / 2;
		this.addRenderableWidget(new RenameButton(this.leftPos, this.topPos - 18, button -> this.onClose()));
		this.name = new EditBox(this.font, this.leftPos + 22, this.topPos + 17, 136, 20, Component.empty());
		this.name.setMaxLength(20);
		this.name.setFocused(false);
		this.name.setTextColor(5635925);
		ItemStack stack = Minecraft.getInstance().player.getInventory().getSelected();
		this.name.setValue(stack.getOrDefault(DataComponents.CUSTOM_NAME, Component.translatable(stack.getDescriptionId())).getString());
		this.addRenderableWidget(this.name);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		String s = this.name.getValue();
		this.init(minecraft, width, height);
		this.name.setValue(s);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		graphics.blit(BASE_TEXTURE, this.leftPos, this.topPos, 0, 0, 181, 55, 256, 64);
		this.name.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	public void onClose() {
		PacketDistributor.sendToServer(new RenameItemPacket(this.name.getValue()));
		super.onClose();
	}

	private static class RenameButton extends Button {
		protected RenameButton(int x, int y, OnPress press) {
			super(x, y, 46, 18, Component.literal("Save"), press, Button.DEFAULT_NARRATION);
		}

		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			Minecraft minecraft = Minecraft.getInstance();
			guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			guiGraphics.blitSprite(BUTTON_TEXTURE[this.isHoveredOrFocused() ? 1 : 0], this.getX(), this.getY(), this.getWidth(), this.getHeight());
			guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			int j = 14737632;

			if (this.getFGColor() != 0) {
				j = this.getFGColor();
			} else if (!this.isActive()) {
				j = 10526880;
			} else if (this.isHoveredOrFocused()) {
				j = 16777120;
			}

			int x = this.getX() + 2;
			int y = this.getX() + this.getWidth() - 2;
			renderScrollingString(guiGraphics, minecraft.font, this.getMessage(), x, this.getY(), y, this.getY() + this.getHeight() + 5, j | Mth.ceil(this.alpha * 255.0F) << 24);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
