package thebetweenlands.client.render.entity.layer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class LayerAnimatedOverlay<T extends EntityLivingBase> implements LayerRenderer<T> {
	public final RenderLivingBase<T> renderer;
	public final ResourceLocation overlay;

	public LayerAnimatedOverlay(RenderLivingBase<T> renderer, ResourceLocation overlay) {
		this.renderer = renderer;
		this.overlay = overlay;
	}

	@Override
	public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(!entity.isInvisible());

		float scrollTimer = entity.ticksExisted + partialTicks;
		this.renderer.bindTexture(this.overlay);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		float yScroll = scrollTimer * 0.002F;
		GlStateManager.translate(0F, -yScroll, 0.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		float colour = 0.5F;
		GlStateManager.color(colour, colour, colour, 1.0F);

		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(0.0F, -5.0F);
		
		ModelBase mainModel = this.renderer.getMainModel();
		mainModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
		mainModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

		GlStateManager.disablePolygonOffset();
		
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
