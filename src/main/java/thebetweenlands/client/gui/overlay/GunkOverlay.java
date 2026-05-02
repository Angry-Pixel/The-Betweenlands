package thebetweenlands.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.GunkData;
import thebetweenlands.common.registries.AttachmentRegistry;

public class GunkOverlay {

	private static final ResourceLocation GUNK_BACKGROUND_SPRITE = TheBetweenlands.prefix("hud/gunk_bar_background");
	private static final ResourceLocation GUNK_PROGRESS_SPRITE = TheBetweenlands.prefix("hud/gunk_bar_progress");

	public static void renderGunkBar(GuiGraphics graphics, DeltaTracker tracker) {
		Minecraft minecraft = Minecraft.getInstance();
		Gui gui = minecraft.gui;
		Player player = gui.getCameraPlayer();
		if (!minecraft.options.hideGui && player != null && player.hasData(AttachmentRegistry.GUNK)) {
			// Don't render gunk bar if gunk is disabled on this player
			if(!GunkData.isGunkEnabled(player)) return;
			
			GunkData gunkData = player.getData(AttachmentRegistry.GUNK);
			
			// If they are not in water and their gunk is at 0, then do not render the gunk bar
			// TODO keep gunk bar on screen for 10 ticks after this
			if(!GunkData.isGunkActive(player) && gunkData.getGunk() == 0) return;

			int posX = graphics.guiWidth() / 2 + 10;
			int posY = graphics.guiHeight() - gui.rightHeight + 2;
			
			minecraft.getProfiler().push("gunk");
			
			final int maxGunk = GunkData.GUNK_MAX;
			int currentGunk = gunkData.getGunk();

			RenderSystem.enableBlend();
			
			graphics.blitSprite(GUNK_BACKGROUND_SPRITE, posX, posY, 81, 5);
			
			if(currentGunk > 0) {
				float gunkProgress = (float)currentGunk / (float)maxGunk;
				int gunkPixels = (int)(gunkProgress * 81);
				graphics.blitSprite(GUNK_PROGRESS_SPRITE, 81, 5, 0, 0, posX, posY, 0, gunkPixels, 5);
			}

			RenderSystem.disableBlend();
			
			gui.rightHeight += 10;
			
			minecraft.getProfiler().pop();
		}
	}
}
