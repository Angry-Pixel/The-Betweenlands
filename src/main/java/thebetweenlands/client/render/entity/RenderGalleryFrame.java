package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.gallery.GalleryEntry;
import thebetweenlands.client.handler.gallery.GalleryManager;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.EntityGalleryFrame;

@SideOnly(Side.CLIENT)
public class RenderGalleryFrame extends Render<EntityGalleryFrame> {
	public RenderGalleryFrame(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityGalleryFrame entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180 - entityYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();

		GalleryEntry entry = GalleryManager.INSTANCE.getEntries().get(entity.getUrl());

		this.bindEntityTexture(entity);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		float relWidth = 1; 
		float relHeight = 1;

		if(entry != null) {
			int maxDim = Math.max(entry.getWidth(), entry.getHeight());
			relWidth = (1.0F - (maxDim - entry.getWidth()) / (float)maxDim);
			relHeight = (1.0F - (maxDim - entry.getHeight()) / (float)maxDim);
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

		switch(entity.getType()) {
		case SMALL:

			buffer.pos(0.5D * relWidth, -0.5D * relHeight, 0).tex(0, 1).normal(0, 0, 1).endVertex();
			buffer.pos(-0.5D * relWidth, -0.5D * relHeight, 0).tex(1, 1).normal(0, 0, 1).endVertex();
			buffer.pos(-0.5D * relWidth, 0.5D * relHeight, 0).tex(1, 0).normal(0, 0, 1).endVertex();
			buffer.pos(0.5D * relWidth, 0.5D * relHeight, 0).tex(0, 0).normal(0, 0, 1).endVertex();

			break;
		case LARGE:

			buffer.pos(1.0D * relWidth, -1.0D * relHeight, 0).tex(0, 1).normal(0, 0, 1).endVertex();
			buffer.pos(-1.0D * relWidth, -1.0D * relHeight, 0).tex(1, 1).normal(0, 0, 1).endVertex();
			buffer.pos(-1.0D * relWidth, 1.0D * relHeight, 0).tex(1, 0).normal(0, 0, 1).endVertex();
			buffer.pos(1.0D * relWidth, 1.0D * relHeight, 0).tex(0, 0).normal(0, 0, 1).endVertex();

			break;
		}

		tessellator.draw();

		if(entry == null && !BetweenlandsConfig.GENERAL.onlineGallery) {
			GlStateManager.pushMatrix();

			GlStateManager.translate(0.4D, 0, -0.03F);

			GlStateManager.scale(-1 / 128.0f, -1 / 128.0f, 1 / 128.0f);
			GlStateManager.disableLighting();

			String[] notFoundLines = I18n.format("tooltip.gallery.not_found").split("\\\\n");

			int yOff = -6 * notFoundLines.length;

			for(int i = 0; i < notFoundLines.length; i++) {
				Minecraft.getMinecraft().fontRenderer.drawString(notFoundLines[i], 0, yOff, 0xFFFFFFFF);
				yOff += 12;
			}

			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGalleryFrame entity) {
		GalleryEntry entry = GalleryManager.INSTANCE.getEntries().get(entity.getUrl());
		return entry != null && entry.isUploaded() ? entry.getLocation() : TextureManager.RESOURCE_LOCATION_EMPTY;
	}
}