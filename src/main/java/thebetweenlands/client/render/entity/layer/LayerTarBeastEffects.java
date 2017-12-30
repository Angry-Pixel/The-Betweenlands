package thebetweenlands.client.render.entity.layer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.entity.RenderTarBeast;
import thebetweenlands.client.render.model.entity.ModelTarBeast;
import thebetweenlands.common.entity.mobs.EntityTarBeast;

public class LayerTarBeastEffects implements LayerRenderer<EntityTarBeast> {
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/tar_beast_overlay.png");

	public final RenderTarBeast renderer;

	public LayerTarBeastEffects(RenderTarBeast renderer) {
		this.renderer = renderer;
	}

	@Override
	public void doRenderLayer(EntityTarBeast entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(!entity.isInvisible());

		float scrollTimer = entity.ticksExisted + partialTicks;
		this.renderer.bindTexture(OVERLAY_TEXTURE);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		float yScroll = scrollTimer * 0.002F;
		GlStateManager.translate(0F, -yScroll, 0.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		float colour = 0.5F;
		GlStateManager.color(colour, colour, colour, 1.0F);

		ModelTarBeast mainModel = (ModelTarBeast) this.renderer.getMainModel();
		mainModel.drippingtar1_keepstraight.showModel = false;
		mainModel.drippingtar2_keepstraight.showModel = false;
		mainModel.teeth_keepstraight.showModel = false;
		mainModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
		mainModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
		mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		mainModel.drippingtar1_keepstraight.showModel = true;
		mainModel.drippingtar2_keepstraight.showModel = true;
		mainModel.teeth_keepstraight.showModel = true;

		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		this.renderer.bindTexture(this.renderer.getEntityTexture(entity));

		if(entity.isShedding()) {
			float sheddingScale = (float)(entity.getSheddingProgress() * entity.getSheddingProgress() * 0.002F) + 1.0F;

			GlStateManager.color(1, 1, 1, 0.6F);

			GlStateManager.pushMatrix();
			GlStateManager.scale(sheddingScale, sheddingScale, sheddingScale);

			this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

			GlStateManager.popMatrix();

			sheddingScale = (float)(entity.getSheddingProgress() * entity.getSheddingProgress() * 0.004F) + 1.0F;

			GlStateManager.color(1, 1, 1, 0.3F);

			GlStateManager.pushMatrix();
			GlStateManager.scale(sheddingScale, sheddingScale, sheddingScale);

			this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

			GlStateManager.popMatrix();
		}

		if(entity.isPreparing()) {
			float sheddingScale = (float)((1.0F - (entity.ticksExisted % 8) / 8.0F) * 0.15F + 1.0F);

			GlStateManager.color(1, 1, 1, 0.6F);

			GlStateManager.pushMatrix();
			GlStateManager.scale(sheddingScale, sheddingScale, sheddingScale);

			this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

			GlStateManager.popMatrix();

			sheddingScale = (float)((1.0F - (entity.ticksExisted % 8) / 8.0F) * 0.2F + 1.05F);

			GlStateManager.color(1, 1, 1, 0.3F);

			GlStateManager.pushMatrix();
			GlStateManager.scale(sheddingScale, sheddingScale, sheddingScale);

			this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

			GlStateManager.popMatrix();
		}

		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
