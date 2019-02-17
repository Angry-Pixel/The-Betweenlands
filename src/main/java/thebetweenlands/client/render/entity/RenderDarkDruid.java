package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelDarkDruid;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;
import thebetweenlands.util.LightingUtil;

@OnlyIn(Dist.CLIENT)
public class RenderDarkDruid extends RenderLiving<EntityDarkDruid> {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/dark_druid.png");

	public RenderDarkDruid(RenderManager renderManager) {
		super(renderManager, new ModelDarkDruid(), 0.7F);
		this.addLayer(new LayerDruidGlow(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDarkDruid entity) {
		return texture;
	}

	@OnlyIn(Dist.CLIENT)
	public static class LayerDruidGlow implements LayerRenderer<EntityDarkDruid> {
		private static final ResourceLocation GLOW = new ResourceLocation("thebetweenlands:textures/entity/dark_druid_glow.png");
		private final RenderDarkDruid renderer;

		public LayerDruidGlow(RenderDarkDruid renderer) {
			this.renderer = renderer;
		}

		@Override
		public void doRenderLayer(EntityDarkDruid entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			if (!entity.isInvisible()) {
				this.renderer.bindTexture(GLOW);
				GlStateManager.enableBlend();
				GlStateManager.disableAlpha();
				LightingUtil.INSTANCE.setLighting(255);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.renderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
				LightingUtil.INSTANCE.revert();
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
			}
		}

		@Override
		public boolean shouldCombineTextures() {
			return false;
		}
	}
}
