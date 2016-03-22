package thebetweenlands.client.render.entity.boss.fortress;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelFortressBoss;
import thebetweenlands.entities.mobs.boss.fortress.EntityFortressBossTeleporter;
import thebetweenlands.utils.LightingUtil;

public class RenderFortressBossTeleporter extends Render {
	private static final ResourceLocation MODEL_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/fortressBoss.png");
	private static final ModelFortressBoss MODEL = new ModelFortressBoss();

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		EntityFortressBossTeleporter tp = (EntityFortressBossTeleporter) entity;

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(x, y, z);

		this.bindTexture(MODEL_TEXTURE);
		GL11.glRotated(180, 1, 0, 0);
		GL11.glTranslated(0, -0.25D, 0.1D);
		GL11.glTranslated(MODEL.eye.rotationPointX * 0.065F / 2.0F, MODEL.eye.rotationPointY * 0.065F / 2.0F, MODEL.eye.rotationPointZ * 0.065F / 2.0F);
		if(tp.getTarget() != null) {
			GL11.glRotated(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0, 1, 0);
			GL11.glRotated(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1, 0, 0);
		} else {
			GL11.glRotated(-92, 1, 0, 0);
		}
		GL11.glTranslated(-MODEL.eye.rotationPointX * 0.065F / 2.0F, -MODEL.eye.rotationPointY * 0.065F / 2.0F + 0.3D, -MODEL.eye.rotationPointZ * 0.065F / 2.0F);
		if(tp.getTarget() != null)
			GL11.glTranslated(Math.sin((entity.ticksExisted + partialTicks)/5.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/7.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/6.0D) * 0.1F);
		GL11.glScaled(0.8F, 0.8F, 0.8F);
		GL11.glDisable(GL11.GL_CULL_FACE);
		LightingUtil.INSTANCE.setLighting(255);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if(tp.getTarget() != null) {
			GL11.glDepthMask(false);
			GL11.glColor4f(1, 1, 1, 0.5F);
			GL11.glColorMask(false, false, false, false);
			MODEL.eye.render(0.065F);
			GL11.glColorMask(true, true, true, true);
			MODEL.eye.render(0.065F);
			GL11.glDepthMask(true);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_ALPHA);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glColorMask(false, false, false, false);
			MODEL.eye.render(0.065F);
			GL11.glColorMask(true, true, true, true);
			MODEL.eye.render(0.065F);
		} else {
			GL11.glColor4f(1, 1, 1, 0.15F);
			GL11.glColorMask(false, false, false, false);
			MODEL.eye.render(0.065F);
			GL11.glColorMask(true, true, true, true);
			MODEL.eye.render(0.065F);
		}
		LightingUtil.INSTANCE.revert();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1, 1, 1, 1);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}
}
