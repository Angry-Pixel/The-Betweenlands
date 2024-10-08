package thebetweenlands.client.handler;

import net.minecraft.world.BossEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.api.entity.bossbar.BetweenlandsBossBar;
import thebetweenlands.api.entity.bossbar.BetweenlandsClientBossBar;

public class BossHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(BossHandler::renderCustomBossbars);
		NeoForge.EVENT_BUS.addListener(BossHandler::renderMiniBossbars);
	}

	private static void renderCustomBossbars(CustomizeGuiOverlayEvent.BossEventProgress event) {
		if (event.getBossEvent() instanceof BetweenlandsClientBossBar bar) {
			event.setCanceled(true);
			if (bar.getType() == BetweenlandsBossBar.BossType.NORMAL_BOSS) {
				bar.renderBossBar(event.getGuiGraphics(), (event.getGuiGraphics().guiWidth() / 2) - 128, event.getY());
				event.setIncrement(20);
			} else {
				event.setIncrement(0);
			}
		}
	}

	private static void renderMiniBossbars(RenderLivingEvent.Post<?, ?> event) {
		if (event.getEntity() instanceof BetweenlandsBossBar boss) {
			if (boss.getBossType() == BetweenlandsBossBar.BossType.MINI_BOSS) {
				BossEvent info = Minecraft.getInstance().gui.getBossOverlay().events.get(boss.getBar().getId());
				if (info instanceof BetweenlandsClientBossBar bar) {
					bar.renderMiniBossBar(boss, event.getPoseStack(), event.getPartialTick());
				}
			}
		}
	}
}
