package thebetweenlands.client.render.entity;

import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityRockSnot;

@SideOnly(Side.CLIENT)
public class RenderRockSnot extends RenderLiving<EntityRockSnot> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/blank.png");
	public final ModelChicken model = new ModelChicken();

	public RenderRockSnot(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelChicken(), 0.5F); //chicken \o/
	}

	@Override
	public void doRender(EntityRockSnot entity, double x, double y, double z, float yaw, float partialTicks) {
		super.doRender(entity, x, y, z, yaw, partialTicks);
		//if(entity.grabber != null)
			//renderDebugBoundingBox(entity.grabber, x, y, z, yaw, partialTicks, entity.grabber.posX - entity.posX, entity.grabber.posY - entity.posY, entity.grabber.posZ - entity.posZ);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRockSnot entity) {
		return null;//TEXTURE;
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
