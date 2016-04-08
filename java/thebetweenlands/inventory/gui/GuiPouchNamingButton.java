package thebetweenlands.inventory.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiPouchNamingButton extends GuiButton {
	private static final ResourceLocation GUI_POUCH_NAMING = new ResourceLocation("thebetweenlands:textures/gui/lurkerPouchNamingGui.png");

	public GuiPouchNamingButton(int id, int xPosition, int yPosition, int width, int height, String text) {
		super(id, xPosition, yPosition, width, height, text);
	}

	public void drawButton(Minecraft mc, int x, int y) {
		if (visible) {
			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(GUI_POUCH_NAMING);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_146123_n = x >= xPosition && y >= yPosition && x < xPosition + width && y < yPosition + height;
			int k = getHoverState(field_146123_n);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			if(k != 1)
				drawTexturedModalRect(xPosition, yPosition, 0, 77, 46, 18);
			else
				drawTexturedModalRect(xPosition, yPosition, 0, 57, 46, 18);
			mouseDragged(mc, x, y);
			int l = 14737632;

			if (packedFGColour != 0)
				l = packedFGColour;
			else if (!enabled)
				l = 10526880;
			else if (field_146123_n)
				l = 16777120;

			drawCenteredString(fontrenderer, displayString, xPosition + width / 2, yPosition + (height - 2) / 2, l);
		}
	}
}
