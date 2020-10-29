package thebetweenlands.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.capability.ISwarmedCapability;
import thebetweenlands.client.audio.SwarmAttackSound;
import thebetweenlands.common.registries.CapabilityRegistry;

public class SwarmOverlayRenderer {
	private static final ResourceLocation SWARM_INDICATOR_OVERLAY_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/overlay/swarm_indicator_overlay.png");

	private final List<GuiCrawler> crawlers = new ArrayList<>();

	private Random rand = new Random();

	private float prevSwarmStrength;
	private float swarmStrength;

	private int activeCrawlers;

	private int hurtTicks = 0;

	private ISound swarmAttackSound = null;

	public void update() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();

		this.prevSwarmStrength = this.swarmStrength;
		this.swarmStrength = 0;

		if(view != null) {
			ISwarmedCapability cap = view.getCapability(CapabilityRegistry.CAPABILITY_SWARMED, null);

			if(cap != null) {
				this.swarmStrength = cap.getSwarmedStrength();

				if(this.swarmStrength > 0.05f) {
					SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();

					if(this.swarmAttackSound == null || !soundHandler.isSoundPlaying(this.swarmAttackSound)) {
						this.swarmAttackSound = new SwarmAttackSound();
						soundHandler.playSound(this.swarmAttackSound);
					}
				}

				if(this.hurtTicks == -1 && cap.getHurtTimer() > 0) {
					this.hurtTicks = 10;
				}
			}
		}

		int maxAge = 80;

		int maxSpawns = (int)((this.swarmStrength <= 0.5f ? 2 * 1.0f / ((0.5f - this.swarmStrength) / 0.5f * 20 + 1) : 0.5f * ((this.swarmStrength - 0.5f) / 0.5f * 20 + 1)) * maxAge);

		int prevActiveCrawlers = this.activeCrawlers;
		this.activeCrawlers = 0;

		int exceeding = prevActiveCrawlers - maxSpawns;

		Iterator<GuiCrawler> crawlersIt = this.crawlers.iterator();
		while(crawlersIt.hasNext()) {
			GuiCrawler crawler = crawlersIt.next();

			crawler.update(this.hurtTicks);

			if(crawler.dead) {
				crawlersIt.remove();
			} else if(!crawler.dropping) {
				this.activeCrawlers++;

				if(crawler.updateCounter < 70 && this.rand.nextInt(20) == 0 && exceeding-- > 0) {
					crawler.dropping = true;
				}
			}
		}

		if(this.swarmStrength > 0.01f) {
			int numSpawns = this.swarmStrength <= 0.5f ? (this.rand.nextInt((int)((0.5f - this.swarmStrength) / 0.5f * 20 + 1)) == 0 ? 1 : 0) : (int)(this.rand.nextFloat() * (this.swarmStrength - 0.5f) / 0.5f * 20) + 1;

			for(int i = 0; i < numSpawns; i++) {
				if(this.crawlers.size() < maxSpawns) {
					ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());

					float margin = 20;

					float speed = (1.0f + this.rand.nextFloat() * 3.0f) * 0.002f;

					switch(this.rand.nextInt(4)) {
					case 0:
						this.crawlers.add(new GuiCrawler(-margin, this.rand.nextFloat() * resolution.getScaledHeight(), speed * resolution.getScaledWidth(), 0));
						break;
					case 1:
						this.crawlers.add(new GuiCrawler(resolution.getScaledWidth() + margin, this.rand.nextFloat() * resolution.getScaledHeight(), -speed * resolution.getScaledWidth(), 0));
						break;
					case 2:
						this.crawlers.add(new GuiCrawler(this.rand.nextFloat() * resolution.getScaledWidth(), -margin, 0, speed * resolution.getScaledHeight()));
						break;
					case 3:
						this.crawlers.add(new GuiCrawler(this.rand.nextFloat() * resolution.getScaledWidth(), resolution.getScaledHeight() + margin, 0, -speed * resolution.getScaledHeight()));
						break;
					}
				}
			}
		}

		if(this.hurtTicks > -1) {
			this.hurtTicks--;
		}
	}

	public void render(float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();

		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -100);

		if(this.crawlers.size() > 0) {
			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

			float alpha = (this.prevSwarmStrength + (this.swarmStrength - this.prevSwarmStrength) * partialTicks) * 0.4f;

			GlStateManager.color(1, 1, 1, alpha);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0);

			//Indicator overlay
			Minecraft.getMinecraft().getTextureManager().bindTexture(SWARM_INDICATOR_OVERLAY_TEXTURE);
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
			vertexbuffer.pos(0.0D, (double)res.getScaledHeight_double(), 0).tex(0.0D, 1.0D).endVertex();
			vertexbuffer.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), 0).tex(1.0D, 1.0D).endVertex();
			vertexbuffer.pos((double)res.getScaledWidth_double(), 0.0D, 0).tex(1.0D, 0.0D).endVertex();
			vertexbuffer.pos(0.0D, 0.0D, 0).tex(0.0D, 0.0D).endVertex();
			tessellator.draw();

			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
		}

		GlStateManager.color(1, 1, 1, 1);

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

		for(GuiCrawler crawler : this.crawlers) {
			crawler.drawCrawler(Minecraft.getMinecraft(), vertexbuffer, partialTicks);
		}

		tessellator.draw();

		GlStateManager.popMatrix();

		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
	}
}
