package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.BossEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.api.entity.bossbar.BetweenlandsBoss;
import thebetweenlands.api.entity.bossbar.BetweenlandsClientBossBar;

public class BossHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(BossHandler::renderCustomBossbars);
		NeoForge.EVENT_BUS.addListener(BossHandler::renderMiniBossbars);
	}

	private static void renderCustomBossbars(CustomizeGuiOverlayEvent.BossEventProgress event) {
		if (event.getBossEvent() instanceof BetweenlandsClientBossBar bar) {
			event.setCanceled(true);
			if (bar.getType() == BetweenlandsBoss.BossType.NORMAL_BOSS) {
				bar.renderBossBar(event.getGuiGraphics(), (event.getGuiGraphics().guiWidth() / 2) - 128, event.getY());
				event.setIncrement(20);
			} else {
				event.setIncrement(0);
			}
		}
	}

	private static void renderMiniBossbars(RenderLivingEvent.Post<?, ?> event) {
		if (event.getEntity() instanceof BetweenlandsBoss boss) {
			BossEvent info = Minecraft.getInstance().gui.getBossOverlay().events.get(boss.getBossBarId());
			if (info instanceof BetweenlandsClientBossBar bar && bar.getType() == BetweenlandsBoss.BossType.MINI_BOSS) {
				bar.renderMiniBossBar(event.getPoseStack(), boss.getMiniBossTagOffset(event.getPartialTick()), boss.getMiniBossTagSize(event.getPartialTick()));
			}
		}
	}
}
