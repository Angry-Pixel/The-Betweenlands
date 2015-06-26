package thebetweenlands.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.lib.ModInfo;

public class GuiSmallButton extends GuiButton {
	private static final ResourceLocation TEXTURES = new ResourceLocation(ModInfo.ID, "textures/gui/widgets.png");

	private int u;
	private int v;

	public GuiSmallButton(int id, int x, int y, int u, int v) {
		super(id, x, y, 20, 20, "");
		this.u = u;
		this.v = v;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			mc.getTextureManager().bindTexture(TEXTURES);
			GL11.glColor4f(1, 1, 1, 1);
			boolean hover = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			drawTexturedModalRect(xPosition, yPosition, 0, hover ? height : 0, width, height);
			drawTexturedModalRect(xPosition, yPosition, u, v, width, height);
		}
	}
}
