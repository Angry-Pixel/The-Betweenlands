package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.proxy.ClientProxy;
import thebetweenlands.client.render.model.entity.ModelLurker;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityLurker;

public class RenderLurker extends RenderLiving<EntityLurker> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/lurker.png");

	public RenderLurker(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelLurker(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLurker entity) {
		return TEXTURE;
	}

	@Override
	protected void applyRotations(EntityLurker lurker, float headPitch, float yaw, float partialTicks) {
		super.applyRotations(lurker, headPitch, yaw, partialTicks);
		GlStateManager.rotate((lurker).getRotationPitch(partialTicks), 1, 0, 0);
	}

	@Override
	protected void renderModel(EntityLurker lurker, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		if (!lurker.getPassengers().isEmpty() && lurker.getPassengers().get(0) instanceof EntityDragonFly) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(180, 1, 0, 0);
			GlStateManager.translate(0, -1.25F, 0);
			GlStateManager.translate(0, 0, 0.7F);
			GlStateManager.rotate(-MathHelper.clamp(netHeadYaw, -50, 50), 0, 1, 0);
			GlStateManager.rotate(headPitch, 1, 0, 0);
			GlStateManager.translate(0, 0, 0.8F);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableTexture2D();
			EntityDragonFly dragonfly = (EntityDragonFly) lurker.getPassengers().get(0);
			dragonfly.prevRenderYawOffset = dragonfly.renderYawOffset = 0;
			dragonfly.prevRotationYaw = dragonfly.rotationYaw = 0;
			dragonfly.prevRotationYawHead = dragonfly.rotationYawHead = 0;
			ClientProxy.dragonFlyRenderer.doRender(dragonfly, 0, 0, 0, 0, ageInTicks - lurker.ticksExisted);
			GlStateManager.popMatrix();
			GlStateManager.enableBlend();
			GlStateManager.enableRescaleNormal();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		}

		super.renderModel(lurker, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
	}

	@Override
	public void doRender(EntityLurker lurker, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(lurker, x, y, z, entityYaw, partialTicks);

	}
}
