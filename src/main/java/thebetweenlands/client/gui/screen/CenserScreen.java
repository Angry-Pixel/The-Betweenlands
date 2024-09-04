package thebetweenlands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.CenserMenu;

import java.util.List;

public class CenserScreen extends AbstractContainerScreen<CenserMenu> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/censer.png");
	private static final ResourceLocation FOG = TheBetweenlands.prefix("container/censer/fog");
	private static final ResourceLocation FLAME = TheBetweenlands.prefix("container/censer/flame");
	private static final ResourceLocation INTERNAL_SLOT_COVER = TheBetweenlands.prefix("container/censer/cover");
	private static final ResourceLocation FLUID_SLOT_COVER = TheBetweenlands.prefix("container/censer/fluid_slot_cover");

	public CenserScreen(CenserMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageHeight = 256;
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

		if (this.getMenu().isLit()) {
			this.renderBurn(graphics);
		}

		if (this.getMenu().shouldRenderCover()) {
			graphics.blitSprite(INTERNAL_SLOT_COVER, this.leftPos + 79, this.topPos + 102, 18, 18);
		} else {
			this.renderFog(graphics);
			this.renderLiquid(graphics);
		}
	}

	private void renderFog(GuiGraphics graphics) {
		int maxBarHeight = 84;
		int barHeight = Mth.ceil(this.getMenu().getFogProgress() * maxBarHeight);
		int barHeightBackground = Math.min(barHeight + 1, maxBarHeight);
		int barHeightBackground2 = Math.min(barHeight + 2, maxBarHeight);
		int barHeightBackground3 = Math.min(barHeight + 3, maxBarHeight);

		this.drawGradiantTexture(graphics, FOG, 47, 84, 0, maxBarHeight - barHeightBackground3, this.leftPos + 67, this.topPos + 18 + maxBarHeight - barHeightBackground3, 47, barHeightBackground3, 0xFF202020, 0xFF202020);
		this.drawGradiantTexture(graphics, FOG, 47, 84, 0, maxBarHeight - barHeightBackground2, this.leftPos + 67, this.topPos + 18 + maxBarHeight - barHeightBackground2, 47, barHeightBackground2, 0xFF505050, 0xFF505050);
		this.drawGradiantTexture(graphics, FOG, 47, 84, 0, maxBarHeight - barHeightBackground, this.leftPos + 67, this.topPos + 18 + maxBarHeight - barHeightBackground, 47, barHeightBackground, 0xFF808080, 0xFF808080);
		this.drawGradiantTexture(graphics, FOG, 47, 84, 0, maxBarHeight - barHeight, this.leftPos + 67, this.topPos + 18 + maxBarHeight - barHeight, 47, barHeight, this.getMenu().getEffectColor(), 0xFFFFFFFF);
	}

	private void renderBurn(GuiGraphics graphics) {
		int maxBarHeight = 14;
		int barHeight = Mth.ceil(this.getMenu().getBurnProgress() * maxBarHeight);
		graphics.blitSprite(FLAME, 14, 14, 0, maxBarHeight - barHeight, this.leftPos + 81, this.topPos + 122 + maxBarHeight - barHeight, 14, barHeight);
	}

	private void renderLiquid(GuiGraphics graphics) {
		var sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(BuiltInRegistries.FLUID.getKey(this.getMenu().getTankFluid().getFluid()));

		if (sprite != null && !this.getMenu().getTankFluid().isEmpty()) {
			graphics.blit(this.leftPos + 80, this.topPos + 103, 0, 16, 16, sprite);
			graphics.blitSprite(FLUID_SLOT_COVER, this.leftPos + 77, this.topPos + 100, 22, 22);
		}
	}

	@Override
	protected void renderTooltip(GuiGraphics graphics, int x, int y) {
		if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
			ItemStack itemstack = this.hoveredSlot.getItem();
			graphics.renderTooltip(this.font, this.possiblyExpandTooltip(this.hoveredSlot), itemstack.getTooltipImage(), itemstack, x, y);
		}
	}


	protected List<Component> possiblyExpandTooltip(Slot slot) {
		List<Component> tooltip = super.getTooltipFromContainerItem(slot.getItem());
		tooltip.addAll(this.getMenu().getExtraTooltips(slot));
		return tooltip;
	}

	private void drawGradiantTexture(GuiGraphics graphics, ResourceLocation texture, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int uWidth, int vHeight, int startColor, int endColor) {
		TextureAtlasSprite sprite = Minecraft.getInstance().getGuiSprites().getSprite(texture);

		float minU = sprite.getU((float) uPosition / (float) textureWidth);
		float maxU = sprite.getU((float) (uPosition + uWidth) / (float) textureWidth);
		float minV = sprite.getV((float) vPosition / (float) textureHeight);
		float maxV = sprite.getV((float) (vPosition + vHeight) / (float) textureHeight);

		RenderSystem.setShaderTexture(0, sprite.atlasLocation());
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		Matrix4f matrix4f = graphics.pose().last().pose();
		BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferbuilder.addVertex(matrix4f, x, y, 0).setUv(minU, minV).setColor(startColor);
		bufferbuilder.addVertex(matrix4f, x, y + vHeight, 0).setUv(minU, maxV).setColor(endColor);
		bufferbuilder.addVertex(matrix4f, x + uWidth, y + vHeight, 0).setUv(maxU, maxV).setColor(endColor);
		bufferbuilder.addVertex(matrix4f, x + uWidth, y, 0).setUv(maxU, minV).setColor(startColor);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
	}
}
