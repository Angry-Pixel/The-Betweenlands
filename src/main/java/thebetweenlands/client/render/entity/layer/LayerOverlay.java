package thebetweenlands.client.render.entity.layer;

import javax.annotation.Nullable;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * Adds a simple overlay layer to the renderer

 * @param <T>
 */
public class LayerOverlay<T extends EntityLivingBase> implements LayerRenderer<T> {
	public final RenderLivingBase<T> renderer;
	public final ResourceLocation texture;

	private boolean glow;
	private float r = 1.0F, g = 1.0F, b = 1.0F;
	private float alpha = 1.0F;

	public LayerOverlay(RenderLivingBase<T> renderer) {
		this(renderer, null);
	}

	public LayerOverlay(RenderLivingBase<T> renderer, ResourceLocation texture) {
		this.renderer = renderer;
		this.texture = texture;
	}

	@Override
	public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		int index = 0;

		for(ModelBase model : this.getModels(entity)) {
			ResourceLocation texture = this.getTexture(entity, index);

			if(texture != null) {
				this.renderer.bindTexture(texture);

				float alpha = this.getAlpha();
				float red = this.getRed();
				float green = this.getGreen();
				float blue = this.getBlue();

				renderOverlay(entity, () -> {
					this.renderOverlay(entity, model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
				}, this.getGlow(), red, green, blue, alpha);
			}

			index++;
		}
	}
	
	public static void renderOverlay(@Nullable Entity entity, Runnable renderer, boolean glow, float red, float green, float blue, float alpha) {
		GlStateManager.doPolygonOffset(-0.01F, -3.0F);
		GlStateManager.enablePolygonOffset();

		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(false);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(red, green, blue, alpha);
		
		if(glow) {
			int i = 61680;
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
			GlStateManager.disableLighting();
	    }
		
		renderer.run();
		
		if(glow) {
			GlStateManager.depthMask(entity == null || !entity.isInvisible());
			GlStateManager.color(red * alpha, green * alpha, blue * alpha, alpha);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

			renderer.run();
			
			if(entity != null) {
				setLightmap(entity);
			}
		}

		GlStateManager.enableLighting();
		
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.doPolygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolygonOffset();
	}

	protected void renderOverlay(T entity, ModelBase model, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
		model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	}

	@Nullable
	protected ResourceLocation getTexture(T entity, int model) {
		return this.texture;
	}

	protected ModelBase[] getModels(T entity) {
		return new ModelBase[] { this.renderer.getMainModel() };
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	/**
	 * Updates the lighting map for the position of the specified entity
	 * @param entity
	 */
	protected static void setLightmap(Entity entity) {
		int i = entity.getBrightnessForRender();
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
	}

	/**
	 * Sets the layer alpha
	 * @param alpha
	 * @return
	 */
	public LayerOverlay<T> setAlpha(float alpha) {
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

	public LayerOverlay<T> setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}

	public LayerOverlay<T> setColor(float r, float g, float b, float a) {
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

	public LayerOverlay<T> setGlow(boolean glow) {
		this.glow = glow;
		return this;
	}

	public boolean getGlow() {
		return this.glow;
	}
}
