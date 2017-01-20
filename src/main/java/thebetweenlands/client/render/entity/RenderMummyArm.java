package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelMummyArm;
import thebetweenlands.common.entity.mobs.EntityMummyArm;

public class RenderMummyArm extends RenderLiving<EntityMummyArm> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/peat_mummy.png");
	public static final ModelMummyArm MODEL = new ModelMummyArm();

	public RenderMummyArm(RenderManager renderManager) {
		super(renderManager, MODEL, 0.2F);
	}

	@Override
	public void doRender(EntityMummyArm arm, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0D, arm.getYOffset() - 1.9D, 0.0D);

		super.doRender(arm, x, y, z, yaw, partialTicks);

		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMummyArm entity) {
		return TEXTURE;
	}
}