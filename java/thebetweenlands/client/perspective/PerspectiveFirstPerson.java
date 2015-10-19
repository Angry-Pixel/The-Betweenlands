package thebetweenlands.client.perspective;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

public class PerspectiveFirstPerson extends Perspective {
	@Override
	protected final void orient(Minecraft mc, World world, EntityRenderer entityRenderer, EntityLivingBase viewer, double x, double y, double z, float yaw, float pitch, float delta) {
		float eye = viewer.yOffset - 1.62F;
		y -= eye;
		if (viewer.isPlayerSleeping()) {
			eye++;
			GL11.glTranslatef(0, 0.3F, 0);
			ForgeHooksClient.orientBedCamera(mc, viewer);
			GL11.glRotatef(yaw + 180, 0, -1, 0);
			GL11.glRotatef(pitch, -1, 0, 0);
		} else {
			orient(world, viewer, x, y, z, yaw, pitch);
		}
		GL11.glRotatef(pitch, 1, 0, 0);
		GL11.glRotatef(yaw + 180, 0, 1, 0);
		GL11.glTranslatef(0, eye, 0);
	}

	protected void orient(World world, EntityLivingBase viewer, double x, double y, double z, float yaw, float pitch) {
		GL11.glTranslatef(0, 0, -0.1F);
	}

	@Override
	protected void applyMovement(Entity entity, float x, float y) {
		float currentPitch = entity.rotationPitch;
		float currentYaw = entity.rotationYaw;
		entity.rotationYaw += x * 0.15F;
		entity.rotationPitch -= y * 0.15F;
		if (entity.rotationPitch < -90) {
			entity.rotationPitch = -90;
		}
		if (entity.rotationPitch > 90) {
			entity.rotationPitch = 90;
		}
		entity.prevRotationPitch += entity.rotationPitch - currentPitch;
		entity.prevRotationYaw += entity.rotationYaw - currentYaw;
	}

	@Override
	public boolean shouldRenderPlayer() {
		return false;
	}
}
