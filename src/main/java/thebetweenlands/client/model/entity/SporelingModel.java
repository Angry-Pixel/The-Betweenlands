package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.Sporeling;

public class SporelingModel extends MowzieModelBase<Sporeling> {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart leftArm;
	private final ModelPart rightArm;

	public SporelingModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.rightArm = root.getChild("right_arm");
		this.leftArm = root.getChild("left_arm");
		this.rightLeg = root.getChild("right_leg");
		this.leftLeg = root.getChild("left_leg");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(20, 0).addBox(-2.5F, -2.0F, -2.5F, 5.0F, 2.0F, 5.0F)
				.texOffs(20, 8).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 1.0F, 4.0F),
			PartPose.offsetAndRotation(0.0F, 18.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

		head.addOrReplaceChild("left_eye", CubeListBuilder.create()
				.texOffs(25, 14).addBox(-0.5F, -1.5F, -2.5F, 1.0F, 1.0F, 1.0F),
			PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		head.addOrReplaceChild("right_eye", CubeListBuilder.create()
				.texOffs(20, 14).addBox(-0.5F, -1.5F, -2.5F, 1.0F, 1.0F, 1.0F),
			PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("torso", CubeListBuilder.create()
				.texOffs(0, 17).addBox(-1.5F, 2.0F, -0.5F, 3.0F, 2.0F, 2.0F)
				.texOffs(0, 0).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 19.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(5, 22).addBox(0.0F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F),
			PartPose.offsetAndRotation(1.0F, 20.0F, 0.0F, -0.2618F, 0.0873F, -0.6109F));

		partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(0, 22).addBox(-1.0F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F),
			PartPose.offsetAndRotation(-1.0F, 20.0F, 0.0F, -0.2618F, 0.0873F, 0.6109F));

		partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(5, 12).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 3.0F, 1.0F),
			PartPose.offset(1.0F, 22.0F, 0.0F));

		partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(0, 12).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 3.0F, 1.0F),
			PartPose.offset(-1.0F, 22.0F, 0.0F));


		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Sporeling entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.leftArm.xRot = Mth.cos(limbSwing * 1.5F + Mth.PI) * 2.0F * limbSwingAmount * 0.5F - (Mth.cos(ageInTicks / 10.0F) + 0.7F) / 2.0F * 0.3F;
		this.rightArm.xRot = Mth.cos(limbSwing * 1.5F) * 2.0F * limbSwingAmount * 0.5F - (Mth.cos(ageInTicks / 11.0F) + 0.7F) / 2.0F * 0.3F;

		if (entity.getIsFalling()) {
			this.head.xScale = 2.0F;
			this.head.zScale = 2.0F;
			this.head.xRot = 0.0F;
		} else {
			this.head.xScale = 1.0F;
			this.head.zScale = 1.0F;
			this.head.xRot = -30.0F * Mth.DEG_TO_RAD;
		}

		if (!entity.isPassenger()) {
			this.leftLeg.xRot = Mth.cos(limbSwing * 1.5F) * 1.4F * limbSwingAmount;
			this.rightLeg.xRot = Mth.cos(limbSwing * 1.5F + Mth.PI) * 1.4F * limbSwingAmount;
		} else {
			this.leftLeg.xRot = -80 * Mth.DEG_TO_RAD;
			this.rightLeg.xRot = -80 * Mth.DEG_TO_RAD;
		}
	}
}
