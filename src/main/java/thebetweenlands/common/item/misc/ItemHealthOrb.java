package thebetweenlands.common.item.misc;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.lib.ModInfo;

public class ItemHealthOrb extends Item {
	protected static final ResourceLocation TEXTURE_ORB = new ResourceLocation(ModInfo.ID, "textures/gui/health_orb.png");
	protected static final ResourceLocation TEXTURE_ORB_LIQUID = new ResourceLocation(ModInfo.ID, "textures/gui/health_orb_liquid.png");
	protected static final ResourceLocation TEXTURE_ORB_LIQUID_TOP = new ResourceLocation(ModInfo.ID, "textures/gui/health_orb_liquid_top.png");
	protected static final ResourceLocation TEXTURE_ORB_LIQUID_MASK = new ResourceLocation(ModInfo.ID, "textures/gui/health_orb_liquid_mask.png");

	public ItemHealthOrb() {
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderLiving(RenderLivingEvent.Specials.Post<EntityLivingBase> event) {
		if(true) return; //TODO Implement health orb
		
		EntityLivingBase entity = event.getEntity();
		RenderManager renderManager = event.getRenderer().getRenderManager();
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

		Tessellator tessellator = Tessellator.getInstance();

		float viewerYaw = renderManager.playerViewY;
		float viewerPitch = renderManager.playerViewX;
		boolean isThirdPersonFrontal = renderManager.options.thirdPersonView == 2;

		double x = event.getX();
		double y = event.getY() + entity.height + 0.5F;
		double z = event.getZ();

		double emptyPercentage = (Math.sin((entity.ticksExisted + event.getPartialRenderTick()) / 10.0F) + 1) / 2 - 0.1D;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		GlStateManager.depthMask(false);

		boolean useStencil = false;
		int stencilBit = MinecraftForgeClient.reserveStencilBit();
		int stencilMask = 1 << stencilBit;

		if(stencilBit >= 0) {
			useStencil = fbo.isStencilEnabled() ? true : fbo.enableStencil();
		}

		double width = 0.33D;
		double height = 0.33D;

		if(useStencil) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);

			//Clear our stencil bit to 0
			GL11.glStencilMask(stencilMask);
			GL11.glClearStencil(0);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
			GL11.glStencilMask(~0);

			GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

			GlStateManager.colorMask(false, false, false, false);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

			textureManager.bindTexture(TEXTURE_ORB_LIQUID_MASK);
			renderOrbQuad(tessellator, -width, -height, width, height, 0, 0, 1, 1);

			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

			GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		}

		renderOrb(tessellator, fbo, textureManager, width, height, emptyPercentage, useStencil);

		GlStateManager.depthMask(true);
		GlStateManager.colorMask(false, false, false, false);

		renderOrb(tessellator, fbo, textureManager, width, height, emptyPercentage, useStencil);

		if(stencilBit >= 0) {
			MinecraftForgeClient.releaseStencilBit(stencilBit);
		}
		if(useStencil) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	private static void renderOrb(Tessellator tessellator, Framebuffer fbo, TextureManager textureManager, double width, double height, double emptyPercentage, boolean useStencil) {
		if(useStencil) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);
		}

		textureManager.bindTexture(TEXTURE_ORB_LIQUID);
		renderOrbQuad(tessellator, -width, -height - 0.05D, width, height - height * 2 * emptyPercentage - 0.05D, 0, emptyPercentage, 1, 1);

		textureManager.bindTexture(TEXTURE_ORB_LIQUID_TOP);
		renderOrbQuad(tessellator, -width, -height, width, height - height * 2 * emptyPercentage, 0, 0, 1, 1 - emptyPercentage);

		if(useStencil) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

		textureManager.bindTexture(TEXTURE_ORB);
		renderOrbQuad(tessellator, -width, -height, width, height, 0, 0, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	private static void renderOrbQuad(Tessellator tessellator, double minX, double minY, double maxX, double maxY, double minU, double minV, double maxU, double maxV) {
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		buffer.pos(minX, minY, 0.0D).tex(minU, maxV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		buffer.pos(minX, maxY, 0.0D).tex(minU, minV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		buffer.pos(maxX, maxY, 0.0D).tex(maxU, minV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		buffer.pos(maxX, minY, 0.0D).tex(maxU, maxV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
	}
}
