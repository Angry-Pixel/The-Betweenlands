package thebetweenlands.client.gui.book.widget;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.BetweenlandsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AspectWidget extends BookWidget {

	public Holder<AspectType> aspect;
	public float scale = 1.0F;

	public AspectWidget(int x, int y, Holder<AspectType> aspect, float scale) {
		super(x, y, (int) (16 * scale), (int) (16 * scale));
		this.aspect = aspect;
	}

	@Override
	protected void renderBookWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		TextureAtlasSprite sprite = BetweenlandsClient.getAspectIconManager().get(this.aspect);
		graphics.blit(this.getX(), this.getY(), 0, (int) (sprite.contents().width() * this.scale), (int) (sprite.contents().height() * this.scale), sprite);
		if (this.isHovered()) {
			List<Component> tooltips = new ArrayList<>();
			tooltips.add(AspectType.getAspectName(this.aspect));
			tooltips.add(AspectType.getAspectType(this.aspect).withStyle(ChatFormatting.GRAY));
			graphics.renderTooltip(Minecraft.getInstance().font, tooltips, Optional.empty(), mouseX, mouseY);
		}
	}
}
