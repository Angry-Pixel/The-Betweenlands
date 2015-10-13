package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelWeedwoodRowboat;

public class RenderWeedwoodRowboat extends Render {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/weedwoodRowboat.png");

	protected ModelWeedwoodRowboat modelBoat;

	public RenderWeedwoodRowboat() {
		modelBoat = new ModelWeedwoodRowboat();
		shadowSize = 0;
	}

	public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
		EntityBoat boat = (EntityBoat) entity;
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y + 1, (float) z);
		GL11.glRotatef(180.0F - p_76986_8_, 0.0F, 1.0F, 0.0F);
		float f2 = boat.getTimeSinceHit() - p_76986_9_;
		float f3 = boat.getDamageTaken() - p_76986_9_;
		GL11.glRotatef(90f, 0f, 1f, 0f);

		if (f3 < 0.0F) {
			f3 = 0.0F;
		}

		if (f2 > 0.0F) {
			GL11.glRotatef(MathHelper.sin(f2) * f2 * f3 / 10.0F * boat.getForwardDirection(), 1.0F, 0.0F, 0.0F);
		}

		float f4 = 0.75F;
		GL11.glScalef(f4, f4, f4);
		GL11.glScalef(1.0F / f4, 1.0F / f4, 1.0F / f4);
		bindEntityTexture(entity);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		modelBoat.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
	}

	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}
