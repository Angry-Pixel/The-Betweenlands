package thebetweenlands.client.render.entity.layer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * Adds a simple glow layer to the renderer

 * @param <T>
 */
public class LayerGlow<T extends EntityLivingBase> implements LayerRenderer<T> {
	public final RenderLivingBase<T> renderer;
	public final ResourceLocation glowTexture;

	private float r = 1.0F, g = 1.0F, b = 1.0F;
	private float alpha = 1.0F;

	public LayerGlow(RenderLivingBase<T> renderer, ResourceLocation glowTexture) {
		this.renderer = renderer;
		this.glowTexture = glowTexture;
	}

	@Override
	public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.renderer.bindTexture(this.glowTexture);
		ModelBase mainModel = this.renderer.getMainModel();

		float alpha = this.getAlpha();

		GlStateManager.doPolygonOffset(0, -3.0F);
		GlStateManager.enablePolygonOffset();
		
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(this.getRed(), this.getGreen(), this.getBlue(), alpha);

		mainModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
		mainModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

		GlStateManager.depthMask(!entity.isInvisible());
		GlStateManager.color(this.getRed() * alpha, this.getGreen() * alpha, this.getBlue() * alpha, alpha);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		int i = 61680;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
		GlStateManager.enableLighting();

		mainModel.setLivingAnimations(entity, limbSwingAmount, ageInTicks, partialTicks);
		mainModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

		this.setLightmap(entity, partialTicks);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		GlStateManager.doPolygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolygonOffset();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	/**
	 * Updates the lighting map for the position of the specified entity
	 * @param entityLivingIn
	 * @param partialTicks
	 */
	protected void setLightmap(T entityLivingIn, float partialTicks) {
		int i = entityLivingIn.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
	}

	/**
	 * Sets the layer alpha
	 * @param alpha
	 * @return
	 */
	public LayerGlow<T> setAlpha(float alpha) {
		this.alpha = alpha;
		return this;
	}

	/**
	 * Returns the layer alpha
	 * @return
	 */
	public float getAlpha() {
		return this.alpha;
	}
	
	public LayerGlow<T> setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}
	
	public LayerGlow<T> setColor(float r, float g, float b, float a) {
		this.setColor(r, g, b);
		this.setAlpha(a);
		return this;
	}
	
	public float getRed() {
		return this.r;
	}
	
	public float getGreen() {
		return this.g;
	}
	
	public float getBlue() {
		return this.b;
	}
}
