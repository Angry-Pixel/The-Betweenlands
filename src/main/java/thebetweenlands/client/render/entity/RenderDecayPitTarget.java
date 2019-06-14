package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDecayPitPlug;
import thebetweenlands.common.entity.EntityDecayPitTarget;
import thebetweenlands.common.entity.EntityDecayPitTargetPart;
import thebetweenlands.common.lib.ModInfo;
@SideOnly(Side.CLIENT)
public class RenderDecayPitTarget extends Render<EntityDecayPitTarget> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/decay_pit_plug.png");
	private final ModelDecayPitPlug PLUG_MODEL = new ModelDecayPitPlug();

	public RenderDecayPitTarget(RenderManager manager) {
		super(manager);
	}

	@Override
    public void doRender(EntityDecayPitTarget entity, double x, double y, double z, float entityYaw, float partialTicks) {
		//super.doRender(entity, x, y, z, entityYaw, partialTicks);

		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.5F, z);
		GlStateManager.scale(-1F, -1F, 1F);
		PLUG_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();	
		
		for(EntityDecayPitTargetPart part : entity.shield_array)
			if(part != entity.bottom)
				renderDebugBoundingBox(part, x, y, z, entityYaw, partialTicks, part.posX - entity.posX, part.posY - entity.posY, part.posZ - entity.posZ);
		//renderDebugBoundingBox(entity, x, y, z, entityYaw, partialTicks, 0F, 0F, 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDecayPitTarget entity) {
		return null;
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