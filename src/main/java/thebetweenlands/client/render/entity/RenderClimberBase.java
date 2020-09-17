package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.handler.DebugHandlerClient;
import thebetweenlands.common.entity.mobs.EntityClimberBase;

public abstract class RenderClimberBase<T extends EntityClimberBase> extends RenderLiving<T> {
	public RenderClimberBase(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
		super(rendermanagerIn, modelbaseIn, shadowsizeIn);
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		float rox = (float) (entity.prevStickingOffsetX + (entity.stickingOffsetX - entity.prevStickingOffsetX) * partialTicks);
		float roy = (float) (entity.prevStickingOffsetY + (entity.stickingOffsetY - entity.prevStickingOffsetY) * partialTicks);
		float roz = (float) (entity.prevStickingOffsetZ + (entity.stickingOffsetZ - entity.prevStickingOffsetZ) * partialTicks);

		EntityClimberBase.Orientation orientation = entity.getOrientation(partialTicks);

		x += rox - orientation.normal.x * 0.55f;
		y += roy - orientation.normal.y * 0.55f;
		z += roz - orientation.normal.z * 0.55f;

		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		if(this.getRenderManager().isDebugBoundingBox()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);

			GlStateManager.disableTexture2D();
			GlStateManager.color(1, 1, 1, 1);

			DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(0.2f));

			GlStateManager.color(1, 0, 0, 1);

			DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(0.1f).offset(entity.orientationNormal));

			GlStateManager.glLineWidth(3);
			GlStateManager.color(0, 1, 0);
			
			Vec3d forward = orientation.getForward(entity.rotationYaw, 0);
			
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3d(orientation.normal.x, orientation.normal.y, orientation.normal.z);
			GL11.glVertex3d(orientation.normal.x * 2 + forward.x * 2, orientation.normal.y + forward.y * 2, orientation.normal.z + forward.z * 2);
			GL11.glEnd();
			
			GlStateManager.glLineWidth(1);
			
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableTexture2D();

			GlStateManager.popMatrix();
		}
	}

	@Override
	protected void applyRotations(T entity, float ageInTicks, float rotationYaw, float partialTicks) {
		EntityClimberBase.Orientation orientation = entity.getOrientation(partialTicks);

		GlStateManager.rotate(orientation.yaw, 0, 1, 0);
		GlStateManager.rotate(orientation.pitch, 1, 0, 0);

		GlStateManager.rotate((float)Math.signum(0.1f - orientation.upComponent) * orientation.yaw, 0, 1, 0);

		this.applyLocalRotations(entity, ageInTicks, rotationYaw, partialTicks);
	}

	protected void applyLocalRotations(T entity, float ageInTicks, float rotationYaw, float partialTicks) {
		super.applyRotations(entity, ageInTicks, rotationYaw, partialTicks);
	}
}
