package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
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
		//renderHead(x, y - 1.1875F, z, entity.sludge_worm_1.rotationYaw);
		renderBodyPart(entity, 1, x + entity.sludge_worm_2.posX - entity.posX, y - 1.1875F + entity.sludge_worm_2.posY - entity.posY, z + entity.sludge_worm_2.posZ - entity.posZ, entity.sludge_worm_2.rotationYaw, partialTicks);
		renderBodyPart(entity, 2, x + entity.sludge_worm_3.posX - entity.posX, y - 1.1875F + entity.sludge_worm_3.posY - entity.posY, z + entity.sludge_worm_3.posZ - entity.posZ, entity.sludge_worm_3.rotationYaw, partialTicks);
		renderBodyPart(entity, 3, x + entity.sludge_worm_4.posX - entity.posX, y - 1.1875F + entity.sludge_worm_4.posY - entity.posY, z + entity.sludge_worm_4.posZ - entity.posZ, entity.sludge_worm_4.rotationYaw, partialTicks);
		renderBodyPart(entity, 4, x + entity.sludge_worm_5.posX - entity.posX, y - 1.1875F + entity.sludge_worm_5.posY - entity.posY, z + entity.sludge_worm_5.posZ - entity.posZ, entity.sludge_worm_5.rotationYaw, partialTicks);
		renderBodyPart(entity, 5, x + entity.sludge_worm_6.posX - entity.posX, y - 1.1875F + entity.sludge_worm_6.posY - entity.posY, z + entity.sludge_worm_6.posZ - entity.posZ, entity.sludge_worm_6.rotationYaw, partialTicks);
		renderBodyPart(entity, 4, x + entity.sludge_worm_7.posX - entity.posX, y - 1.1875F + entity.sludge_worm_7.posY - entity.posY, z + entity.sludge_worm_7.posZ - entity.posZ, entity.sludge_worm_7.rotationYaw, partialTicks);
		renderBodyPart(entity, 3, x + entity.sludge_worm_8.posX - entity.posX, y - 1.1875F + entity.sludge_worm_8.posY - entity.posY, z + entity.sludge_worm_8.posZ - entity.posZ, entity.sludge_worm_8.rotationYaw, partialTicks);
		renderBodyPart(entity, 2, x + entity.sludge_worm_9.posX - entity.posX, y - 1.1875F + entity.sludge_worm_9.posY - entity.posY, z + entity.sludge_worm_9.posZ - entity.posZ, entity.sludge_worm_9.rotationYaw, partialTicks);
		GlStateManager.popMatrix();

	}
/*
	private void renderHead(double x, double y, double z, float yaw) {
		bindTexture(TEXTURE_HEAD);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180F - yaw, 0, 1F, 0);
		model.renderHead();
		GlStateManager.popMatrix();
	}
*/
	private void renderBodyPart(EntitySmolSludgeWorm entity, int frame, double x, double y, double z, float yaw, float partialTicks) {
		bindTexture(TEXTURE_BODY);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180F - yaw, 0, 1F, 0);
		model.renderBody(entity, frame, partialTicks);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySmolSludgeWorm entity) {
		return TEXTURE_HEAD;
	}

}