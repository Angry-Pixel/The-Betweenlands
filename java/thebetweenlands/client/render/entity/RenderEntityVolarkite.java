package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.item.ModelVolarkite;
import thebetweenlands.entities.EntityVolarkite;

public class RenderEntityVolarkite extends Render {

	private static final float ONGROUND_ROTATION = 90F;

	private final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/items/volarkite.png");
	private final ModelVolarkite model = new ModelVolarkite();

	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime) {

		EntityVolarkite kite = (EntityVolarkite) entity;
		GL11.glPushMatrix();
		float rotation = interpolateRotation(kite.prevRotationYaw, kite.rotationYaw, partialTickTime);
		double x2 = Math.cos(Math.toRadians(rotation + 90)) * -0.5;
		double z2 = Math.sin(Math.toRadians(rotation + 90)) * -0.5;
		if (kite.getPlayer() == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
			GL11.glTranslatef((float) x, (float) y - 1.5F, (float) z);
		else {
			if (kite.getPlayer() != null && kite.isPlayerOnGround())
				GL11.glTranslatef((float) x, (float) y - 1.5F, (float) z);
			else
				GL11.glTranslatef((float) x + (float) x2, (float) y - 1.55F, (float) z + (float) z2);
		}
		GL11.glRotatef(180.0F - rotation, 0.0F, 1.0F, 0.0F);
		if (kite.getPlayer() != null && kite.isPlayerOnGround()) {
			GL11.glTranslatef(0F, 0F, -0.2F);
			GL11.glRotatef(ONGROUND_ROTATION, 1F, 0F, 0F);
			GL11.glScalef(0.4F, 1F, 0.4F);
		}
		GL11.glPushMatrix();
		bindTexture(TEXTURE);
		GL11.glRotatef(180F, 0f, 1F, 0F);
		GL11.glRotatef(-180F, 1F, 0F, 0F);
		model.render();
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	private static float interpolateRotation(float prevRotation, float nextRotation, float modifier) {
		float rotation = nextRotation - prevRotation;
		while (rotation < -180.0F)
			rotation += 360.0F;
		while (rotation >= 180.0F)
			rotation -= 360.0F;
		return prevRotation + modifier * rotation;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}
