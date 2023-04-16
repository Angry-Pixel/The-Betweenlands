package thebetweenlands.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.api.entity.IInfectionBehavior;
import thebetweenlands.api.entity.IInfectionBehaviorOverlay;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.CatmullRomSpline;

public class InfectionOverlayRenderer {
	// TODO Change texture
	protected static final ResourceLocation INFECTION_OVERLAY_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/swarm_indicator_overlay.png");

	protected static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/strong_vignette.png");

	private static final CatmullRomSpline SPLINE = new CatmullRomSpline(new Vec3d[] {
			new Vec3d(0, 0, 0),
			new Vec3d(1, 0, 0),
			new Vec3d(2, 6, 0),
			new Vec3d(3, 9, 0),
			new Vec3d(4, 6, 0),
			new Vec3d(5, 0, 0),
			new Vec3d(6, 0, 0),
			new Vec3d(7, 26, 0),
			new Vec3d(8, 50, 0),
			new Vec3d(9, 80, 0),
			new Vec3d(10, 61, 0),
			new Vec3d(11, 23, 0),
			new Vec3d(12, 0, 0),
			new Vec3d(13, -24, 0),
			new Vec3d(14, -16, 0),
			new Vec3d(15, 0, 0),
			new Vec3d(16, 0, 0),
			new Vec3d(17, 0, 0),
			new Vec3d(18, 0, 0)
	});

	private float prevInfectionStrength;
	private float infectionStrength;

	private int prevPulseTicks;
	private int pulseTicks;

	private int prevPulseFadeTicks;
	private int pulseFadeTicks;

	private float prevOverlayPercentage;
	private float overlayPercentage;

	public void update() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();

		this.prevInfectionStrength = this.infectionStrength;
		this.infectionStrength = 0;

		this.prevOverlayPercentage = this.overlayPercentage;

		boolean isInfected = false;

		if(view != null) {
			IInfectionCapability cap = view.getCapability(CapabilityRegistry.CAPABILITY_INFECTION, null);

			if(cap != null) {
				this.infectionStrength = cap.getInfectionPercent();

				IInfectionBehavior behavior = cap.getCurrentInfectionBehavior();

				if(behavior instanceof IInfectionBehaviorOverlay) {
					isInfected = true;

					this.overlayPercentage = ((IInfectionBehaviorOverlay) behavior).getOverlayPercentage();
				}
			}
		}

		this.prevPulseTicks = this.pulseTicks;
		this.prevPulseFadeTicks = this.pulseFadeTicks;

		if(isInfected) {
			this.pulseFadeTicks = Math.min(20, this.pulseFadeTicks + 1);
		} else {
			this.pulseFadeTicks = Math.max(0, this.pulseFadeTicks - 1);
		}

		if(this.pulseFadeTicks > 0) {
			this.pulseTicks++;

			if((this.pulseTicks + 12) % 20 == 0) {
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.INFECTION_HEARTBEAT, 1.0f, 0.25f * this.overlayPercentage));
			}
		} else {
			this.pulseTicks = 0;
		}
	}

	public void render(float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();

		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -90);

		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

		float alpha = (this.prevInfectionStrength + (this.infectionStrength - this.prevInfectionStrength) * partialTicks) * 0.4f;

		float fade = 0.0f;

		if(this.pulseTicks > 0) {
			fade = (this.prevPulseFadeTicks + (this.pulseFadeTicks - this.prevPulseFadeTicks) * partialTicks) * 0.05f;

			fade *= this.prevOverlayPercentage + (this.overlayPercentage - this.prevOverlayPercentage) * partialTicks;

			float throb = this.prevPulseTicks + (this.pulseTicks - this.prevPulseTicks) * partialTicks;

			throb = ((float)SPLINE.interpolate((throb * 0.05f) % 1.0f).y) / 100.0f * fade;
			
			alpha = MathHelper.clamp(alpha + (1.0f - alpha) * throb, 0.0f, 1.0f);
		}

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);

		float shade = fade * 0.75f;

		GlStateManager.color(1, 1, 1, shade);

		Minecraft.getMinecraft().getTextureManager().bindTexture(VIGNETTE_TEXTURE);

		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(0.0D, (double)res.getScaledHeight_double(), 0).tex(0.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), 0).tex(1.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), 0.0D, 0).tex(1.0D, 0.0D).endVertex();
		vertexbuffer.pos(0.0D, 0.0D, 0).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();

		GlStateManager.color(1, 1, 1, alpha);

		Minecraft.getMinecraft().getTextureManager().bindTexture(INFECTION_OVERLAY_TEXTURE);

		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(0.0D, (double)res.getScaledHeight_double(), 0).tex(0.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), 0).tex(1.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), 0.0D, 0).tex(1.0D, 0.0D).endVertex();
		vertexbuffer.pos(0.0D, 0.0D, 0).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);

		GlStateManager.color(1, 1, 1, 1);

		GlStateManager.popMatrix();

		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
	}

	public void onInfectionIncrease(float amount) {
		System.out.println("GUI Infection: " + amount);
	}
}
