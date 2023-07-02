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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.api.entity.IInfectionBehavior;
import thebetweenlands.api.entity.IInfectionBehaviorOverlay;
import thebetweenlands.common.entity.infection.DeathInfectionBehavior;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.CatmullRomSpline;

public class InfectionOverlayRenderer {
	protected static final ResourceLocation INFECTION_OVERLAY_INNER_GRADIENT_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/infection_overlay_inner_gradient.png");
	protected static final ResourceLocation INFECTION_OVERLAY_OUTER_GRADIENT_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/infection_overlay_outer_gradient.png");
	protected static final ResourceLocation INFECTION_OVERLAY_MYCELIUM_BACK_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/infection_overlay_mycelium_back.png");
	protected static final ResourceLocation INFECTION_OVERLAY_MYCELIUM_MAIN_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/infection_overlay_mycelium_main.png");
	protected static final ResourceLocation INFECTION_OVERLAY_FULL_BACK_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/infection_overlay_full_back.png");
	protected static final ResourceLocation INFECTION_OVERLAY_FULL_MAIN_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/overlay/infection_overlay_full_main.png");

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

	private int ticks;

	private float prevInfectionStrength;
	private float infectionStrength;

	private float prevWobbleStrength;
	private float wobbleStrength;

	private int prevPulseTicks;
	private int pulseTicks;

	private int prevPulseFadeTicks;
	private int pulseFadeTicks;

	private float prevOverlayPercentage;
	private float overlayPercentage;
	
	private int tempGrowthTicks;

	private float deathProgress;
	private float prevDeathProgress;
	
	public void update() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();

		this.prevInfectionStrength = this.infectionStrength;
		this.infectionStrength = 0;

		this.prevWobbleStrength = this.wobbleStrength;

		this.prevOverlayPercentage = this.overlayPercentage;

		this.prevDeathProgress = this.deathProgress;
		
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
				
				if(behavior instanceof DeathInfectionBehavior) {
					this.deathProgress = ((DeathInfectionBehavior) behavior).getDeathProgress();
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
			this.wobbleStrength = Math.max(0.2f, this.wobbleStrength - 0.1f);

			this.pulseTicks++;

			if((this.pulseTicks + 12) % 20 == 0) {
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.INFECTION_HEARTBEAT, 1.0f, 0.25f * this.overlayPercentage));
			}
		} else {
			this.pulseTicks = 0;
			this.wobbleStrength = Math.min(1.0f, this.wobbleStrength + 0.1f);
		}

		this.ticks++;
		
		if(this.tempGrowthTicks > 0) {
			--this.tempGrowthTicks;
		}
	}

	public void render(float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();

		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -90);

		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

		float strength = (this.prevInfectionStrength + (this.infectionStrength - this.prevInfectionStrength) * partialTicks);

		float alpha = strength * 0.8f;

		float wobble = (this.prevWobbleStrength + (this.wobbleStrength - this.prevWobbleStrength) * partialTicks);

		float fade = 0.0f;

		float throb = 0;


		float tempGrowthTicks = this.tempGrowthTicks > 0 ? (1.0f - (this.tempGrowthTicks - partialTicks) / 40.0f) : 0;
		
		alpha = MathHelper.clamp(alpha + (float)Math.sin(Math.pow(tempGrowthTicks, 0.5D) * Math.PI) * 0.2f, 0.0f, 1.0f);
		
		if(this.pulseTicks > 0 || tempGrowthTicks > 0) {
			fade = (this.prevPulseFadeTicks + (this.pulseFadeTicks - this.prevPulseFadeTicks) * partialTicks) * 0.05f;
			fade *= this.prevOverlayPercentage + (this.overlayPercentage - this.prevOverlayPercentage) * partialTicks;

			throb = this.prevPulseTicks + (this.pulseTicks - this.prevPulseTicks) * partialTicks;
			throb = ((float)SPLINE.interpolate((throb * 0.05f) % 1.0f).y) / 100.0f * fade;

			alpha = MathHelper.clamp(alpha + 0.15f * throb, 0.0f, 1.0f);
		}
		
		float shade = fade * 0.75f;

		GlStateManager.color(1, 1, 1, shade);
		Minecraft.getMinecraft().getTextureManager().bindTexture(VIGNETTE_TEXTURE);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(0.0D, (double)res.getScaledHeight_double(), 0).tex(0.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), 0).tex(1.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), 0.0D, 0).tex(1.0D, 0.0D).endVertex();
		vertexbuffer.pos(0.0D, 0.0D, 0).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
		
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(INFECTION_OVERLAY_MYCELIUM_BACK_TEXTURE);
		this.drawMesh(res, 13, 5, alpha * 0.8f, (Math.cos(((this.ticks + partialTicks) * 0.138) % (Math.PI)) + 1) * 0.5D * wobble + throb * (1 - wobble), (0.03 - wobble * 0.023) * strength, 1, 1, 1, 1);
		
		double deathCurtain = this.prevDeathProgress + (this.deathProgress - this.prevDeathProgress) * partialTicks;
		
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(INFECTION_OVERLAY_FULL_BACK_TEXTURE);
		this.drawMesh(res, 13, 20, deathCurtain * 1.2, 0.0f, 0.0f, 1, 1, 1, 1);

		GlStateManager.color(1, 1, 1, MathHelper.clamp(alpha + (1.0f - alpha) * throb, 0.0f, 1.0f));
		Minecraft.getMinecraft().getTextureManager().bindTexture(INFECTION_OVERLAY_INNER_GRADIENT_TEXTURE);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(0.0D, (double)res.getScaledHeight_double(), 0).tex(0.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), 0).tex(1.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), 0.0D, 0).tex(1.0D, 0.0D).endVertex();
		vertexbuffer.pos(0.0D, 0.0D, 0).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
		
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(INFECTION_OVERLAY_FULL_MAIN_TEXTURE);
		this.drawMesh(res, 13, 20, deathCurtain * 1.2, (Math.cos(((this.ticks + partialTicks) * 0.1) % (Math.PI)) + 1) * 0.5D * wobble + throb * (1 - wobble), (0.03 - wobble * 0.023) * strength, 1, 1, 1, 1);
		
		GlStateManager.color(1, 1, 1, 1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(INFECTION_OVERLAY_MYCELIUM_MAIN_TEXTURE);
		this.drawMesh(res, 13, 5, alpha * 0.8f, (Math.cos(((this.ticks + partialTicks) * 0.1) % (Math.PI)) + 1) * 0.5D * wobble + throb * (1 - wobble), (0.03 - wobble * 0.023) * strength, 1, 1, 1, 1);
		
		GlStateManager.color(1, 1, 1, alpha);
		Minecraft.getMinecraft().getTextureManager().bindTexture(INFECTION_OVERLAY_OUTER_GRADIENT_TEXTURE);
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(0.0D, (double)res.getScaledHeight_double(), 0).tex(0.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), 0).tex(1.0D, 1.0D).endVertex();
		vertexbuffer.pos((double)res.getScaledWidth_double(), 0.0D, 0).tex(1.0D, 0.0D).endVertex();
		vertexbuffer.pos(0.0D, 0.0D, 0).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
		
		GlStateManager.disableTexture2D();
		this.drawMesh(res, 13, 20, deathCurtain * 1.2, 0.0f, 0.0f, 0, 0, 0, (float)deathCurtain * 0.9f);
		GlStateManager.enableTexture2D();

		GlStateManager.popMatrix();

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
	}

	private void drawMesh(ScaledResolution res, int radialSegments, int lateralSegments, double coverage, double wobble, double wobbleAmount, float r, float g, float b, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();

		double w = res.getScaledWidth_double();
		double h = res.getScaledHeight_double();

		double cx = w * 0.5D;
		double cy = h * 0.5D;

		double max = Math.max(w, h);

		double sx = w / max;
		double sy = h / max;

		double radius = max * 0.5D * Math.sqrt(2) + 50;

		vertexbuffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);

		for(int j = 0; j < lateralSegments; ++j) {
			double ir = j / (double)(lateralSegments) * radius;
			double or = ir + radius / (double)(lateralSegments);

			for(int i = 0; i < radialSegments; ++i) {
				double angle = i / (double)(radialSegments-1) * Math.PI * 2;

				double cos = Math.cos(-angle);
				double sin = Math.sin(-angle);

				double ipx = cos * ir * sx;
				double ipy = sin * ir * sy;

				double opx = cos * or * sx;
				double opy = sin * or * sy;

				ipx += cx;
				ipy += cy;

				opx += cx;
				opy += cy;

				double iu = ipx / w;
				double iv = ipy / h;

				double ou = opx / w;
				double ov = opy / h;

				double iou = iu - 0.5D;
				double iov = iv - 0.5D;

				double oou = ou - 0.5D;
				double oov = ov - 0.5D;

				double ic = MathHelper.clamp(Math.abs(Math.sqrt(iou * iou + iov * iov) - wobble) * 2.0, 0.0, 0.5) * -wobbleAmount;
				double oc = MathHelper.clamp(Math.abs(Math.sqrt(oou * oou + oov * oov) - wobble) * 2.0, 0.0, 0.5) * -wobbleAmount;

				iu += ic * iou;
				iv += ic * iov;
				ou += oc * oou;
				ov += oc * oov;

				double frs = (1 - coverage) * radius;
				double fre = frs + radius / 6.0f;

				float ia = alpha * (float) MathHelper.clamp((ir - frs) / (fre - frs), 0, 1);
				float oa = alpha * (float) MathHelper.clamp((or - frs) / (fre - frs), 0, 1);

				vertexbuffer.pos(ipx, ipy, 0).tex(iu, iv).color(r, g, b, ia).endVertex();
				vertexbuffer.pos(opx, opy, 0).tex(ou, ov).color(r, g, b, oa).endVertex();
			}
		}

		tessellator.draw();
	}

	public void onInfectionIncrease(float amount) {
		if(this.tempGrowthTicks <= 0) {
			this.tempGrowthTicks = 40;
		}
		
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null) {
			player.world.playSound(player, player.posX, player.posY, player.posZ, SoundRegistry.INFECTION_SPREAD, SoundCategory.PLAYERS, 0.75f + 1.0f * MathHelper.clamp(amount * 10.0f, 0.0f, 1.0f), 1);
		}
	}
}
