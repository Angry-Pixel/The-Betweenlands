package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSmolSludgeWorm;
import thebetweenlands.common.entity.mobs.EntitySmolSludgeWorm;

@SideOnly(Side.CLIENT)
public class RenderSmolSludgeWorm extends RenderLiving<EntitySmolSludgeWorm> {

	public static final ResourceLocation TEXTURE_HEAD = new ResourceLocation("thebetweenlands:textures/entity/smol_sludge_worm_head.png");
	public static final ResourceLocation TEXTURE_BODY = new ResourceLocation("thebetweenlands:textures/entity/smol_sludge_worm_body.png");
	public final ModelSmolSludgeWorm model = new ModelSmolSludgeWorm();

	public RenderSmolSludgeWorm(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSmolSludgeWorm(), 0.5F);
	}

	@Override
	public void doRender(EntitySmolSludgeWorm entity, double x, double y, double z, float yaw, float partialTicks) {
		super.doRender(entity, x, y, z, yaw, partialTicks);

		GlStateManager.pushMatrix();
		if (entity.hurtResistantTime <= 40 && entity.hurtResistantTime >= 35) {
			float red = 0.8F;
			float green = 0.2F;
			float blue = 0.2F;
			GlStateManager.color(red, green, blue);
		}
		renderHead(entity, 1, x, y + 1.5F, z, entity.sludge_worm_1.rotationYaw, partialTicks);
		
		double ex = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double ey = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double ez = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
		
        double rx = ex - x;
        double ry = ey - y;
        double rz = ez - z;
        
		GlStateManager.pushMatrix();
		
		renderBodyPart(entity, entity.sludge_worm_2, rx, ry, rz, 1, partialTicks);
		renderBodyPart(entity, entity.sludge_worm_3, rx, ry, rz, 2, partialTicks);
		renderBodyPart(entity, entity.sludge_worm_4, rx, ry, rz, 3, partialTicks);
		renderBodyPart(entity, entity.sludge_worm_5, rx, ry, rz, 4, partialTicks);
		renderBodyPart(entity, entity.sludge_worm_6, rx, ry, rz, 5, partialTicks);
		renderBodyPart(entity, entity.sludge_worm_7, rx, ry, rz, 4, partialTicks);
		renderBodyPart(entity, entity.sludge_worm_8, rx, ry, rz, 3, partialTicks);
		renderTailPart(entity, entity.sludge_worm_9, rx, ry, rz, 2,  partialTicks);

		GlStateManager.popMatrix();
		
		GlStateManager.popMatrix();

	}

	private void renderHead(EntitySmolSludgeWorm entity, int frame, double x, double y, double z, float yaw, float partialTicks) {
		bindTexture(TEXTURE_HEAD);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(180F + yaw, 0, 1F, 0);
		model.renderHead(entity, frame, partialTicks);
		GlStateManager.popMatrix();
	}

	private void renderBodyPart(EntitySmolSludgeWorm entity, MultiPartEntityPart part, double rx, double ry, double rz, int frame, float partialTicks) {
		bindTexture(TEXTURE_BODY);
		
		double x = part.lastTickPosX + (part.posX - part.lastTickPosX) * (double)partialTicks - rx;
        double y = part.lastTickPosY + (part.posY - part.lastTickPosY) * (double)partialTicks - ry;
        double z = part.lastTickPosZ + (part.posZ - part.lastTickPosZ) * (double)partialTicks - rz;
		
        float yaw = part.prevRotationYaw + (part.rotationYaw - part.prevRotationYaw) * partialTicks;
        
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y - 1.125f, z);// GlStateManager.translate(x, y, z);
		//GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(-yaw, 0, 1F, 0);
		model.renderBody(entity, frame, partialTicks);
		GlStateManager.popMatrix();
	}

	private void renderTailPart(EntitySmolSludgeWorm entity, MultiPartEntityPart part, double rx, double ry, double rz, int frame, float partialTicks) {
		bindTexture(TEXTURE_BODY);
		
		double x = part.lastTickPosX + (part.posX - part.lastTickPosX) * (double)partialTicks - rx;
        double y = part.lastTickPosY + (part.posY - part.lastTickPosY) * (double)partialTicks - ry;
        double z = part.lastTickPosZ + (part.posZ - part.lastTickPosZ) * (double)partialTicks - rz;
		
        float yaw = part.prevRotationYaw + (part.rotationYaw - part.prevRotationYaw) * partialTicks;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.525f, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(180F + yaw, 0, 1F, 0);
		GlStateManager.disableCull();
		model.renderTail(entity, frame, partialTicks);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySmolSludgeWorm entity) {
		return TEXTURE_HEAD;
	}

}