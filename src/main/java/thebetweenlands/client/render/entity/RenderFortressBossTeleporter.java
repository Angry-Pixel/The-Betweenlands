package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelFortressBoss;
import thebetweenlands.common.entity.mobs.EntityFortressBossTeleporter;
import thebetweenlands.util.LightingUtil;

public class RenderFortressBossTeleporter extends Render<EntityFortressBossTeleporter> {
	protected static final ResourceLocation MODEL_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/fortress_boss.png");
	protected static final ModelFortressBoss MODEL = new ModelFortressBoss();

	public RenderFortressBossTeleporter(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityFortressBossTeleporter entity, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.translate(x, y, z);

		this.bindTexture(MODEL_TEXTURE);
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.translate(0, -0.25D, 0.1D);
		GlStateManager.translate(MODEL.eye.rotationPointX * 0.065F / 2.0F, MODEL.eye.rotationPointY * 0.065F / 2.0F, MODEL.eye.rotationPointZ * 0.065F / 2.0F);
		if(entity.isLookingAtPlayer || entity.getTarget() != null) {
			GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0, 1, 0);
			GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1, 0, 0);
		} else {
			GlStateManager.rotate(88, 1, 0, 0);
		}
		GlStateManager.translate(-MODEL.eye.rotationPointX * 0.065F / 2.0F, -MODEL.eye.rotationPointY * 0.065F / 2.0F + 0.3D, -MODEL.eye.rotationPointZ * 0.065F / 2.0F);
		if(entity.getTarget() != null)
			GlStateManager.translate(Math.sin((entity.ticksExisted + partialTicks)/5.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/7.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/6.0D) * 0.1F);
		if(entity.getTarget() == null || entity.getTarget() != Minecraft.getMinecraft().getRenderViewEntity()) {
			GlStateManager.scale(0.8F, 0.8F, 0.8F);
		} else {
			float scale = (float)Math.pow(entity.getTeleportProgress(), 3) * 2.5F;
			GlStateManager.translate(0, scale / 2.0F, 0);
			GlStateManager.scale(0.8F + scale, 0.8F + scale, 0.8F);
		}
		GlStateManager.disableCull();
		LightingUtil.INSTANCE.setLighting(255);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		if(entity.getTarget() != null) {
			GlStateManager.depthMask(false);
			GlStateManager.color(1, 1, 1, 0.5F);
			GlStateManager.colorMask(false, false, false, false);
			MODEL.eye.render(0.065F);
			GlStateManager.colorMask(true, true, true, true);
			MODEL.eye.render(0.065F);
			GlStateManager.depthMask(true);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.SRC_ALPHA);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.colorMask(false, false, false, false);
			MODEL.eye.render(0.065F);
			GlStateManager.colorMask(true, true, true, true);
			MODEL.eye.render(0.065F);

			if(entity.getTarget() == Minecraft.getMinecraft().getRenderViewEntity()) {
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				float alpha = (float) Math.pow(entity.getTeleportProgress(), 2.5D);
				GlStateManager.color(1, 1, 1, alpha);
				MODEL.cap1.render(0.065F);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			}
		} else {
			GlStateManager.color(1, 1, 1, 0.15F);
			GlStateManager.colorMask(false, false, false, false);
			MODEL.eye.render(0.065F);
			GlStateManager.colorMask(true, true, true, true);
			MODEL.eye.render(0.065F);
		}
		LightingUtil.INSTANCE.revert();
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFortressBossTeleporter p_110775_1_) {
		return null;
	}
}
