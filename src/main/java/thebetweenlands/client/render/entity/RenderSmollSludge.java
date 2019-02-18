package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelSmollSludge;
import thebetweenlands.common.entity.mobs.EntitySmollSludge;

@OnlyIn(Dist.CLIENT)
public class RenderSmollSludge extends RenderLiving<EntitySmollSludge> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/smoll_sludge.png");

	public RenderSmollSludge(RenderManager renderManager) {
		super(renderManager, new ModelSmollSludge(), 0.0F);
	}

	@Override
	public void doRender(EntitySmollSludge entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		float squishFactor = entity.getSquishFactor(partialTicks) / 1.5F;
		float scale = 1.0F / (squishFactor + 1.0F);

		GlStateManager.translated(x, y - (1.0F - entity.scale.getAnimationProgressSin(partialTicks)) * 2.5F, z);
		GlStateManager.scalef(scale, 1.0F / scale, scale);

		GlStateManager.depthMask(false);

		GlStateManager.cullFace(CullFace.FRONT);
		super.doRender(entity, 0, 0, 0, entityYaw, partialTicks);

		GlStateManager.cullFace(CullFace.BACK);
		super.doRender(entity, 0, 0, 0, entityYaw, partialTicks);

		GlStateManager.depthMask(true);
		GlStateManager.colorMask(false, false, false, false);

		super.doRender(entity, 0, 0, 0, entityYaw, partialTicks);

		GlStateManager.colorMask(true, true, true, true);

		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySmollSludge entity) {
		return TEXTURE;
	}
}
