package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelFreshwaterUrchin;
import thebetweenlands.common.entity.mobs.EntityFreshwaterUrchin;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderFreshwaterUrchin extends RenderLiving<EntityFreshwaterUrchin> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/freshwater_urchin.png");

	public RenderFreshwaterUrchin(RenderManager renderManager) {
		super(renderManager, new ModelFreshwaterUrchin(), 0.2F);
	}
	
//TODO (start) remove once particles are properly sorted
	@Override
	protected void preRenderCallback(EntityFreshwaterUrchin entity, float partialTickTime) {
		// may need some GL scaling here
	}
	
	@Override
	public void doRender(EntityFreshwaterUrchin entity, double x, double y, double z, float yaw, float partialTicks) {
		super.doRender(entity, x, y, z, yaw, partialTicks);

	//	renderDebugBoundingBox(entity, x, y, z, yaw, partialTicks, 0, 0, 0); 
	}

	private void renderDebugBoundingBox(EntityFreshwaterUrchin entity, double x, double y, double z, float yaw, float partialTicks, double xOff, double yOff, double zOff) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		AxisAlignedBB axisalignedbb = entity.spikesBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entity.posX + x + xOff, axisalignedbb.minY - entity.posY + y + yOff, axisalignedbb.minZ - entity.posZ + z + zOff, axisalignedbb.maxX - entity.posX + x + xOff, axisalignedbb.maxY - entity.posY + y + yOff, axisalignedbb.maxZ - entity.posZ + z + zOff);
		RenderGlobal.drawSelectionBoundingBox(axisalignedbb1, 1F, 1F, 1F, 1F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}
// TODO (end)

	@Override
	protected ResourceLocation getEntityTexture(EntityFreshwaterUrchin entity) {
		return TEXTURE;
	}
}
