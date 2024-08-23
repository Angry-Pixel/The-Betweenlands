package thebetweenlands.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import thebetweenlands.api.capability.IDecayData;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.handler.PlayerDecayHandler;
import thebetweenlands.common.registries.AttachmentRegistry;

@EventBusSubscriber(modid = TheBetweenlands.ID, bus = Bus.MOD)
public class ScreenRenderHandler {

	public static final ResourceLocation DECAY_BAR = TheBetweenlands.prefix("decay_bar");

	private static final ResourceLocation DECAY_FULL_SPRITE = TheBetweenlands.prefix("hud/decay_full");
	private static final ResourceLocation DECAY_HALF_SPRITE = TheBetweenlands.prefix("hud/decay_half");
	private static final ResourceLocation DECAY_EMPTY_SPRITE = TheBetweenlands.prefix("hud/decay_empty");
	
	@SubscribeEvent
	public static void onRenderGameOverlay(RegisterGuiLayersEvent event) {
		event.registerBelow(VanillaGuiLayers.AIR_LEVEL, DECAY_BAR, ScreenRenderHandler::renderDecayBar);
	}
	
	public static Player getCameraPlayer(Minecraft mc) {
		return mc != null && mc.getCameraEntity() instanceof Player player ? player : null;
	}
	
	public static void renderDecayBar(GuiGraphics guiGraphics, DeltaTracker partialTickTracker) {
		Minecraft minecraft = Minecraft.getInstance();
        Gui gui = minecraft.gui;
        Player player = getCameraPlayer(minecraft);
        if (player != null && player.isAddedToLevel() && player.level() != null && player.hasData(AttachmentRegistry.DECAY)) {
            IDecayData data = player.getData(AttachmentRegistry.DECAY);
            
            if(!data.isDecayEnabled(player)) return;
            
            int posX = guiGraphics.guiWidth() / 2 + 91;
            int posY = guiGraphics.guiHeight() - gui.rightHeight;

            minecraft.getProfiler().push("decay");
            final int maxDecay = 20;
            int currentDecay = data.getDecayLevel(player);
            int decayBalls = maxDecay - currentDecay;
            
            RenderSystem.enableBlend();
            
            for(int i = 0; i < 10; ++i) {
                int ballX = posX - i * 8 - 9;
                guiGraphics.blitSprite(DECAY_EMPTY_SPRITE, ballX, posY, 9, 9);
                
                if (i * 2 + 1 < decayBalls) {
                    guiGraphics.blitSprite(DECAY_FULL_SPRITE, ballX, posY, 9, 9);
                }

                if (i * 2 + 1 == decayBalls) {
                    guiGraphics.blitSprite(DECAY_HALF_SPRITE, ballX, posY, 9, 9);
                }

//                if (i * 2 + 1 > decayBalls) {
//                    guiGraphics.blitSprite(DECAY_EMPTY_SPRITE, ballX, posY, 9, 9);
//                }
            }
            
            RenderSystem.disableBlend();
            gui.rightHeight += 10;

            minecraft.getProfiler().pop();
        }
	}
	
}
