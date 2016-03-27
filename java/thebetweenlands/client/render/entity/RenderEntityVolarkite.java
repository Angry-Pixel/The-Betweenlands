package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.item.ModelVolarkite;
import thebetweenlands.entities.EntityVolarkite;

public class RenderEntityVolarkite extends Render {

	private static final float ONGROUND_ROTATION = 90f;

	private final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/items/volarkite.png");
	private final ModelVolarkite model = new ModelVolarkite();
	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1) {

		EntityVolarkite kite = (EntityVolarkite)entity;
		GL11.glPushMatrix();
		float rotation = interpolateRotation(kite.prevRotationYaw, kite.rotationYaw, f1);
		double x2 = Math.cos(Math.toRadians(rotation + 90)) * -0.5;
		double z2 = Math.sin(Math.toRadians(rotation + 90)) * -0.5;

		/* Only shift to first person if FP and we're on kite */
		if (kite.getPlayer() == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			GL11.glTranslatef((float)x, (float)y - 1.5F, (float)z);
		} else {
			if (kite.getPlayer() != null && kite.isPlayerOnGround()) {
				GL11.glTranslatef((float)x, (float)y -1.5F, (float)z);
			} else {
				GL11.glTranslatef((float)x + (float)x2, (float)y - 1.55f, (float)z + (float)z2);
			}
		}

		// Maybe this should be pushed in to the matrix and popped out too
		GL11.glRotatef(180.0F - rotation, 0.0F, 1.0F, 0.0F);


		/* Rotate kite backwards when the player hits the ground */
		if (kite.getPlayer() != null && kite.isPlayerOnGround()) {
			GL11.glTranslatef(0f, 0f, -0.2f);
			GL11.glRotatef(ONGROUND_ROTATION, 1f, 0f, 0f);
			GL11.glScalef(0.4f, 1f, 0.4f);
		}

		// Push matrix to hold it's location for rendering other stuff */
		GL11.glPushMatrix();
		bindTexture(TEXTURE);
		GL11.glRotatef(180F, 0f, 1f, 0f);
		GL11.glRotatef(-180F, 1f, 0f, 0f);
		model.render();
		GL11.glPopMatrix();

		// Render other stuff here if you wish
		GL11.glPopMatrix();
	}

	/* Interpolate rotation */
	private static float interpolateRotation(float prevRotation, float nextRotation, float modifier) {
		float rotation = nextRotation - prevRotation;

		while (rotation < -180.0F)
			rotation += 360.0F;

		while (rotation >= 180.0F) {
			rotation -= 360.0F;
		}

		return prevRotation + modifier * rotation;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}


