package thebetweenlands.client.gui.overlay.swarm;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import thebetweenlands.client.audio.SwarmSoundInstance;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.SwarmedData;
import thebetweenlands.common.registries.AttachmentRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SwarmOverlay {

	public static final SwarmOverlay INSTANCE = new SwarmOverlay();
	private static final ResourceLocation SWARM_INDICATOR_OVERLAY_TEXTURE = TheBetweenlands.prefix("textures/gui/overlay/swarm_indicator_overlay.png");
	private final List<Crawler> crawlers = new ArrayList<>();
	private final RandomSource rand = RandomSource.create();
	private float prevSwarmStrength;
	private float swarmStrength;
	private int activeCrawlers;
	private int hurtTicks = 0;
	private SoundInstance swarmAttackSound = null;

	public void update() {
		Entity view = Minecraft.getInstance().gui.getCameraPlayer();

		this.prevSwarmStrength = this.swarmStrength;
		this.swarmStrength = 0;

		if (view != null) {
			SwarmedData cap = view.getData(AttachmentRegistry.SWARMED);

			this.swarmStrength = cap.getSwarmedStrength();

			if (this.swarmStrength > 0.05f) {
				SoundManager manager = Minecraft.getInstance().getSoundManager();

				if (this.swarmAttackSound == null || !manager.isActive(this.swarmAttackSound)) {
					this.swarmAttackSound = new SwarmSoundInstance();
					manager.play(this.swarmAttackSound);
				}
			}

			if (this.hurtTicks == -1 && cap.getHurtTimer() > 0) {
				this.hurtTicks = 10;
			}
		}

		int maxAge = 80;

		int maxSpawns = (int) ((this.swarmStrength <= 0.5f ? 2 * 1.0f / ((0.5f - this.swarmStrength) / 0.5f * 20 + 1) : 0.5f * ((this.swarmStrength - 0.5f) / 0.5f * 20 + 1)) * maxAge);

		int prevActiveCrawlers = this.activeCrawlers;
		this.activeCrawlers = 0;

		int exceeding = prevActiveCrawlers - maxSpawns;

		Iterator<Crawler> crawlersIt = this.crawlers.iterator();
		while (crawlersIt.hasNext()) {
			Crawler crawler = crawlersIt.next();

			crawler.update(this.hurtTicks);

			if (crawler.dead) {
				crawlersIt.remove();
			} else if (!crawler.dropping) {
				this.activeCrawlers++;

				if (crawler.updateCounter < 70 && this.rand.nextInt(20) == 0 && exceeding-- > 0) {
					crawler.dropping = true;
				}
			}
		}

		if (this.swarmStrength > 0.01f) {
			int numSpawns = this.swarmStrength <= 0.5f ? (this.rand.nextInt((int) ((0.5f - this.swarmStrength) / 0.5f * 20 + 1)) == 0 ? 1 : 0) : (int) (this.rand.nextFloat() * (this.swarmStrength - 0.5f) / 0.5f * 20) + 1;

			for (int i = 0; i < numSpawns; i++) {
				if (this.crawlers.size() < maxSpawns) {
					Window resolution = Minecraft.getInstance().getWindow();

					float margin = 20;

					float speed = (1.0f + this.rand.nextFloat() * 3.0f) * 0.002f;

					switch (this.rand.nextInt(4)) {
						case 0:
							this.crawlers.add(new Crawler(-margin, this.rand.nextFloat() * resolution.getGuiScaledHeight(), speed * resolution.getGuiScaledWidth(), 0));
							break;
						case 1:
							this.crawlers.add(new Crawler(resolution.getGuiScaledWidth() + margin, this.rand.nextFloat() * resolution.getGuiScaledHeight(), -speed * resolution.getGuiScaledWidth(), 0));
							break;
						case 2:
							this.crawlers.add(new Crawler(this.rand.nextFloat() * resolution.getGuiScaledWidth(), -margin, 0, speed * resolution.getGuiScaledHeight()));
							break;
						case 3:
							this.crawlers.add(new Crawler(this.rand.nextFloat() * resolution.getGuiScaledWidth(), resolution.getGuiScaledHeight() + margin, 0, -speed * resolution.getGuiScaledHeight()));
							break;
					}
				}
			}
		}

		if (this.hurtTicks > -1) {
			this.hurtTicks--;
		}
	}

	public void renderSwarm(GuiGraphics graphics, DeltaTracker tracker) {
		if (!this.crawlers.isEmpty()) {
			float alpha = (this.prevSwarmStrength + (this.swarmStrength - this.prevSwarmStrength) * tracker.getGameTimeDeltaPartialTick(false)) * 0.4f;
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
			graphics.blit(SWARM_INDICATOR_OVERLAY_TEXTURE, 0, 0, -90, 0.0F, 0.0F, graphics.guiWidth(), graphics.guiHeight(), graphics.guiWidth(), graphics.guiHeight());
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
			graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		}

		for (Crawler crawler : this.crawlers) {
			crawler.drawCrawler(graphics, tracker.getGameTimeDeltaPartialTick(false));
		}
	}
}
