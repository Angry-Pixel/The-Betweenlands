package thebetweenlands.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import thebetweenlands.api.capability.IDecayData;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.AttachmentRegistry;

public class DecayBarOverlay {

	private static final ResourceLocation DECAY_FULL_SPRITE = TheBetweenlands.prefix("hud/decay_full");
	private static final ResourceLocation DECAY_HALF_SPRITE = TheBetweenlands.prefix("hud/decay_half");
	private static final ResourceLocation DECAY_EMPTY_SPRITE = TheBetweenlands.prefix("hud/decay_empty");

	public static void renderDecayBar(GuiGraphics graphics, DeltaTracker tracker) {
		Minecraft minecraft = Minecraft.getInstance();
		Gui gui = minecraft.gui;
		Player player = gui.getCameraPlayer();
		if (player != null && player.hasData(AttachmentRegistry.DECAY)) {
			IDecayData data = player.getData(AttachmentRegistry.DECAY);

			if (!data.isDecayEnabled(player)) return;

			int posX = graphics.guiWidth() / 2 + 91;
			int posY = graphics.guiHeight() - gui.rightHeight;

			minecraft.getProfiler().push("decay");
			final int maxDecay = 20;
			int currentDecay = data.getDecayLevel(player);
			int decayBalls = maxDecay - currentDecay;

			RenderSystem.enableBlend();

			boolean shake = gui.getGuiTicks() % (decayBalls * 3 + 1) == 0;

			for (int i = 0; i < 10; ++i) {
				int ballX = posX - i * 8 - 9;
				int ballY = posY;

				if(shake)
					ballY += gui.random.nextInt(3) - 1;

				graphics.blitSprite(DECAY_EMPTY_SPRITE, ballX, ballY, 9, 9);

				if (i * 2 + 1 < decayBalls) {
					graphics.blitSprite(DECAY_FULL_SPRITE, ballX, ballY, 9, 9);
				}

				if (i * 2 + 1 == decayBalls) {
					graphics.blitSprite(DECAY_HALF_SPRITE, ballX, ballY, 9, 9);
				}
			}

			RenderSystem.disableBlend();
			gui.rightHeight += 10;

			minecraft.getProfiler().pop();
		}
	}
}
