package thebetweenlands.client.renderer.entity.rowboat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.rowboat.ArmArticulation;
import thebetweenlands.client.model.entity.rowboat.RowboatLanternModel;
import thebetweenlands.client.model.entity.rowboat.WeedwoodRowboatModel;
import thebetweenlands.client.renderer.entity.FireflyRenderer;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.rowboat.RowboatLantern;
import thebetweenlands.common.entity.rowboat.ShipSide;
import thebetweenlands.common.entity.rowboat.WeedwoodRowboat;
import thebetweenlands.util.CubicBezier;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.Matrix;
import thebetweenlands.util.Quat;

import java.util.EnumMap;

public class WeedwoodRowboatRenderer extends EntityRenderer<WeedwoodRowboat> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/weedwood_rowboat.png");
	public static final ResourceLocation TEXTURE_TAR_OVERLAY = TheBetweenlands.prefix("textures/entity/weedwood_rowboat_tar_overlay.png");

	private static final CubicBezier PULL_CURVE = new CubicBezier(1, 0, 1, 0.25F);

	private final RowerRenderer rowerDefaultRender;
	private final RowerRenderer rowerSlimRender;
	private final WeedwoodRowboatModel model;
	private final RowboatLanternModel lanternModel;

	private final Matrix matrix = new Matrix();
	private final EnumMap<ShipSide, Vec3> grips = ShipSide.newEnumMap(Vec3.class, Vec3.ZERO, Vec3.ZERO);
	private final EnumMap<ShipSide, ArmArticulation> arms = ShipSide.newEnumMap(ArmArticulation.class, new ArmArticulation(), new ArmArticulation());
	private final EnumMap<ShipSide, Float> shoulderZ = ShipSide.newEnumMap(float.class);

	private float bodyRotateAngleX;
	private float bodyRotateAngleY;

	public WeedwoodRowboatRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new WeedwoodRowboatModel(context.bakeLayer(BLModelLayers.WEEDWOOD_ROWBOAT));
		this.lanternModel = new RowboatLanternModel(context.bakeLayer(BLModelLayers.WEEDWOOD_ROWBOAT_LANTERN));
		this.rowerDefaultRender = new RowerRenderer(context, false);
		this.rowerSlimRender = new RowerRenderer(context, true);
	}

	@Override
	public void render(WeedwoodRowboat entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		double wave = entity.getWaveHeight(partialTick);
		RowboatLantern lantern = entity.getLantern();
		Vec3 anchor = Vec3.ZERO, lanternLight = Vec3.ZERO;
		if (lantern != null) {
			anchor = entity.getLocalLanternPosition(partialTick);
			Matrix m = new Matrix();
			m.translate(
				Mth.lerp(partialTick, entity.xOld, entity.getX()) + anchor.x,
				Mth.lerp(partialTick, entity.yOld, entity.getY()) + anchor.y + wave,
				Mth.lerp(partialTick, entity.zOld, entity.getZ()) + anchor.z
			);
			m.rotate(lantern.getAngle(partialTick), 1, 0, 0);
			m.translate(0, -3.5 / 16, 0);
			lanternLight = m.transform(Vec3.ZERO);
		}
		float scale = 0.6F;
		this.model.animateOars(entity, partialTick);
		stack.pushPose();
		stack.translate(0.0D, wave, 0.0D);
		stack.pushPose();
		this.roll(entity, stack, entityYaw, partialTick);
		stack.scale(1, -1, -1);
		this.model.setLanternMount(lantern != null);
		this.model.renderToBuffer(stack, buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);

		if (!entity.isUnderWater()) {
			this.model.waterPatch().render(stack, buffer.getBuffer(RenderType.waterMask()), light, OverlayTexture.NO_OVERLAY);
		}

		if (entity.isTarred()) {
			this.model.renderToBuffer(stack, buffer.getBuffer(this.model.renderType(TEXTURE_TAR_OVERLAY)), light, OverlayTexture.NO_OVERLAY);
		}

		stack.popPose();
		if (lantern != null) {
			stack.pushPose();
			stack.translate(anchor.x, anchor.y, anchor.z);
			stack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
			stack.scale(1, -1, -1);
			this.lanternModel.render(lantern, partialTick, stack, buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY, -1);
			if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
				FireflyRenderer.addFireflyLight(lanternLight.x, lanternLight.y, lanternLight.z, scale * 7.0F);
			}
			stack.translate(0.0D, 0.25D, 0.0D);
			stack.scale(1, -1, -1);
			stack.mulPose(Axis.YP.rotationDegrees(entityYaw));
			FireflyRenderer.renderFireflyGlow(stack, buffer.getBuffer(RenderType.entityTranslucentEmissive(FireflyRenderer.GLOW)), scale);
			stack.popPose();
		}

		stack.popPose();
	}

	private void roll(WeedwoodRowboat rowboat, PoseStack stack, float yaw, float partialTick) {
		Quat rot = rowboat.getRotation(partialTick);
		stack.mulPose(new Quaternionf((float) rot.x, (float) rot.y, (float) rot.z, (float) rot.w));
		stack.mulPose(Axis.YP.rotationDegrees(-yaw - 180));
		float roll = rowboat.getRoll(partialTick);
		if (roll != 0) {
			stack.mulPose(Axis.ZP.rotationDegrees(roll));
		}
	}

	public void setupAndRenderRower(WeedwoodRowboat rowboat, AbstractClientPlayer player, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		this.model.animateOars(rowboat, partialTick);
		this.calculateGrip(rowboat, partialTick);
		this.articulateBody(rowboat, partialTick);
		this.articulateArms();
		RowerRenderer render = player.getSkin().model().equals(PlayerSkin.Model.SLIM) ? this.rowerSlimRender : this.rowerDefaultRender;
		render.render(player, this.arms.get(ShipSide.STARBOARD), this.arms.get(ShipSide.PORT), this.bodyRotateAngleX, this.bodyRotateAngleY, partialTick, stack, buffer, light);
	}

	private void calculateGrip(WeedwoodRowboat rowboat, float partialTick) {
		for (ShipSide side : ShipSide.values()) {
			int dir = side == ShipSide.PORT ? 1 : -1;
			this.matrix.setIdentity();
			float yaw = rowboat.getViewYRot(partialTick) * Mth.DEG_TO_RAD;
			double pelvis = 0.75;
			this.matrix.translate(0, pelvis - 1.5, 0);
			this.matrix.rotate(yaw, 0, 1, 0);
			this.matrix.rotate(rowboat.getRotation(partialTick));
			this.matrix.rotate(-yaw, 0, 1, 0);
			this.matrix.translate(0, 1.5, 0);
			this.matrix.scale(-1, -1, 1);
			this.matrix.translate(-9 / 16D * dir, pelvis + 10 / 16D, -7 / 16D);
			this.createOarTransformationMatrix(side);
			this.grips.put(side, this.matrix.transform(new Vec3(0, -6 / 16D, 0)));
		}
	}

	private void createOarTransformationMatrix(ShipSide side) {
		ModelPart oar = this.model.getOar(side);
		this.matrix.rotate(oar.zRot, 0, 0, 1);
		this.matrix.rotate(oar.xRot, 1, 0, 0);
		this.matrix.rotate(oar.yRot, 0, 1, 0);
	}

	private void articulateBody(WeedwoodRowboat rowboat, float partialTick) {
		float pow = rowboat.getPilotPower(partialTick);
		float leftZ = (float) this.grips.get(ShipSide.STARBOARD).z;
		float rightZ = (float) this.grips.get(ShipSide.PORT).z;
		float generalX = 0, generalY = 0, powerX = 0;
		if (pow != 0) {
			float port = rowboat.getRowProgress(ShipSide.PORT, partialTick);
			powerX = Mth.sin((port + 0.03F) * WeedwoodRowboat.OAR_ROTATION_SCALE) * 0.32F + 0.05F;
		}
		if (pow != 1) {
			float tilt = (float) Math.atan2(leftZ, rightZ) + 3 * Mth.PI / 4;
			generalY = tilt * 0.75F;
			float z = (leftZ + rightZ) / 2;
			float y = (float) (this.grips.get(ShipSide.STARBOARD).y + this.grips.get(ShipSide.PORT).y) / 2;
			float forward = -z * MathUtils.linearTransformf(Math.abs(leftZ + rightZ), 0, 0.05F, 1.1F, 1);
			float downward = Mth.clamp((-y - 0.3F) / 0.35F, 0, 1);
			float upward;
			if (downward < 0.6F) {
				upward = MathUtils.linearTransformf(downward, 0, 0.6F, 1, 0);
				upward = PULL_CURVE.eval(upward) * 0.6F;
			} else {
				upward = 0;
			}
			float lean = (forward + (downward * 0.1F)) * (1 - upward) + upward * 0.2F;
			generalX = MathUtils.linearTransformf(lean, 0.2F, 0.72F, -0.45F, 0.5F);
		}
		this.bodyRotateAngleX = generalX + (powerX - generalX) * pow;
		this.bodyRotateAngleY = generalY * (1 - pow);
		this.shoulderZ.put(ShipSide.STARBOARD, Mth.clamp((leftZ + 0.44F) * 0.45F - 0.02F, -0.1F, 0.1F));
		this.shoulderZ.put(ShipSide.PORT, Mth.clamp((rightZ + 0.44F) * 0.45F - 0.02F, -0.1F, 0.1F));
	}

	private void articulateArms() {
		for (ShipSide side : ShipSide.values()) {
			int dir = side == ShipSide.PORT ? 1 : -1;
			this.createBodyTransformationMatrix();
			// move to shoulder joint
			this.matrix.translate(-6 / 16F * dir, -10 / 16F, this.shoulderZ.get(side));
			Vec3 arm = this.matrix.transform(Vec3.ZERO);
			Vec3 grip = this.grips.get(side);
			float targetX = (float) (grip.x - arm.x);
			float targetY = (float) (grip.y - arm.y);
			float targetZ = (float) (grip.z - arm.z);
			float horizontalDistSq = targetX * targetX + targetZ * targetZ;
			float targetPitch = (float) Math.atan2(targetY, Mth.sqrt(horizontalDistSq));
			float targetLen = Mth.sqrt(horizontalDistSq + targetY * targetY);
			float upperArmLen = 4 / 16F, lowerArmLen = 4.25F / 16;
			float shoulderAngle = (float) Math.acos((upperArmLen * upperArmLen + targetLen * targetLen - lowerArmLen * lowerArmLen) / (2 * upperArmLen * targetLen));
			float flexionAngle = (float) Math.acos((upperArmLen * upperArmLen + lowerArmLen * lowerArmLen - targetLen * targetLen) / (2 * upperArmLen * lowerArmLen));
			ArmArticulation armArt = this.arms.get(side);
			// If shoulderAngle is NaN then the target is out of reach so the arm should simply point in that direction.
			armArt.shoulderAngleX = (Float.isNaN(shoulderAngle) ? -Mth.HALF_PI - targetPitch : (shoulderAngle - Mth.HALF_PI - targetPitch)) - this.bodyRotateAngleX;
			armArt.shoulderAngleY = (float) Math.atan2(targetZ, targetX) + Mth.HALF_PI - this.bodyRotateAngleY;
			armArt.flexionAngle = Float.isNaN(shoulderAngle) ? 0 : (flexionAngle - Mth.PI) * Mth.RAD_TO_DEG;
			armArt.shoulderZ = this.shoulderZ.get(side);
		}
	}

	private void createBodyTransformationMatrix() {
		this.matrix.setIdentity();
		// player yOffset
		this.matrix.translate(0, -1.62, 0);
		// regular scale
		this.matrix.scale(-1, -1, 1);
		// player scale
		this.matrix.scale(15 / 16F, 15 / 16F, 15 / 16F);
		// regular translate
		this.matrix.translate(0, -24 / 16F - 0.0078125, 0);
		// body rotation point
		this.matrix.translate(0, 12 / 16F, 0);
		this.matrix.rotate(this.bodyRotateAngleY, 0, 1, 0);
		this.matrix.rotate(this.bodyRotateAngleX, 1, 0, 0);
	}

	@Override
	public ResourceLocation getTextureLocation(WeedwoodRowboat rowboat) {
		return TEXTURE;
	}
}
