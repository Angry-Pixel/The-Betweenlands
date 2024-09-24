package thebetweenlands.client.gui.book;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class HerbloreTabButton extends Button {

	private final ResourceLocation TAB_BASE_LEFT = TheBetweenlands.prefix("manual/bookmark_base_left");
	private final ResourceLocation TAB_BASE_RIGHT = TheBetweenlands.prefix("manual/bookmark_base_right");
	private final ResourceLocation TAB_COLOR_LEFT = TheBetweenlands.prefix("manual/bookmark_tab_left");
	private final ResourceLocation TAB_COLOR_RIGHT = TheBetweenlands.prefix("manual/bookmark_tab_right");

	private final boolean left;
	private final int color;
	private final DrawIcon drawIcon;

	protected HerbloreTabButton(int x, int y, boolean left, int color, DrawIcon drawIcon, OnPress onPress) {
		super(x, y, 14, 20, Component.empty(), onPress, Button.DEFAULT_NARRATION);
		this.left = left;
		this.color = color;
		this.drawIcon = drawIcon;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.pose().pushPose();
		graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		graphics.blitSprite(this.left ? TAB_BASE_LEFT : TAB_BASE_RIGHT, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		float r = (float) (this.color >> 16 & 0xff) / 255F;
		float g = (float) (this.color >> 8 & 0xff) / 255F;
		float b = (float) (color & 0xff) / 255F;
		graphics.setColor(r, g, b, this.alpha);
		graphics.blitSprite(this.left ? TAB_COLOR_LEFT : TAB_COLOR_RIGHT, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		graphics.pose().popPose();
		this.drawIcon.draw(graphics, this.getX(), this.getY());
	}

	public interface DrawIcon {
		void draw(GuiGraphics graphics, int x, int y);
	}
}
