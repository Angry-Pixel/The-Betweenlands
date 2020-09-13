package thebetweenlands.client.render.entity;

import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.handler.DebugHandlerClient;
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
		
		x += rox;
		y += roy;
		z += roz;
		
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		
		GlStateManager.disableTexture2D();
		GlStateManager.color(1, 1, 1, 1);
		
		DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(0.2f));
		
		if(entity.renderNormal != null) {
			GlStateManager.color(1, 0, 0, 1);
			DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(0.1f).offset(entity.renderNormal));
		}
		
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
		
		GlStateManager.popMatrix();
	}
	
	@Override
	protected void applyRotations(EntityClimber entity, float ageInTicks, float rotationYaw, float partialTicks) {
		// TODO Auto-generated method stub
		//super.applyRotations(entity, ageInTicks, rotationYaw, partialTicks);
		
		if(entity.prevRenderNormal != null) {
			Vec3d renderNormal = entity.prevRenderNormal.add(entity.renderNormal.subtract(entity.prevRenderNormal).scale(partialTicks));
			
			Vec3d fwdAxis = new Vec3d(0, 0, 1);
			Vec3d upAxis = new Vec3d(0, 1, 0);
			Vec3d rightAxis = new Vec3d(1, 0, 0);
	
			double fwd = fwdAxis.dotProduct(renderNormal);
			double up = upAxis.dotProduct(renderNormal);
			double right = rightAxis.dotProduct(renderNormal);
	
			float yaw = (float)Math.toDegrees(Math.atan2(right, fwd));
			float pitch = (float)Math.toDegrees(Math.atan2(fwd, up)) * (float)Math.signum(fwd);

			GlStateManager.rotate(yaw, 0, 1, 0);
			GlStateManager.rotate(pitch, 1, 0, 0);
			
			//GlStateManager.rotate(yaw, 0, 1, 0);
			
			//System.out.println(entity.renderNormal);
		}
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityClimber entity) {
		return new ResourceLocation("textures/entity/spider/spider.png");
	}
}
