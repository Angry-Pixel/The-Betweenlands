package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.handler.DebugHandlerClient;
import thebetweenlands.common.entity.mobs.EntityClimberBase;

public abstract class RenderClimberBase<T extends EntityClimberBase> extends RenderLiving<T> {
	public RenderClimberBase(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
		super(rendermanagerIn, modelbaseIn, shadowsizeIn);
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		EntityClimberBase.Orientation orientation = entity.getOrientation(partialTicks);
		
		float rox = (float) (entity.prevStickingOffsetX + (entity.stickingOffsetX - entity.prevStickingOffsetX) * partialTicks) - (float) orientation.normal.x * 0.55f;
		float roy = (float) (entity.prevStickingOffsetY + (entity.stickingOffsetY - entity.prevStickingOffsetY) * partialTicks) - (float) orientation.normal.y * 0.55f;
		float roz = (float) (entity.prevStickingOffsetZ + (entity.stickingOffsetZ - entity.prevStickingOffsetZ) * partialTicks) - (float) orientation.normal.z * 0.55f;

		x += rox;
		y += roy;
		z += roz;

		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		if(this.getRenderManager().isDebugBoundingBox()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);

			GlStateManager.disableTexture2D();
			GlStateManager.color(1, 1, 1, 1);

			DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(0.2f));

			GlStateManager.color(1, 0, 0, 1);

			DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(0, 0, 0, 0, 0, 0).grow(0.1f).offset(entity.orientationNormal));

			GlStateManager.glLineWidth(4);
			GlStateManager.color(0, 0, 1);
			
			Vec3d forward = orientation.getForward(entity.rotationYaw, 0);
			
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3d(orientation.normal.x, orientation.normal.y, orientation.normal.z);
			GL11.glVertex3d(orientation.normal.x * 2 + forward.x * 2, orientation.normal.y + forward.y * 2, orientation.normal.z + forward.z * 2);
			GL11.glEnd();
			
			GlStateManager.color(0, 1, 0);
			
			forward = orientation.getForward(entity.rotationYaw, -90);
			
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3d(orientation.normal.x, orientation.normal.y, orientation.normal.z);
			GL11.glVertex3d(orientation.normal.x * 2 + forward.x * 2, orientation.normal.y + forward.y * 2, orientation.normal.z + forward.z * 2);
			GL11.glEnd();
			
			GlStateManager.color(1, 0, 0);
			
			forward = orientation.getForward(entity.rotationYaw - 90, 0);
			
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3d(orientation.normal.x, orientation.normal.y, orientation.normal.z);
			GL11.glVertex3d(orientation.normal.x * 2 + forward.x * 2, orientation.normal.y + forward.y * 2, orientation.normal.z + forward.z * 2);
			GL11.glEnd();
			
			GlStateManager.glLineWidth(1);
			
			BlockPos pathingTarget = entity.getPathingTarget();
			if(pathingTarget != null) {
				double rx = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
				double ry = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
				double rz = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;

				GlStateManager.color(0, 0, 0, 1);
				DebugHandlerClient.drawBoundingBoxOutline(new AxisAlignedBB(pathingTarget).offset(-rx - rox, -ry - roy, -rz - roz));
				
				GlStateManager.enableBlend();
		        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		        
				GlStateManager.color(1, 0, 0, 0.15f);
				DebugHandlerClient.drawBoundingBox(new AxisAlignedBB(pathingTarget).offset(-rx - rox, -ry - roy, -rz - roz));
				
				GlStateManager.disableBlend();
			}
			
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
