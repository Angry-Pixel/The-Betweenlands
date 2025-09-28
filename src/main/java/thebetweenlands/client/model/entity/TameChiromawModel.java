package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.chiromaw.TameChiromaw;

public class TameChiromawModel extends MowzieModelBase<TameChiromaw> {

	private final ModelPart body;
	private final ModelPart leftArm1;
	private final ModelPart leftArm2;
	private final ModelPart rightArm1;
	private final ModelPart rightArm2;
	private final ModelPart leftLeg1;
	private final ModelPart leftLeg2;
	private final ModelPart rightLeg1;
	private final ModelPart rightLeg2;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart head;
	private final ModelPart mouth;

	public TameChiromawModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.leftArm1 = this.body.getChild("left_arm1");
		this.leftArm2 = this.leftArm1.getChild("left_arm2");
		this.rightArm1 = this.body.getChild("right_arm1");
		this.rightArm2 = this.rightArm1.getChild("right_arm2");
		var butt = this.body.getChild("butt");
		this.leftLeg1 = butt.getChild("left_leg1");
		this.leftLeg2 = this.leftLeg1.getChild("left_leg2");
		this.rightLeg1 = butt.getChild("right_leg1");
		this.rightLeg2 = this.rightLeg1.getChild("right_leg2");
		this.tail1 = butt.getChild("tail1");
		this.tail2 = this.tail1.getChild("tail2");
		this.tail3 = this.tail2.getChild("tail3");
		this.head = this.body.getChild("neck").getChild("head");
		this.mouth = this.head.getChild("mouth");
	}

	@Override
	public void setupAnim(TameChiromaw entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float flap = Mth.sin(ageInTicks * 0.5F) * 0.6F;
		float flapSlower = Mth.sin(ageInTicks * 0.125F) * 0.1F;

		if (entity.isInSittingPose() || entity.isPassenger()) {
			float wingRaised = Mth.lerp(partialTick, entity.prevRaiseWingTicks, entity.raiseWingsTicks) / 2.5F;

			float wingFlapRaised = wingRaised;
			float wingFlapTicks = Mth.lerp(partialTick, entity.prevWingFlapTicks, entity.wingFlapTicks);
			if(wingFlapTicks > 1) {
				float wingSwing = (20 - wingFlapTicks) / 5.0f;
				wingSwing = 1.0f / (1.0f + (float)Math.exp(3 - wingSwing * 6));
				wingFlapRaised = Math.max(0.0f, wingRaised - wingSwing);
			} else if(wingFlapTicks == 1) {
				wingFlapRaised = 0.0f;
			}

			this.rightArm1.zRot = 0.5462880558742251F;
			this.rightArm2.zRot = 0F;
			this.leftArm1.zRot = -0.5462880558742251F;
			this.leftArm2.zRot = 0;

			this.rightArm1.yRot = 0.9091106186954104F;
			this.rightArm2.yRot = 0F;
			this.leftArm1.yRot = -0.9091106186954104F;
			this.leftArm2.yRot = 0;

			float globalSpeed = 1F;
			float globalDegree = 1F;
			float frame = wingFlapRaised * 6;

			this.swing(this.rightArm1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0.5f, frame, 1F);
			this.flap(this.rightArm2, globalSpeed * 0.5f, globalDegree * 0.8f, false, 2.0f, 0f, frame, 1F);
			this.swing(this.leftArm1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, -0.5f, frame, 1F);
			this.flap(this.leftArm2, globalSpeed * 0.5f, globalDegree * 0.8f, true, 2.0f, 0f, frame, 1F);

			this.walk(this.rightArm1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, frame, 1F);
			this.walk(this.rightArm2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, frame, 1F);
			this.walk(this.leftArm1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, frame, 1F);
			this.walk(this.leftArm2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, frame, 1F);

			globalSpeed = 0.1f;
			globalDegree = 0.1f;

			this.swing(this.rightArm1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0, ageInTicks, 1F);
			this.flap(this.rightArm2, globalSpeed * 0.5f, globalDegree * 0.8f, false, 2.0f, 0, ageInTicks, 1F);
			this.swing(this.leftArm1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, 0, ageInTicks, 1F);
			this.flap(this.leftArm2, globalSpeed * 0.5f, globalDegree * 0.8f, true, 2.0f, 0, ageInTicks, 1F);


			this.rightLeg1.xRot = -2.276432943376204F;
			this.leftLeg1.xRot = -2.276432943376204F;

			this.tail1.xRot = 0.27314402793711257F;
			this.tail2.xRot = 0.36425021489121656F;
			this.tail3.xRot = 0.40980330836826856F;

			this.tail1.xRot = 0.27314402793711257F;
			this.tail2.xRot = 0.36425021489121656F;
			this.tail3.xRot = 0.40980330836826856F;

			this.mouth.xRot = 0.9560913642424937F - flapSlower;
			this.head.xRot = 0.091106186954104F;

			float stance = wingRaised * 0.6f;

			this.rightLeg2.zRot -= stance * 2;
			this.leftLeg2.zRot += stance * 2;

			this.rightLeg2.xRot -= stance * 1.5f;
			this.leftLeg2.xRot -= stance * 1.5f;

			this.rightLeg2.yRot -= stance * 0.15f;
			this.leftLeg2.yRot += stance * 0.15f;

			this.head.xRot -= stance;
			this.body.xRot += stance;

			this.body.y += stance * 3;
			this.body.z -= stance * 3 + 3;
		} else {
			this.rightArm1.zRot = 0.5462880558742251F;
			this.rightArm2.zRot = 0F;
			this.leftArm1.zRot = -0.5462880558742251F;
			this.leftArm2.zRot = 0;

			this.rightArm1.yRot = 0.9091106186954104F;
			this.rightArm2.yRot = 0F;
			this.leftArm1.yRot = -0.9091106186954104F;
			this.leftArm2.yRot = 0;

			float globalSpeed = 1F;
			float globalDegree = 1F;

			this.swing(this.rightArm1, globalSpeed * 0.5f, globalDegree * 1.1f, false, 2.8f, 0.5f, ageInTicks, 1F);
			this.flap(this.rightArm2, globalSpeed * 0.5f, globalDegree * 0.8f, false, 2.0f, 0f, ageInTicks, 1F);
			this.swing(this.leftArm1, globalSpeed * 0.5f, globalDegree * 1.1f, true, 2.8f, -0.5f, ageInTicks, 1F);
			this.flap(this.leftArm2, globalSpeed * 0.5f, globalDegree * 0.8f, true, 2.0f, 0f, ageInTicks, 1F);

			this.walk(this.rightArm1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, ageInTicks, 1F);
			this.walk(this.rightArm2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, ageInTicks, 1F);
			this.walk(this.leftArm1, globalSpeed * 0.5f, globalDegree * 0.6f * 0.7f, true, 1.2f, 0.15f, ageInTicks, 1F);
			this.walk(this.leftArm2, globalSpeed * 0.5f, globalDegree * 1.2f * 0.7f, false, 1.2f, -0.9f, ageInTicks, 1F);

			this.rightLeg1.xRot = -2.276432943376204F + flap * 0.5F;
			this.leftLeg1.xRot = -2.276432943376204F + flap * 0.5F;

			this.tail1.xRot = 0.27314402793711257F + flap * 0.5F;
			this.tail2.xRot = 0.36425021489121656F + flap * 0.25F;
			this.tail3.xRot = 0.40980330836826856F + flap * 0.125F;

			this.mouth.xRot = 0.9560913642424937F - flap * 0.5F;
			this.head.xRot = -0.698132F;
		}
	}
}
