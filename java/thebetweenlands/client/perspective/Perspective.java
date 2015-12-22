package thebetweenlands.client.perspective;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.lwjgl.util.glu.GLU;

public abstract class Perspective {
	private static final List<Perspective> PERSPECTIVES = new ArrayList<Perspective>();

	public static final Perspective FIRST_PERSON = new PerspectiveFirstPerson();

	public static final Perspective THIRD_PERSON = new PerspectiveThirdPerson();

	public static final Perspective THIRD_PERSON_FRONT = new PerspectiveThirdPersonFront();

	static {
		register(FIRST_PERSON);
		register(THIRD_PERSON);
		register(THIRD_PERSON_FRONT);
	}

	private static float yaw;

	private static float pitch;

	private static int currentPerspective;

	private int id;

	private boolean doesRenderPlayer;

	public Perspective() {
		doesRenderPlayer = shouldRenderPlayer();
	}

	private void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public boolean shouldRenderPlayer() {
		return true;
	}

	protected boolean canCycleFrom(Perspective perspective) {
		return true;
	}

	protected void applyMovement(Entity entity, float x, float y) {}

	protected void orient(Minecraft mc, World world, EntityRenderer entityRenderer, EntityLivingBase viewer, double x, double y, double z, float yaw, float pitch, float delta) {}

	protected boolean canCycleTo(Perspective perspective) {
		return true;
	}

	protected void onCycleTo() {}

	protected void onCycleOff() {}

	protected void onChangeTo() {}

	protected void onChangeOff() {}

	public final void switchTo() {
		switchTo(this);
	}

	public final boolean doesRenderPlayer() {
		return doesRenderPlayer;
	}

	public final boolean isCurrentPerspective() {
		return this == getCurrentPerspective();
	}

	public static Perspective getCurrentPerspective() {
		return getPerspective(getCurrentPerspectiveId());
	}

	public static int getCurrentPerspectiveId() {
		return currentPerspective;
	}

	public static Perspective getPerspective(int view) {
		int viewCount = PERSPECTIVES.size();
		if (view < 0 || view >= viewCount) {
			throw new IllegalStateException(String.format("The view (%s) is an invalid camera view ordinal, should be in range [0,%s]. I can't believe you've done this.", view, viewCount - 1));
		}
		return PERSPECTIVES.get(view);
	}

	public static void register(Perspective perspective) {
		perspective.setId(PERSPECTIVES.size());
		PERSPECTIVES.add(perspective);
	}

	public static void updateRenderInfo(float winx, float winz, FloatBuffer modelview, FloatBuffer projection, IntBuffer viewport, FloatBuffer objectCoords) {
		GLU.gluUnProject(winx, winz, 1, modelview, projection, viewport, objectCoords);
		float lookVecX = objectCoords.get(0) - ActiveRenderInfo.objectX;
		float lookVecY = objectCoords.get(1) - ActiveRenderInfo.objectY;
		float lookVecZ = objectCoords.get(2) - ActiveRenderInfo.objectZ;
		float frustumDepth = MathHelper.sqrt_float(lookVecX * lookVecX + lookVecY * lookVecY + lookVecZ * lookVecZ);
		lookVecX /= frustumDepth;
		lookVecY /= frustumDepth;
		lookVecZ /= frustumDepth;
		yaw = (float) (Math.atan2(lookVecZ, lookVecX) - Math.PI / 2);
		if (yaw < -Math.PI) {
			yaw += 2 * Math.PI;
		}
		pitch = (float) -Math.atan2(lookVecY, Math.sqrt(lookVecX * lookVecX + lookVecZ * lookVecZ));
		float sinPitch = MathHelper.sin(pitch);
		ActiveRenderInfo.rotationX = MathHelper.cos(yaw);
		ActiveRenderInfo.rotationZ = MathHelper.sin(yaw);
		ActiveRenderInfo.rotationYZ = -ActiveRenderInfo.rotationZ * sinPitch;
		ActiveRenderInfo.rotationXY = ActiveRenderInfo.rotationX * sinPitch;
		ActiveRenderInfo.rotationXZ = MathHelper.cos(pitch);
	}

	public static boolean orient(float delta) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityLivingBase viewer = mc.renderViewEntity;
		double x = viewer.prevPosX + (viewer.posX - viewer.prevPosX) * delta;
		double y = viewer.prevPosY + (viewer.posY - viewer.prevPosY) * delta;
		double z = viewer.prevPosZ + (viewer.posZ - viewer.prevPosZ) * delta;
		float yaw = viewer.prevRotationYaw + (viewer.rotationYaw - viewer.prevRotationYaw) * delta;
		float pitch = viewer.prevRotationPitch + (viewer.rotationPitch - viewer.prevRotationPitch) * delta;
		getCurrentPerspective().orient(mc, mc.theWorld, mc.entityRenderer, viewer, x, y, z, yaw, pitch, delta);
		return mc.renderGlobal.hasCloudFog(x, y, z, delta);
	}

	public static int getInsideOpaqueBlockView() {
		Perspective perspective = getCurrentPerspective();
		Perspective newPerspective = perspective.getPerspectiveForOpaqueBlockView();
		if (newPerspective == null) {
			newPerspective = perspective;
		}
		changeTo(newPerspective);
		return newPerspective.doesRenderPlayer() ? currentPerspective : 0;
	}

	protected Perspective getPerspectiveForOpaqueBlockView() {
		return Perspective.FIRST_PERSON;
	}

	public static int cyclePerspective() {
		int currentView = getCurrentPerspectiveId();
		Perspective perspective = getPerspective(currentView);
		for (int i = 1; i < PERSPECTIVES.size(); i++) {
			Perspective candidate = getPerspective((currentView + i) % PERSPECTIVES.size());
			if (perspective.canCycleTo(candidate) && candidate.canCycleFrom(perspective)) {
				candidate.onCycleTo();
				perspective.onCycleOff();
				perspective = candidate;
				break;
			}
		}
		return cycleTo(perspective);
	}

	public static void switchTo(Perspective perspective) {
		changeTo(perspective);
		Minecraft.getMinecraft().gameSettings.thirdPersonView = perspective.doesRenderPlayer() ? currentPerspective : 0;
	}

	public static int cycleTo(Perspective perspective) {
		changeTo(perspective);
		return perspective.doesRenderPlayer() ? perspective.getId() : 0;
	}

	private static void changeTo(Perspective perspective) {
		getCurrentPerspective().onChangeOff();
		perspective.onChangeTo();
		currentPerspective = perspective.getId();
	}

	public static void cacheActiveRenderInfo() {
		RenderManager renderManager = RenderManager.instance;
		renderManager.playerViewY = yaw * (float) (180 / Math.PI);
		renderManager.playerViewX = pitch * (float) (180 / Math.PI);
		renderManager.viewerPosX += ActiveRenderInfo.objectX;
		renderManager.viewerPosY += ActiveRenderInfo.objectY;
		renderManager.viewerPosZ += ActiveRenderInfo.objectZ;
		TileEntityRendererDispatcher dispatcher = TileEntityRendererDispatcher.instance;
		dispatcher.field_147562_h = renderManager.playerViewY;
		dispatcher.field_147563_i = renderManager.playerViewX;
		dispatcher.field_147560_j = renderManager.viewerPosX;
		dispatcher.field_147561_k = renderManager.viewerPosY;
		dispatcher.field_147558_l = renderManager.viewerPosZ;
	}

	public static void setAngles(Entity entity, float x, float y) {
		getCurrentPerspective().applyMovement(entity, x, y);
	}
}
