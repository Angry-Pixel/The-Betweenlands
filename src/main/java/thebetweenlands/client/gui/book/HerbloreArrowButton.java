package thebetweenlands.client.gui.book;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class HerbloreArrowButton extends Button {

	private static final ArrowSprites ARROW = new ArrowSprites(
		TheBetweenlands.prefix("manual/left_arrow"),
		TheBetweenlands.prefix("manual/left_arrow_highlighted"),
		TheBetweenlands.prefix("manual/right_arrow"),
		TheBetweenlands.prefix("manual/right_arrow_highlighted")
	);
	private final boolean left;

	public HerbloreArrowButton(int x, int y, boolean left, OnPress onPress) {
		super(x, y, 19, 9, Component.empty(), onPress, Button.DEFAULT_NARRATION);
		this.left = left;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		graphics.blitSprite(ARROW.get(this.left, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public record ArrowSprites(ResourceLocation left, ResourceLocation leftFocused, ResourceLocation right, ResourceLocation rightFocused) {

		public ResourceLocation get(boolean left, boolean focused) {
			if (left) {
				return focused ? this.leftFocused : this.left;
			} else {
				return focused ? this.rightFocused : this.right;
			}
		}
	}
}
