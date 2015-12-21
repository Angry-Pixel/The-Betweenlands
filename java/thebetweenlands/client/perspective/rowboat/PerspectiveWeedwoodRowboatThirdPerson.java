package thebetweenlands.client.perspective.rowboat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.input.WeedwoodRowboatHandler;
import thebetweenlands.client.perspective.Perspective;
import thebetweenlands.utils.MathUtils;
import thebetweenlands.utils.confighandler.ConfigHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class PerspectiveWeedwoodRowboatThirdPerson extends Perspective {
	private float prevYaw;

	private float yaw;

	private float prevPitch;

	private float pitch;

	@Override
	protected boolean canCycleTo(Perspective perspective) {
		return perspective == WeedwoodRowboatHandler.FIRST_PERSON_PERSPECTIVE;
	}

	@Override
	protected boolean canCycleFrom(Perspective perspective) {
		return WeedwoodRowboatHandler.INSTANCE.isPlayerInRowboat();
	}

	@Override
	protected void onCycleTo() {
		ConfigHandler.rowboatView = true;
		ConfigHandler.INSTANCE.save();
	}

	@Override
	protected void onChangeTo() {
		GuiIngameForge.renderCrosshairs = false;
	}

	@Override
	protected void onChangeOff() {
		GuiIngameForge.renderCrosshairs = true;
	}

	@Override
	protected void applyMovement(Entity entity, float x, float y) {
		float currentPitch = pitch;
		float currentYaw = yaw;
		yaw += x * 0.15F;
		pitch -= y * 0.15F;
		if (pitch < 0) {
			pitch = 0;
		}
		if (pitch > 90) {
			pitch = 90;
		}
		prevPitch += pitch - currentPitch;
		prevYaw += yaw - currentYaw;
	}

	@Override
	protected void orient(Minecraft mc, World world, EntityRenderer entityRenderer, EntityLivingBase viewer, double x, double y, double z, float viewerYaw, float viewerPitch, float delta) {
		float yaw = this.prevYaw + (this.yaw - this.prevYaw) * delta;
		float pitch = this.prevPitch + (this.pitch - this.prevPitch) * delta;
		GL11.glTranslatef(0, 0, (float) -getDistance(world, x, y, z, yaw, pitch));
		GL11.glRotatef(pitch, 1, 0, 0);
		GL11.glRotatef(yaw + 180, 0, 1, 0);
	}

	private double getDistance(World world, double x, double y, double z, float yaw, float pitch) {
		double extent = 5;
		float cosPitch = MathHelper.cos(pitch * MathUtils.DEG_TO_RAD);
		double extentX = -MathHelper.sin(yaw * MathUtils.DEG_TO_RAD) * cosPitch * extent;
		double extentZ = MathHelper.cos(yaw * MathUtils.DEG_TO_RAD) * cosPitch * extent;
		double extentY = -MathHelper.sin(pitch * MathUtils.DEG_TO_RAD) * extent;
		for (int zyx = 0; zyx < 8; zyx++) {
			float dx = ((zyx & 1) * 2 - 1) * 0.1F;
			float dy = ((zyx >> 1 & 1) * 2 - 1) * 0.1F;
			float dz = ((zyx >> 2 & 1) * 2 - 1) * 0.1F;
			MovingObjectPosition vector = world.rayTraceBlocks(Vec3.createVectorHelper(x + dx, y + dy, z + dz), Vec3.createVectorHelper(x - extentX + dx, y - extentY + dy, z - extentZ + dz));
			if (vector != null) {
				double distance = vector.hitVec.distanceTo(Vec3.createVectorHelper(x, y, z));
				if (distance < extent) {
					extent = distance;
				}
			}
		}
		return extent;
	}

	@SubscribeEvent
	protected void update(TickEvent.ClientTickEvent event) {
		if (getCurrentPerspective() == this && event.phase == TickEvent.Phase.START) {
			return;
		}
		if (yaw > 180) {
			yaw -= 360;
		}
		if (yaw < -180) {
			yaw += 360;
		}
		if (pitch > 180) {
			pitch -= 360;
		}
		if (pitch < -180) {
			pitch += 360;
		}
		prevYaw = yaw;
		prevPitch = pitch;
	}
}
