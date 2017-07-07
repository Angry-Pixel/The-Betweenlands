package thebetweenlands.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiPouchNamingButton extends GuiButton {
	public GuiPouchNamingButton(int id, int xPosition, int yPosition, int width, int height, String text) {
		super(id, xPosition, yPosition, width, height, text);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			FontRenderer fontrenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(GuiPouchNaming.GUI_TEXTURE);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			if(this.hovered) {
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 77, 46, 18);
			} else {
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 57, 46, 18);
			}
			this.mouseDragged(mc, mouseX, mouseY);
			int j = 14737632;

			if (this.packedFGColour != 0) {
				j = this.packedFGColour;
			} else if (!this.enabled) {
				j = 10526880;
			} else if (this.hovered) {
				j = 16777120;
			}

			this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 2) / 2, j);
		}
	}
}
