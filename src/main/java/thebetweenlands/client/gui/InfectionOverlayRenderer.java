package thebetweenlands.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.capability.IInfectionCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class InfectionOverlayRenderer {
	// TODO Change texture
	private static final ResourceLocation INFECTION_OVERLAY_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/overlay/swarm_indicator_overlay.png");

	private float prevInfectionStrength;
	private float infectionStrength;

	public void update() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();

		this.prevInfectionStrength = this.infectionStrength;
		this.infectionStrength = 0;

		if(view != null) {
			IInfectionCapability cap = view.getCapability(CapabilityRegistry.CAPABILITY_INFECTION, null);

			if(cap != null) {
				this.infectionStrength = cap.getInfectionPercent();
			}
		}
	}

	public void render(float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();

		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -90);

		ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

		float alpha = (this.prevInfectionStrength + (this.infectionStrength - this.prevInfectionStrength) * partialTicks) * 0.4f;

		GlStateManager.color(1, 1, 1, alpha);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0);

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
