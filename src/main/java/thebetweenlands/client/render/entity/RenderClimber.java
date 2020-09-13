package thebetweenlands.client.render.entity;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.entity.mobs.EntityClimber;

public class RenderClimber extends RenderLiving<EntityClimber> {
	public RenderClimber(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSpider(), 0.5f);
	}

	@Override
	public void doRender(EntityClimber entity, double x, double y, double z, float entityYaw, float partialTicks) {
		float rox = (float) (entity.prevRenderOffsetX + (entity.renderOffsetX - entity.prevRenderOffsetX) * partialTicks);
		float roy = (float) (entity.prevRenderOffsetY + (entity.renderOffsetY - entity.prevRenderOffsetY) * partialTicks);
		float roz = (float) (entity.prevRenderOffsetZ + (entity.renderOffsetZ - entity.prevRenderOffsetZ) * partialTicks);

		EntityClimber.Orientation orientation = entity.getOrientation(partialTicks);

		x += rox - orientation.normal.x * 0.55f;
		y += roy - orientation.normal.y * 0.55f;
		z += roz - orientation.normal.z * 0.55f;

		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		/*GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		GlStateManager.disableTexture2D();
		GlStateManager.color(1, 1, 1, 1);

		DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(0.2f));

		if(entity.orientationNormal != null) {
			GlStateManager.color(1, 0, 0, 1);
			DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(0.1f).offset(entity.orientationNormal));
		}

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();

		GlStateManager.popMatrix();*/
	}

	@Override
	protected void applyRotations(EntityClimber entity, float ageInTicks, float rotationYaw, float partialTicks) {
		if(entity.prevOrientationNormal != null) {
			EntityClimber.Orientation orientation = entity.getOrientation(partialTicks);

			GlStateManager.rotate(orientation.yaw, 0, 1, 0);
			GlStateManager.rotate(orientation.pitch, 1, 0, 0);

			float upThreshold = 0.1f;

			GlStateManager.rotate((float)Math.signum(upThreshold - orientation.upComponent) * orientation.yaw, 0, 1, 0);

			super.applyRotations(entity, ageInTicks, rotationYaw, partialTicks);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityClimber entity) {
		return new ResourceLocation("textures/entity/spider/spider.png");
	}
}
