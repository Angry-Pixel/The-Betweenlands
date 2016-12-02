package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelTarminion;
import thebetweenlands.common.entity.projectiles.EntityThrownTarminion;

public class RenderThrownTarminion extends Render<EntityThrownTarminion> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/tarminion.png");
	private static final ModelTarminion MODEL = new ModelTarminion();

	public RenderThrownTarminion(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityThrownTarminion entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
        GlStateManager.disableCull();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(entityYaw, 0, 1, 0);
		GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.501F, 0.0F);
        GlStateManager.enableTexture2D();
        this.bindEntityTexture(entity);
		MODEL.render(entity, 0, 0, entity.ticksExisted + partialTicks, entityYaw, partialTicks, 0.0625F);
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityThrownTarminion entity) {
		return TEXTURE;
	}
}
