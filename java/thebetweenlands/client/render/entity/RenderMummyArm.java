package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelPeatMummy;
import thebetweenlands.entities.mobs.EntityMummyArm;

@SideOnly(Side.CLIENT)
public class RenderMummyArm extends Render {
	private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/peatMummy.png");
	private static final ModelPeatMummy MODEL = new ModelPeatMummy();


	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		EntityMummyArm arm = (EntityMummyArm) entity;

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(x, y + arm.yOffset, z);
		GL11.glScaled(0.7F, 0.7F, 0.7F);

		this.bindTexture(texture);
		MODEL.setToInitPose();
		MODEL.armleft.rotateAngleY = (float) Math.toRadians(yaw);
		MODEL.armleft.rotateAngleX = 0.3F;
		MODEL.armleft.rotateAngleZ = -0.1F;
		float offset = 0.0F;
		if(arm.attackSwing > 0) {
			MODEL.walk(MODEL.armleft, 1, -0.4F, false, offset, 0, ((float)Math.abs(arm.attackSwing-10)/2.0F+Math.signum(arm.attackSwing-10)*-1*partialTicks/2.0F), 1);
			MODEL.walk(MODEL.armleft2, 1, -0.6F, false, offset, 0, ((float)Math.abs(arm.attackSwing-10)/2.0F+Math.signum(arm.attackSwing-10)*-1*partialTicks/2.0F), 1);
		}
		if(arm.hurtTime > 0) {
			MODEL.bob(MODEL.armleft, 0.5F, -10, true, (arm.hurtTime-partialTicks) / 5.0F, 1);
		}
		MODEL.swing(MODEL.armleft, 1, 0.4f, false, offset, 0, (entity.ticksExisted + partialTicks) / 10.0F, 1);
		MODEL.walk(MODEL.armleft2, 1, 0.4f, false, offset, 0, (entity.ticksExisted + partialTicks) / 8.0F, 1);
		MODEL.swing(MODEL.armleft2, 1, 0.1f, false, offset, 0, (entity.ticksExisted + partialTicks) / 4.0F, 1);
		MODEL.walk(MODEL.armleft, 1, 0.1F, false, offset, 0, (entity.ticksExisted + partialTicks) / 10.0F, 1);
		MODEL.walk(MODEL.armleft, 2, 0.3F, false, offset, 0, (entity.ticksExisted + partialTicks) / 15.0F, 1);
		MODEL.bob(MODEL.armleft, 2, 1.8F, false, (entity.ticksExisted + partialTicks) / 15.0F, 1);
		MODEL.armleft.render(0.065F);

		GL11.glPopMatrix();
	}
}