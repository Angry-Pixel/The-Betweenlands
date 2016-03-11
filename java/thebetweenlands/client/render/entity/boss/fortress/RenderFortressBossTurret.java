package thebetweenlands.client.render.entity.boss.fortress;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.entities.mobs.boss.fortress.EntityFortressBossTurret;

public class RenderFortressBossTurret extends Render {
	private static final ModelWight modelHeadOnly = new ModelWight().setRenderHeadOnly(true);

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		EntityFortressBossTurret turret = (EntityFortressBossTurret) entity;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(x, y, z);
		this.bindTexture(new ResourceLocation("thebetweenlands:textures/entity/wight.png"));
		GL11.glRotated(180, 1, 0, 0);
		GL11.glRotated(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0, 1, 0);
		GL11.glRotated(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1, 0, 0);
		GL11.glTranslated(0, 0, 0.25D);
		GL11.glTranslated(Math.sin((entity.ticksExisted + partialTicks)/5.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/7.0D) * 0.1F, Math.cos((entity.ticksExisted + partialTicks)/6.0D) * 0.1F);
		if(!turret.isObstructedByBoss()) {
			GL11.glColor4f(1, 1, 1, 0.8F);
		} else {
			GL11.glColor4f(1, 0.4F, 0.4F, 0.8F);
		}
		modelHeadOnly.render(entity, entity.distanceWalkedModified, 360, entity.ticksExisted + partialTicks, 0, 0, 0.065F);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}
}
