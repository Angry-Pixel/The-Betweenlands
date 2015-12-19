package thebetweenlands.client.render.entity;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelWeedwoodRowboat;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.utils.MathUtils;

public class RenderWeedwoodRowboat extends Render {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID + ":textures/entity/weedwoodRowboat.png");

	public static boolean notRenderingPilot = true;

	private ModelWeedwoodRowboat model = new ModelWeedwoodRowboat();

	private int maskId = -1;

	private Matrix4f rotationMatrix3D = new Matrix4f();

	private Matrix4f rotationMatrix2D = new Matrix4f();

	private AxisAngle4f rotation2D = new AxisAngle4f();

	private Point3f grip = new Point3f();

	private ArmArticulation[] arms = { new ArmArticulation(), new ArmArticulation() };

	public RenderWeedwoodRowboat() {
		rotationMatrix2D.setIdentity();
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yawn, float delta) {
		final boolean lines = false;
		EntityWeedwoodRowboat rowboat = (EntityWeedwoodRowboat) entity;
		model.animateOar(rowboat, EntityWeedwoodRowboat.LEFT_OAR, delta);
		model.animateOar(rowboat, EntityWeedwoodRowboat.RIGHT_OAR, delta);
		float yaw = 270 - yawn;
		GL11.glPushMatrix();
		GL11.glRotatef(yaw, 0, 1, 0);
		roll(rowboat, delta);
		GL11.glRotatef(-yaw, 0, 1, 0);
		if (lines) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
		}
		renderPilot(rowboat, x, y, z, yaw, delta);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslated(x, y + 1.05F, z);
		GL11.glRotatef(yaw, 0, 1, 0);
		roll(rowboat, delta);
		bindEntityTexture(entity);
		GL11.glScalef(-1, -1, 1);
		if (lines) {
			GL11.glColor3f(0, 0, 0);
		}
		model.render(rowboat, 0.0625F, delta);
		if (lines) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
		GL11.glScalef(-1, -1, 1);
		if (!lines) {
			renderWaterMask();
		}
		GL11.glPopMatrix();
	}

	private void roll(EntityWeedwoodRowboat rowboat, float delta) {
		float timeSinceHit = rowboat.getTimeSinceHit() - delta;
		float damageTaken = rowboat.getDamageTaken() - delta;
		if (damageTaken < 0) {
			damageTaken = 0;
		}
		if (timeSinceHit > 0) {
			GL11.glTranslatef(0, -1, 0);
			GL11.glRotatef(MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10 * rowboat.getHitRollDirection(), 0, 0, 1);
			GL11.glTranslatef(0, 1, 0);
		}
	}

	private void renderPilot(EntityWeedwoodRowboat boat, double x, double y, double z, float yaw, float delta) {
		Entity pilot = boat.riddenByEntity;
		if (pilot == null) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if (pilot != mc.thePlayer || mc.gameSettings.thirdPersonView != 0) {
			articulateArm(EntityWeedwoodRowboat.LEFT_OAR);
			articulateArm(EntityWeedwoodRowboat.RIGHT_OAR);
			notRenderingPilot = false;
			RenderPlayerRower.INSTANCE.renderPilot(pilot, arms[EntityWeedwoodRowboat.LEFT_OAR], arms[EntityWeedwoodRowboat.RIGHT_OAR], delta);
			notRenderingPilot = true;
		}
	}

	private void articulateArm(int side) {
		int dir = side == EntityWeedwoodRowboat.RIGHT_OAR ? 1 : -1;
		final float offsetZ = -4;
		float pivotX = 9 * dir, pivotY = -9, pivotZ = offsetZ;
		grip.set(9 * dir, -2, offsetZ);
		ModelRenderer leftOar = model.getOar(side);
		rotationMatrix3D.setIdentity();
		rotation2D.set(0, 0, 1, leftOar.rotateAngleZ);
		rotationMatrix2D.setRotation(rotation2D);
		rotationMatrix3D.mul(rotationMatrix2D);
		// the x and y rotation axes are negative because the rowboat is scaled as such
		rotation2D.set(-1, 0, 0, leftOar.rotateAngleX);
		rotationMatrix2D.setRotation(rotation2D);
		rotationMatrix3D.mul(rotationMatrix2D);
		rotation2D.set(0, -1, 0, leftOar.rotateAngleY);
		rotationMatrix2D.setRotation(rotation2D);
		rotationMatrix3D.mul(rotationMatrix2D);
		// move to origin
		grip.x -= pivotX;
		grip.y -= pivotY;
		grip.z -= pivotZ;
		rotationMatrix3D.transform(grip);
		// move to pivot
		grip.x += pivotX;
		grip.y += pivotY;
		grip.z += pivotZ;
		// the player is scaled by this amount
		final float ps = 0.9375F;
		float armX = 6 * ps * dir;
		// I haven't taken the time to fine the exact value of the rotation point y and z, but these are close enough
		float armY = -4.325F * ps;
		float armZ = 4.425F * ps;
		float targetX = grip.x - armX;
		float targetY = grip.y - armY;
		float targetZ = grip.z - armZ;
		float horizontalDistSq = targetX * targetX + targetZ * targetZ;
		float targetPitch = (float) Math.atan2(targetY, MathHelper.sqrt_float(horizontalDistSq));
		float targetLen = MathHelper.sqrt_float(horizontalDistSq + targetY * targetY);
		float upperArmLen = 4, lowerArmLen = 4.25F;
		float shoulderAngle = (float) Math.acos((upperArmLen * upperArmLen + targetLen * targetLen - lowerArmLen * lowerArmLen) / (2 * upperArmLen * targetLen));
		float flexionAngle = (float) Math.acos((upperArmLen * upperArmLen + lowerArmLen * lowerArmLen - targetLen * targetLen) / (2 * upperArmLen * lowerArmLen));
		// If shoulderAngle is NaN then the target is out of reach so the arm should simply point in that direction.
		ArmArticulation arm = arms[side];
		arm.setShoulderAngleX(shoulderAngle != shoulderAngle ? -MathUtils.PI / 2 - targetPitch : (shoulderAngle - MathUtils.PI / 2 - targetPitch));
		arm.setShoulderAngleY((float) Math.atan2(targetZ, targetX) + MathUtils.PI / 2);
		arm.setFlexionAngle(shoulderAngle != shoulderAngle ? 0 : (flexionAngle - MathUtils.PI) * MathUtils.RAD_TO_DEG);
	}

	private void renderWaterMask() {
		if (maskId == -1) {
			maskId = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(maskId, GL11.GL_COMPILE_AND_EXECUTE);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			double y = -0.687;
			double midWidth = 0.55;
			double midDepth = 0.65;
			double endWidth = 0.4;
			double endDepth = 0.16;
			double endOffset = 0.81;
			tessellator.addVertex(-midWidth, y, midDepth);
			tessellator.addVertex(midWidth, y, midDepth);
			tessellator.addVertex(midWidth, y, -midDepth);
			tessellator.addVertex(-midWidth, y, -midDepth);
			tessellator.addVertex(-endWidth, y, endDepth - endOffset);
			tessellator.addVertex(endWidth, y, endDepth - endOffset);
			tessellator.addVertex(endWidth, y, -endDepth - endOffset);
			tessellator.addVertex(-endWidth, y, -endDepth - endOffset);
			tessellator.addVertex(-endWidth, y, endDepth + endOffset);
			tessellator.addVertex(endWidth, y, endDepth + endOffset);
			tessellator.addVertex(endWidth, y, -endDepth + endOffset);
			tessellator.addVertex(-endWidth, y, -endDepth + endOffset);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColorMask(false, false, false, false);
			tessellator.draw();
			GL11.glColorMask(true, true, true, true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEndList();
		} else {
			GL11.glCallList(maskId);
		}
	}

	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}

	public static class ArmArticulation {
		private float shoulderAngleX;

		private float shoulderAngleY;

		private float flexionAngle;

		public void setShoulderAngleX(float shoulderAngleX) {
			this.shoulderAngleX = shoulderAngleX;
		}

		public float getShoulderAngleX() {
			return shoulderAngleX;
		}

		public void setShoulderAngleY(float shoulderAngleY) {
			this.shoulderAngleY = shoulderAngleY;
		}

		public float getShoulderAngleY() {
			return shoulderAngleY;
		}

		public void setFlexionAngle(float flexionAngle) {
			this.flexionAngle = flexionAngle;
		}

		public float getFlexionAngle() {
			return flexionAngle;
		}
	}
}
