package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelShambler;
import thebetweenlands.common.entity.mobs.EntityShambler;

@SideOnly(Side.CLIENT)
public class RenderShambler extends RenderLiving<EntityShambler> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/shambler.png");
	public final ModelShambler model = new ModelShambler();
	public RenderShambler(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelShambler(), 0.5F);
	}

	@Override
	public void doRender(EntityShambler entity, double x, double y, double z, float yaw, float partialTicks) {
		super.doRender(entity, x, y, z, yaw, partialTicks);
		//	renderDebugBoundingBox(entity, x, y, z, yaw, partialTicks, 0, 0, 0);

		if(entity.getTongueLength() > 0) {
/*
			renderDebugBoundingBox(entity.tongue_end, x, y, z, yaw, partialTicks, entity.tongue_end.posX - entity.posX, entity.tongue_end.posY - entity.posY, entity.tongue_end.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_1, x, y, z, yaw, partialTicks, entity.tongue_1.posX - entity.posX, entity.tongue_1.posY - entity.posY, entity.tongue_1.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_2, x, y, z, yaw, partialTicks, entity.tongue_2.posX - entity.posX, entity.tongue_2.posY - entity.posY, entity.tongue_2.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_3, x, y, z, yaw, partialTicks, entity.tongue_3.posX - entity.posX, entity.tongue_3.posY - entity.posY, entity.tongue_3.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_4, x, y, z, yaw, partialTicks, entity.tongue_4.posX - entity.posX, entity.tongue_4.posY - entity.posY, entity.tongue_4.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_5, x, y, z, yaw, partialTicks, entity.tongue_5.posX - entity.posX, entity.tongue_5.posY - entity.posY, entity.tongue_5.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_6, x, y, z, yaw, partialTicks, entity.tongue_6.posX - entity.posX, entity.tongue_6.posY - entity.posY, entity.tongue_6.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_7, x, y, z, yaw, partialTicks, entity.tongue_7.posX - entity.posX, entity.tongue_7.posY - entity.posY, entity.tongue_7.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_8, x, y, z, yaw, partialTicks, entity.tongue_8.posX - entity.posX, entity.tongue_8.posY - entity.posY, entity.tongue_8.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_9, x, y, z, yaw, partialTicks, entity.tongue_9.posX - entity.posX, entity.tongue_9.posY - entity.posY, entity.tongue_9.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_10, x, y, z, yaw, partialTicks, entity.tongue_10.posX - entity.posX, entity.tongue_10.posY - entity.posY, entity.tongue_10.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_11, x, y, z, yaw, partialTicks, entity.tongue_11.posX - entity.posX, entity.tongue_11.posY - entity.posY, entity.tongue_11.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_12, x, y, z, yaw, partialTicks, entity.tongue_12.posX - entity.posX, entity.tongue_12.posY - entity.posY, entity.tongue_12.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_13, x, y, z, yaw, partialTicks, entity.tongue_13.posX - entity.posX, entity.tongue_13.posY - entity.posY, entity.tongue_13.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_14, x, y, z, yaw, partialTicks, entity.tongue_14.posX - entity.posX, entity.tongue_14.posY - entity.posY, entity.tongue_14.posZ - entity.posZ);
			renderDebugBoundingBox(entity.tongue_15, x, y, z, yaw, partialTicks, entity.tongue_15.posX - entity.posX, entity.tongue_15.posY - entity.posY, entity.tongue_15.posZ - entity.posZ);
*/			
			double ex = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
	        double ey = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
	        double ez = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;

	        double rx = ex - x;
	        double ry = ey - y;
	        double rz = ez - z;

	        for(int i = 0; i < entity.tongue_array.length; i++) {
	        	renderTonguePart(entity, entity.tongue_array[i], rx, ry, rz, partialTicks);
	        }
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShambler entity) {
		return TEXTURE;
	}

	private void renderTonguePart(EntityShambler entity, MultiPartEntityPart part, double rx, double ry, double rz, float partialTicks) {
		double x = part.lastTickPosX + (part.posX - part.lastTickPosX) * (double)partialTicks - rx;
        double y = part.lastTickPosY + (part.posY - part.lastTickPosY) * (double)partialTicks - ry;
        double z = part.lastTickPosZ + (part.posZ - part.lastTickPosZ) * (double)partialTicks - rz;
        float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y - 0.85D, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(180F + yaw, 0F, 1F, 0F);
		GlStateManager.rotate(180F + pitch, 1F, 0F, 0F);
		if(part == entity.tongue_end)
			model.renderTongueEnd(0.0625F);
		else
			model.renderTonguePart(0.0625F);
		GlStateManager.popMatrix();
	}

	private void renderDebugBoundingBox(Entity entity, double x, double y, double z, float yaw, float partialTicks, double xOff, double yOff, double zOff) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entity.posX + x + xOff, axisalignedbb.minY - entity.posY + y + yOff, axisalignedbb.minZ - entity.posZ + z + zOff, axisalignedbb.maxX - entity.posX + x + xOff, axisalignedbb.maxY - entity.posY + y + yOff, axisalignedbb.maxZ - entity.posZ + z + zOff);
		RenderGlobal.drawSelectionBoundingBox(axisalignedbb1, 1F, 1F, 1F, 1F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}
}
