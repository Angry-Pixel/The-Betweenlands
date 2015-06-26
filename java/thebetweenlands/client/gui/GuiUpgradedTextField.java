package thebetweenlands.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiUpgradedTextField extends GuiTextField {
	private FontRenderer fontRenderer;

	private String placeholder;

	public GuiUpgradedTextField(FontRenderer fontRenderer, int x, int y, int width, int height) {
		super(fontRenderer, x, y, width, height);
		this.fontRenderer = fontRenderer;
		placeholder = "";
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	@Override
	public void drawTextBox() {
		super.drawTextBox();
		if (!isFocused() && placeholder.length() > 0 && getText().length() == 0) {
            int x = getEnableBackgroundDrawing() ? xPosition + 4 : xPosition;
            int y = getEnableBackgroundDrawing() ? yPosition + (height - 8) / 2 : yPosition;
			fontRenderer.drawString(placeholder, x, y, 0x4C4C4C);
		}
	}
}
