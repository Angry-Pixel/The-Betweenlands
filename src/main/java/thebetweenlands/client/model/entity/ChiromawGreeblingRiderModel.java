package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawGreeblingRider;
import thebetweenlands.common.registries.ItemRegistry;

public class ChiromawGreeblingRiderModel extends ChiromawModel<ChiromawGreeblingRider> implements ArmedModel {

	private final ModelPart greeblingBody;
	private final ModelPart chest;
	private final ModelPart head;
	private final ModelPart mouth;
	private final ModelPart rightArm1;
	private final ModelPart rightArm2;
	private final ModelPart leftArm1;
	private final ModelPart leftArm2;

	public ChiromawGreeblingRiderModel(ModelPart root) {
		super(root);
		this.greeblingBody = this.body.getChild("greebling_body");
		this.chest = this.greeblingBody.getChild("chest");
		this.head = this.chest.getChild("head");
		this.mouth = this.head.getChild("mouth");
		this.rightArm1 = this.chest.getChild("right_arm1");
		this.rightArm2 = this.rightArm1.getChild("right_arm2");
		this.leftArm1 = this.chest.getChild("left_arm1");
		this.leftArm2 = this.leftArm1.getChild("left_arm2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = createBase();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.getChild("body").addOrReplaceChild("greebling_body", CubeListBuilder.create()
				.texOffs(71, 9).addBox(-2.0F, -4.0F, -1.5F, 4, 4, 3),
			PartPose.offsetAndRotation(0.0F, 0.5F, 2.0F, -75 * Mth.DEG_TO_RAD, 0.0F, 0.0F));

		var rightLeg = body.addOrReplaceChild("right_leg1", CubeListBuilder.create()
				.texOffs(116, 5).addBox(-0.5F, -0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.7F, -0.4F, 0.0F, -0.5462880558742251F, 1.0927506446736497F, 0.40980330836826856F));
		rightLeg.addOrReplaceChild("right_leg2", CubeListBuilder.create()
				.texOffs(121, 5).addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.6829473363053812F, 0.0F, 0.0F));
		var leftLeg = body.addOrReplaceChild("left_leg1", CubeListBuilder.create()
				.texOffs(116, 0).addBox(-0.4F, -0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.7F, -0.4F, 0.0F, -0.5462880558742251F, -1.0927506446736497F, -0.40980330836826856F));
		leftLeg.addOrReplaceChild("left_leg2", CubeListBuilder.create()
				.texOffs(121, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, 0.6829473363053812F, -0.045553093477052F, 0.045553093477052F));

		var chest = body.addOrReplaceChild("chest", CubeListBuilder.create()
				.texOffs(71, 0).addBox(-2.5F, -3.5F, -2.0F, 5, 4, 4),
			PartPose.offsetAndRotation(0.0F, -3.4F, 0.0F, 0.5462880558742251F, 0.0F, 0.0F));
		var rightArm = chest.addOrReplaceChild("right_arm1", CubeListBuilder.create()
				.texOffs(106, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offset(-2.5F, -2.7F, 0.5F));
		rightArm.addOrReplaceChild("right_arm2", CubeListBuilder.create()
				.texOffs(111, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1),
			PartPose.offset(0.0F, 3.5F, 0.0F));
		var leftArm = chest.addOrReplaceChild("left_arm1", CubeListBuilder.create()
				.texOffs(96, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 4, 1),
			PartPose.offset(2.5F, -2.7F, 0.5F));
		leftArm.addOrReplaceChild("left_arm2", CubeListBuilder.create()
				.texOffs(101, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1),
			PartPose.offset(0.0F, 3.5F, 0.0F));

		var head = chest.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(71, 17).addBox(-2.0F, -3.0F, -3.0F, 4, 3, 4),
			PartPose.offsetAndRotation(0.0F, -3.8F, -0.5F, -0.5918411493512771F, 0.0F, 0.0F));
		head.addOrReplaceChild("mouth", CubeListBuilder.create()
				.texOffs(71, 25).addBox(-1.5F, -0.0F, -2.0F, 3, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.5009094953223726F, 0.0F, 0.0F));
		head.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(88, 21).addBox(-2.0F, 0.0F, -1.0F, 4, 1, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		head.addOrReplaceChild("nose", CubeListBuilder.create()
				.texOffs(82, 25).addBox(-0.5F, -2.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.22759093446006054F, 0.0F, 0.0F));
		head.addOrReplaceChild("left_ear", CubeListBuilder.create()
				.texOffs(87, 25).addBox(-1.0F, -2.0F, 0.0F, 5, 3, 0),
			PartPose.offsetAndRotation(2.0F, -1.5F, -0.5F, -0.091106186954104F, -0.5918411493512771F, -0.5009094953223726F));
		head.addOrReplaceChild("right_ear", CubeListBuilder.create()
				.texOffs(87, 29).addBox(-4.0F, -2.0F, 0.0F, 5, 3, 0),
			PartPose.offsetAndRotation(-2.0F, -1.5F, -0.5F, -0.091106186954104F, 0.5918411493512771F, 0.5009094953223726F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public void setupAnim(ChiromawGreeblingRider entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, partialTick, netHeadYaw, headPitch);
		float animationShootingTick = entity.getReloadTimer();
		float animationShooting = animationShootingTick + partialTick;
		HumanoidArm slingshotHand = entity.getMainArm().getOpposite();

		if (entity.getOffhandItem().is(ItemRegistry.SLINGSHOT)) {
			if (slingshotHand == HumanoidArm.RIGHT) {
				this.rightArm1.setRotation(-1.2292353921796064F, 0.045553093477052F, 0.36425021489121656F);
				this.rightArm2.setRotation(-0.6829473363053812F, 0.0F, 0.0F);

				this.leftArm1.setRotation(0.9105382707654417F, -0.136659280431156F, -0.18203784098300857F);
				this.leftArm2.setRotation(-2.5497515042385164F, -0.045553093477052F, 0.045553093477052F);
			} else {
				this.leftArm1.setRotation(-1.2292353921796064F, 0.045553093477052F, -0.36425021489121656F);
				this.leftArm2.setRotation(-0.6829473363053812F, 0.0F, 0.0F);

				this.rightArm1.setRotation(0.9105382707654417F, -0.136659280431156F, 0.18203784098300857F);
				this.rightArm2.setRotation(-2.5497515042385164F, 0.045553093477052F, -0.045553093477052F);
			}

			if (animationShootingTick < 90) {
				float frame = this.convertDegtoRad(animationShooting);
				if (slingshotHand == HumanoidArm.RIGHT) {
					this.chest.yRot = -frame;
					this.head.yRot = frame;
					this.head.xRot = -0.5918411493512771F + frame / Mth.PI;

					this.mouth.xRot = 0.5009094953223726F - frame / Mth.PI;

					this.rightArm1.yRot = 0.045553093477052F + frame;
					this.rightArm2.xRot = -0.6829473363053812F + frame / Mth.PI;

					this.leftArm1.xRot = 0.9105382707654417F - frame;
					this.leftArm1.zRot = -0.18203784098300857F - frame / Mth.PI;
					this.leftArm2.xRot = -2.5497515042385164F + frame;
				} else {
					this.chest.yRot = frame;
					this.head.yRot = -frame;
					this.head.xRot = -0.5918411493512771F + frame / Mth.PI;

					this.mouth.xRot = 0.5009094953223726F - frame / Mth.PI;

					this.leftArm1.yRot = 0.045553093477052F - frame;
					this.leftArm2.xRot = -0.6829473363053812F + frame / Mth.PI;

					this.rightArm1.xRot = 0.9105382707654417F - frame;
					this.rightArm1.zRot = 0.18203784098300857F + frame / Mth.PI;
					this.rightArm2.xRot = -2.5497515042385164F + frame;
				}
			} else {
				if (slingshotHand == HumanoidArm.RIGHT) {
					this.chest.yRot = -this.convertDegtoRad(90F) + this.convertDegtoRad(animationShooting - 90) * 9;
					this.head.yRot = this.convertDegtoRad(90F) - this.convertDegtoRad(animationShooting - 90) * 9;
					this.head.xRot = -0.5918411493512771F + this.convertDegtoRad(90F) / Mth.PI - (convertDegtoRad(animationShooting - 90) / Mth.PI) * 9;

					this.mouth.xRot = 0.5009094953223726F - this.convertDegtoRad(90F) / Mth.PI + (this.convertDegtoRad(animationShooting - 90) / Mth.PI) * 9;
					this.rightArm1.yRot = 0.045553093477052F + this.convertDegtoRad(90F) - this.convertDegtoRad(animationShooting - 90) * 9;
					this.rightArm2.xRot = -0.6829473363053812F + this.convertDegtoRad(90F) / Mth.PI - (this.convertDegtoRad(animationShooting - 90) / Mth.PI) * 9;

					this.leftArm1.xRot = 0.9105382707654417F - this.convertDegtoRad(90F) + this.convertDegtoRad(animationShooting - 90) * 9;
					this.leftArm1.zRot = -0.18203784098300857F - this.convertDegtoRad(90F) / Mth.PI + (this.convertDegtoRad(animationShooting - 90) / Mth.PI) * 9;
					this.leftArm2.xRot = -2.5497515042385164F + this.convertDegtoRad(90F) - this.convertDegtoRad(animationShooting - 90) * 9;
				} else {
					this.chest.yRot = this.convertDegtoRad(90F) - this.convertDegtoRad(animationShooting - 90) * 9;
					this.head.yRot = 0F - this.convertDegtoRad(90F) + this.convertDegtoRad(animationShooting - 90) * 9;
					this.head.xRot = -0.5918411493512771F + this.convertDegtoRad(90F) / Mth.PI - (convertDegtoRad(animationShooting - 90) / Mth.PI) * 9;

					this.mouth.xRot = 0.5009094953223726F - this.convertDegtoRad(90F) / Mth.PI + (this.convertDegtoRad(animationShooting - 90) / Mth.PI) * 9;
					this.leftArm1.yRot = 0.045553093477052F - this.convertDegtoRad(90F) + this.convertDegtoRad(animationShooting - 90) * 9;
					this.leftArm2.xRot = -0.6829473363053812F + this.convertDegtoRad(90F) / Mth.PI - (this.convertDegtoRad(animationShooting - 90) / Mth.PI) * 9;

					this.rightArm1.xRot = 0.9105382707654417F - this.convertDegtoRad(90F) + this.convertDegtoRad(animationShooting - 90) * 9;
					this.rightArm1.zRot = 0.18203784098300857F + this.convertDegtoRad(90F) / Mth.PI - (this.convertDegtoRad(animationShooting - 90) / Mth.PI) * 9;
					this.rightArm2.xRot = -2.5497515042385164F + this.convertDegtoRad(90F) - this.convertDegtoRad(animationShooting - 90) * 9;
				}
			}
		} else {
			this.rightArm1.setRotation(this.convertDegtoRad(-50), 0.0F, this.convertDegtoRad(15));
			this.rightArm2.setRotation(this.convertDegtoRad(-35), 0.0F, this.convertDegtoRad(-15));

			this.leftArm1.setRotation(this.convertDegtoRad(-50), 0.0F, this.convertDegtoRad(-15));
			this.leftArm2.setRotation(this.convertDegtoRad(-35), 0.0F, this.convertDegtoRad(15));
		}
	}

	private ModelPart getUpperArm(HumanoidArm side) {
		return side == HumanoidArm.LEFT ? this.leftArm1 : this.rightArm1;
	}

	private ModelPart getLowerArm(HumanoidArm side) {
		return side == HumanoidArm.LEFT ? this.leftArm2 : this.rightArm2;
	}

	@Override
	public void translateToHand(HumanoidArm side, PoseStack stack) {
		this.body.translateAndRotate(stack);
		this.greeblingBody.translateAndRotate(stack);
		this.chest.translateAndRotate(stack);
		this.getUpperArm(side).translateAndRotate(stack);
		this.getLowerArm(side).translateAndRotate(stack);
		stack.translate(side == HumanoidArm.LEFT ? -0.06F : 0.06F, -0.4F, 0.15F);
	}
}
