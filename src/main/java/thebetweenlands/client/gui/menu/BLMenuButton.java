package thebetweenlands.client.gui.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.util.Mth;
import thebetweenlands.common.TheBetweenlands;

public class BLMenuButton extends Button {

	protected static final WidgetSprites SPRITES = new WidgetSprites(
		TheBetweenlands.prefix("textures/gui/menu/button.png"),
		TheBetweenlands.prefix("textures/gui/menu/button_disabled.png"),
		TheBetweenlands.prefix("textures/gui/menu/button_highlighted.png")
	);

	public BLMenuButton(Builder builder) {
		super(builder);
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();
		graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		graphics.blit(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), 0, 0, this.getWidth() / 2, this.getHeight(), 200, 20);
		graphics.blit(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 0, this.getWidth() / 2, this.getHeight(), 200, 20);
		graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		int i = this.getFGColor();
		this.renderString(graphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
	}
}
