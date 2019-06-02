package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelTonyWormEggSac;
import thebetweenlands.common.entity.EntityTonyWormEggSac;
import thebetweenlands.common.lib.ModInfo;

public class RenderTonyWormEggSac extends Render<EntityTonyWormEggSac> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/worm_egg_sac.png");
	private final ModelTonyWormEggSac EGG_SAC = new ModelTonyWormEggSac();

	public RenderTonyWormEggSac(RenderManager manager) {
		super(manager);
	}

	@Override
	public void doRender(EntityTonyWormEggSac entity, double x, double y, double z, float entityYaw, float partialTicks) {
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.5F, z);
		GlStateManager.scale(-1F, -1F, 1F);
		// GlStateManager.scale(2.5F + entitylivingbaseIn.pulseFloat, 2.5F + entitylivingbaseIn.pulseFloat, 2.5F + entitylivingbaseIn.pulseFloat);
		EGG_SAC.render(0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTonyWormEggSac entity) {
		return TEXTURE;
	}
}