package thebetweenlands.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.BarrelMenu;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BarrelScreen extends AbstractContainerScreen<BarrelMenu> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/barrel.png");
	private static final ResourceLocation BARREL_COVER_TEXTURE = TheBetweenlands.prefix("container/barrel/barrel_cover");
	private static final ResourceLocation BUCKET_TEXTURE = TheBetweenlands.prefix("container/barrel/bucket");
	private static final ResourceLocation BUCKET_COVER_TEXTURE = TheBetweenlands.prefix("container/barrel/bucket_cover");

	private final DecimalFormat numberFormat;

	public BarrelScreen(BarrelMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.numberFormat = new DecimalFormat();
		this.numberFormat.setMinimumFractionDigits(1);
		this.numberFormat.setMaximumFractionDigits(1);
		this.numberFormat.setRoundingMode(RoundingMode.DOWN);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
		if (this.isHovering(69, 16, 37, 54, mouseX, mouseY)) {
			FluidStack stack = this.getMenu().getTank().getFluid();

			if (!stack.isEmpty()) {
				graphics.renderTooltip(this.font, Component.translatable("block.thebetweenlands.barrel.fluid", stack.getHoverName(), stack.getAmount()), mouseX, mouseY);
			}
		}
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		//no-op
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
		graphics.blitSprite(BUCKET_TEXTURE, i + 140, j + 16, 16, 16);

		if (!this.getMenu().getTank().isEmpty()) {
			FluidStack stack = this.getMenu().getTank().getFluid();
			int barHeight = Mth.ceil(57 * stack.getAmount() / (float) this.getMenu().getTank().getCapacity());
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture());
			int color = IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(stack) | 0xFF000000;

			this.renderTank(graphics, i + 64, j + 71, sprite, 47, 57, barHeight, color);
			this.renderTank(graphics, i + 140, j + 32, sprite, 16, 16, 16, color);

			graphics.blitSprite(BARREL_COVER_TEXTURE, i + 64, j + 12, 47, 62);
			graphics.blitSprite(BUCKET_COVER_TEXTURE, i + 140, j + 16, 16, 16);
			graphics.drawString(this.font, this.numberFormat.format(stack.getAmount() / 1000.0F), i + 127, j + 26, 0xFFFFFFFF, false);
		}
	}

	protected void renderTank(GuiGraphics graphics, int x, int y, TextureAtlasSprite sprite, int width, int height, int fullHeight, int color) {
		if (height <= 0) {
			return;
		}

		int yStart = 0;
		while (yStart < fullHeight) {
			int segmentHeight = Math.min(yStart + height, fullHeight) - yStart;
			float fraction = segmentHeight / (float) height;
			int yOff = -yStart - (int) (height * fraction);
			float r = ((color >> 16) & 0xFF) / 255.0f;
			float g = ((color >> 8) & 0xFF) / 255.0f;
			float b = ((color) & 0xFF) / 255.0f;
			float a = ((color >> 24) & 0xFF) / 255.0f;

			graphics.blit(x, yOff + y, 0, width, (int)(height * fraction), sprite, r, g, b, a);
			yStart += height;
		}
	}
}
