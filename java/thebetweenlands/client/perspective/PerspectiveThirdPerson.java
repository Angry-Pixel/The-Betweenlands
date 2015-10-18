package thebetweenlands.client.perspective;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class PerspectiveThirdPerson extends PerspectiveFirstPerson {
	@Override
	protected final void orient(World world, EntityLivingBase viewer, double x, double y, double z, float yaw, float pitch) {
		double extent = 4;
		if (isFrontFacing()) {
			pitch += 180;
		}
		double extentX = -MathHelper.sin(yaw / (180 / (float) Math.PI)) * MathHelper.cos(pitch / 180 * (float) Math.PI) * extent;
		double extentZ = MathHelper.cos(yaw / 180 * (float) Math.PI) * MathHelper.cos(pitch / 180 * (float) Math.PI) * extent;
		double extentY = -MathHelper.sin(pitch / 180 * (float) Math.PI) * extent;
		for (int zyx = 0; zyx < 8; zyx++) {
			float dx = ((zyx & 1) * 2 - 1) * 0.1F;
			float dy = ((zyx >> 1 & 1) * 2 - 1) * 0.1F;
			float dz = ((zyx >> 2 & 1) * 2 - 1) * 0.1F;
			MovingObjectPosition movingobjectposition = world.rayTraceBlocks(Vec3.createVectorHelper(x + dx, y + dy, z + dz), Vec3.createVectorHelper(x - extentX + dx, y - extentY + dy, z - extentZ + dz));
			if (movingobjectposition != null) {
				double dist = movingobjectposition.hitVec.distanceTo(Vec3.createVectorHelper(x, y, z));
				if (dist < extent) {
					extent = dist;
				}
			}
		}
		if (isFrontFacing()) {
			GL11.glRotatef(180, 0, 1, 0);
		}
		GL11.glRotatef(viewer.rotationPitch - pitch, 1, 0, 0);
		GL11.glRotatef(viewer.rotationYaw - yaw, 0, 1, 0);
		GL11.glTranslatef(0, 0, (float) -extent);
		GL11.glRotatef(yaw - viewer.rotationYaw, 0, 1, 0);
		GL11.glRotatef(pitch - viewer.rotationPitch, 1, 0, 0);
	}

	protected boolean isFrontFacing() {
		return false;
	}

	@Override
	public boolean shouldRenderPlayer() {
		return true;
	}
}
