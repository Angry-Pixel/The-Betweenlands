package thebetweenlands.client.gui.overlay;

import java.time.LocalDateTime;
import java.time.Month;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import thebetweenlands.common.registries.DimensionRegistries;

public class AprilFoolsOverlay {

	public static final AprilFoolsOverlay INSTANCE = new AprilFoolsOverlay();
	
	public void renderAprilFools(GuiGraphics graphics, DeltaTracker tracker) {
		LocalDateTime currentDate = LocalDateTime.now();
		if(currentDate.getMonth() == Month.APRIL && currentDate.getDayOfMonth() == 1) {
			Minecraft minecraft = Minecraft.getInstance();
			if(minecraft.player == null && (minecraft.level == null || minecraft.level.dimension() != DimensionRegistries.DIMENSION_KEY)) return;
			if(minecraft.player != null && (!minecraft.player.isAddedToLevel() || minecraft.player.level().dimension() != DimensionRegistries.DIMENSION_KEY)) return;
			final Component titleComponent = Component.translatable("gui.thebetweenlands.april_fools.title");
			final Component subtitleComponent = Component.translatable("gui.thebetweenlands.april_fools.subtitle");
			Font font = minecraft.font;
			int titleWidth = font.width(titleComponent) * 2;
			int subtitleWidth = font.width(subtitleComponent);
			final int lineHeight = font.lineHeight;
			
			int offset = Math.max(titleWidth, subtitleWidth);

			graphics.pose().pushPose();
			graphics.pose().scale(2, 2, 2);
			graphics.drawString(font, titleComponent, (graphics.guiWidth() - 10 - offset) / 2, (graphics.guiHeight() - lineHeight - 10 - 2 * lineHeight) / 2, 0x80FFFFFF, false);
			graphics.pose().popPose();
			graphics.drawString(font, subtitleComponent, graphics.guiWidth() - 10 - offset, graphics.guiHeight() - lineHeight - 10, 0x80FFFFFF, false);
		}
	}
	
}
