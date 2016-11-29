package thebetweenlands.client.render.entity.layer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.entity.mobs.EntityPyrad;

public class LayerPyradEffects extends LayerGlow<EntityPyrad> {

	public LayerPyradEffects(RenderLivingBase<EntityPyrad> renderer, ResourceLocation glowTexture) {
		super(renderer, glowTexture);
	}

	@Override
	public void doRenderLayer(EntityPyrad entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		this.renderer.bindTexture(this.glowTexture);
		ModelBase mainModel = this.renderer.getMainModel();
		mainModel.setLivingAnimations(entity, limbSwingAmount, ageInTicks, partialTicks);
		mainModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		float alpha = entity.getActiveTicks(partialTicks) / 60.0F + (!entity.isActive() ? entity.getHitTicks(partialTicks) / 20.0F * 0.45F : 0.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
		mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		GlStateManager.color(alpha, alpha, alpha, alpha);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		GlStateManager.depthMask(!entity.isInvisible());
		int i = 61680;
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
		GlStateManager.enableLighting();
		mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		this.setLightmap(entity, partialTicks);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
	}
}
