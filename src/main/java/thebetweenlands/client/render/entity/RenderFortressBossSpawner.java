package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelWight;
import thebetweenlands.common.entity.mobs.EntityFortressBossSpawner;

public class RenderFortressBossSpawner extends Render<EntityFortressBossSpawner> {
	protected static final ModelWight MODEL = new ModelWight();
	protected static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/wight.png");

	public RenderFortressBossSpawner(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityFortressBossSpawner entity, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.pushMatrix();
		GlStateManager.enableTexture2D();
		GlStateManager.translate(x, y + 1.338F, z);
		GlStateManager.scale(0.9F, 0.9F, 0.9F);
		this.bindTexture(TEXTURE);
		GlStateManager.rotate(180, 1, 0, 0);
		float progress = 1.0F - (float)entity.spawnDelay / entity.maxSpawnDelay;
		GlStateManager.color(1, 1, 1, progress);
		MODEL.render(entity, 0, 0, entity.ticksExisted + partialTicks, 0, 0, 0.0625F);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFortressBossSpawner p_110775_1_) {
		return null;
	}
}
