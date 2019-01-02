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

	public RenderShambler(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelShambler(), 0.5F);
	}
	
	@Override
	public void doRender(EntityShambler entity, double x, double y, double z, float yaw, float partialTicks) {
		super.doRender(entity, x, y, z, yaw, partialTicks);
		//	renderDebugBoundingBox(entity, x, y, z, yaw, partialTicks, 0, 0, 0);
			// Child Entity Hitbox
		MultiPartEntityPart tongue = (MultiPartEntityPart) entity.tongue_end;
			renderDebugBoundingBox(tongue, x, y, z, yaw, partialTicks, tongue.posX - entity.posX, tongue.posY - entity.posY, tongue.posZ - entity.posZ);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShambler entity) {
		return TEXTURE;
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
