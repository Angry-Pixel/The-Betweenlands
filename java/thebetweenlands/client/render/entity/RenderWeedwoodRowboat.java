package thebetweenlands.client.render.entity;

import java.util.EnumMap;

import javax.vecmath.Point3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelWeedwoodRowboat;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.entities.rowboat.ShipSide;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.utils.CubicBezier;
import thebetweenlands.utils.MathUtils;
import thebetweenlands.utils.Matrix;

public class RenderWeedwoodRowboat extends Render {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID + ":textures/entity/weedwoodRowboat.png");

	private static final CubicBezier PULL_CURVE = new CubicBezier(1, 0, 1, 0.25F);

	public static boolean shouldPreventRidingRender = true;

	private RenderPlayerRower rowerRenderer = new RenderPlayerRower();

	private ModelWeedwoodRowboat model = new ModelWeedwoodRowboat();

	private int maskId = -1;

	private Matrix matrix = new Matrix(16);

	private EnumMap<ShipSide, Point3f> grips = ShipSide.newEnumMap(Point3f.class, new Point3f(), new Point3f());

	private EnumMap<ShipSide, ArmArticulation> arms = ShipSide.newEnumMap(ArmArticulation.class, new ArmArticulation(), new ArmArticulation());

	private Point3f arm = new Point3f();

	private float bodyRotateAngleX;

	private float bodyRotateAngleY;

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yawn, float delta) {
		final boolean lines = false;
		EntityWeedwoodRowboat rowboat = (EntityWeedwoodRowboat) entity;
		model.animateOar(rowboat, ShipSide.STARBOARD, delta);
		model.animateOar(rowboat, ShipSide.PORT, delta);
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
		if (!(pilot instanceof EntityPlayer)) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if (pilot != mc.thePlayer || mc.gameSettings.thirdPersonView != 0) {
			calculateGrip(ShipSide.STARBOARD);
			calculateGrip(ShipSide.PORT);
			articulateBody();
			articulateArm(ShipSide.STARBOARD, x, y, z, yaw);
			articulateArm(ShipSide.PORT, x, y, z, yaw);
			shouldPreventRidingRender = false;
			AbstractClientPlayer player = (AbstractClientPlayer) pilot;
			rowerRenderer.renderPilot(pilot, arms.get(ShipSide.STARBOARD), arms.get(ShipSide.PORT), bodyRotateAngleX, bodyRotateAngleY, delta);
			shouldPreventRidingRender = true;
		}
	}

	private void calculateGrip(ShipSide side) {
		int dir = side == ShipSide.PORT ? 1 : -1;
		float pivotX = 9 / 16F * dir, pivotY = -10 / 16F, pivotZ = -7 / 16F;
		createOarTransformationMatrix(side);
		Point3f grip = grips.get(side);
		grip.set(0, 6 / 16F, 0);
		matrix.transform(grip);
		grip.x += pivotX;
		grip.y += pivotY;
		grip.z += pivotZ;
	}

	private void createOarTransformationMatrix(ShipSide side) {
		ModelRenderer oar = model.getOar(side);
		matrix.setIdentity();
		matrix.rotate(oar.rotateAngleZ, 0, 0, 1);
		matrix.rotate(oar.rotateAngleX, -1, 0, 0);
		matrix.rotate(oar.rotateAngleY, 0, -1, 0);
	}

	private void articulateBody() {
		float leftZ = grips.get(ShipSide.STARBOARD).z;
		float rightZ = grips.get(ShipSide.PORT).z;
		float tilt = (float) Math.atan2(leftZ, rightZ) + 3 * MathUtils.PI / 4;
		bodyRotateAngleY = tilt * 0.65F;
		float straightness = 1 - Math.abs(tilt) / (MathUtils.PI / 4);
		if (straightness > 1) {
			straightness = 1;
		}
		if (straightness < 0) {
			straightness = 0;
		}
		float z = (leftZ + rightZ) / 2;
		float y = (grips.get(ShipSide.STARBOARD).y + grips.get(ShipSide.PORT).y) / 2;
		float forward = Math.abs(z);
		float downward = (-y - 0.3F) / 0.35F;
		if (downward > 1) {
			downward = 1;
		}
		if (downward < 0) {
			downward = 0;
		}
		float upward = 0;
		if (downward < 0.6F) {
			upward = MathUtils.linearTransformf(downward, 0, 0.6F, 1, 0);
			upward = PULL_CURVE.eval(upward) * 0.6F;
		}
		float lean = (forward + (downward * 0.1F)) * (1 - upward) + upward * 0.2F;
		bodyRotateAngleX = MathUtils.linearTransformf(lean, 0.2F, 0.72F, -0.45F, 0.5F);
	}

	private void articulateArm(ShipSide side, double x, double y, double z, float yaw) {
		int dir = side == ShipSide.PORT ? 1 : -1;
		// the player is scaled by this amount
		final float ps = 0.9375F;
		createBodyTransformationMatrix();
		// move to shoulder joint
		matrix.translate(-6 / 16F * dir, -10 / 16F, 0);
		arm.set(0, 0, 0);
		matrix.transform(arm);
		Point3f grip = grips.get(side);
		float targetX = grip.x - arm.x;
		float targetY = grip.y - arm.y;
		float targetZ = grip.z - arm.z;
		float horizontalDistSq = targetX * targetX + targetZ * targetZ;
		float targetPitch = (float) Math.atan2(targetY, MathHelper.sqrt_float(horizontalDistSq));
		float targetLen = MathHelper.sqrt_float(horizontalDistSq + targetY * targetY);
		float upperArmLen = 4 / 16F, lowerArmLen = 4.25F / 16;
		float shoulderAngle = (float) Math.acos((upperArmLen * upperArmLen + targetLen * targetLen - lowerArmLen * lowerArmLen) / (2 * upperArmLen * targetLen));
		float flexionAngle = (float) Math.acos((upperArmLen * upperArmLen + lowerArmLen * lowerArmLen - targetLen * targetLen) / (2 * upperArmLen * lowerArmLen));
		// If shoulderAngle is NaN then the target is out of reach so the arm should simply point in that direction.
		ArmArticulation armArt = arms.get(side);
		armArt.setShoulderAngleX((shoulderAngle != shoulderAngle ? -MathUtils.PI / 2 - targetPitch : (shoulderAngle - MathUtils.PI / 2 - targetPitch)) - bodyRotateAngleX);
		armArt.setShoulderAngleY((float) Math.atan2(targetZ, targetX) + MathUtils.PI / 2 - bodyRotateAngleY);
		armArt.setFlexionAngle(shoulderAngle != shoulderAngle ? 0 : (flexionAngle - MathUtils.PI) * MathUtils.RAD_TO_DEG);
		if (shoulderAngle != shoulderAngle) {
			GL11.glColor3f(1, 0, 0);
		} else {
			if ((flexionAngle - MathUtils.PI) * MathUtils.RAD_TO_DEG < -90) {
				GL11.glColor3f(1, 1, 0);
			} else {
				GL11.glColor3f(0, 1, 0);
			}
		}
		GL11.glPointSize(8);
		GL11.glRotatef(yaw, 0, 1, 0);
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glVertex3d(arm.x, arm.y, arm.z);
		GL11.glVertex3d(grip.x, grip.y, grip.z);
		GL11.glEnd();
		GL11.glLineWidth(3);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(arm.x, arm.y, arm.z);
		GL11.glVertex3d(grip.x, grip.y, grip.z);
		GL11.glEnd();
		GL11.glLineWidth(1);
		GL11.glRotatef(-yaw, 0, 1, 0);
	}

	private void createBodyTransformationMatrix() {
		matrix.setIdentity();
		// player yOffset
		matrix.translate(0, -1.62, 0);
		// regular scale
		matrix.scale(-1, -1, 1);
		// player scale
		matrix.scale(15 / 16F, 15 / 16F, 15 / 16F);
		// regular translate
		matrix.translate(0, -24 / 16F - 0.0078125, 0);
		// body rotation point
		matrix.translate(0, 12 / 16F, 0);
		matrix.rotate(bodyRotateAngleY, 0, 1, 0);
		matrix.rotate(bodyRotateAngleX, 1, 0, 0);
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

	@Override
	public void setRenderManager(RenderManager renderManager) {
		super.setRenderManager(renderManager);
		rowerRenderer.setRenderManager(renderManager);
	}

	@Override
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
