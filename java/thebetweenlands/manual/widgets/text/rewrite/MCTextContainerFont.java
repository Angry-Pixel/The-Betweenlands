package thebetweenlands.manual.widgets.text.rewrite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class MCTextContainerFont implements ITextContainerFont {
	private final FontRenderer font = Minecraft.getMinecraft().fontRenderer;

	@Override
	public double getWidth(String text) {
		return this.font.getStringWidth(text);
	}

	@Override
	public double getHeight(String text) {
		return this.font.FONT_HEIGHT;
	}

	@Override
	public void render(String text, Object... args) {
		this.font.drawString(text, 0, 0, 0xFFFF0000);
	}
}
