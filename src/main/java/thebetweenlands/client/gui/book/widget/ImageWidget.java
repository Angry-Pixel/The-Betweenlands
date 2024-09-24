package thebetweenlands.client.gui.book.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ImageWidget extends BookWidget {

	private final ResourceLocation texture;

	public ImageWidget(int x, int y, int width, int height, ResourceLocation texture) {
		super(x, y, width, height);
		this.texture = texture;
	}

	@Override
	protected void renderBookWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		RenderSystem.enableBlend();
		graphics.blit(this.texture, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY) {
		return false;
	}
}
