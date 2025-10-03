package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.BloodSnail;

public class BloodSnailModel extends MowzieModelBase<BloodSnail> {

	private final ModelPart leftSensor1;
	private final ModelPart leftSensor2;
	private final ModelPart rightSensor1;
	private final ModelPart rightSensor2;

	public BloodSnailModel(ModelPart root) {
		super(root);
		var head = root.getChild("base").getChild("head");
		var snout = head.getChild("snout");

		this.leftSensor1 = head.getChild("left_sensor_1");
		this.rightSensor1 = head.getChild("right_sensor_1");
		this.leftSensor2 = snout.getChild("left_sensor_2");
		this.rightSensor2 = snout.getChild("right_sensor_2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, -3.0F, 0.0F, 4, 3, 6),
			PartPose.offsetAndRotation(0.0F, 24.0F, -3.0F, -0.091106186954104F, 0.0F, 0.0F));

		var shell1 = base.addOrReplaceChild("shell1", CubeListBuilder.create()
				.texOffs(21, 0).addBox(-2.5F, -3.0F, 0.0F, 5, 5, 1),
			PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, 0.5462880558742251F, -0.045553093477052F, 0.18203784098300857F));
		var shell2 = shell1.addOrReplaceChild("shell2", CubeListBuilder.create()
				.texOffs(21, 7).addBox(-3.0F, -0.5F, 0.0F, 6, 6, 3),
			PartPose.offsetAndRotation(0.0F, -3.0F, 1.0F, -0.091106186954104F, 0.0F, 0.0F));
		var shell3 = shell2.addOrReplaceChild("shell3", CubeListBuilder.create()
				.texOffs(21, 17).addBox(-2.5F, 0.0F, 0.0F, 5, 5, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.091106186954104F, 0.0F, 0.0F));
		var shell4 = shell3.addOrReplaceChild("shell4", CubeListBuilder.create()
				.texOffs(21, 25).addBox(-2.0F, 0.0F, 0.0F, 4, 4, 2),
			PartPose.offsetAndRotation(0.0F, 0.5F, 2.0F, -0.091106186954104F, 0.0F, 0.0F));
		shell4.addOrReplaceChild("shell5", CubeListBuilder.create()
				.texOffs(34, 0).addBox(-1.5F, 0.0F, 0.0F, 3, 3, 1),
			PartPose.offsetAndRotation(0.0F, 0.5F, 2.0F, -0.091106186954104F, 0.0F, 0.0F));

		var head = base.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 16).addBox(-1.5F, 0.0F, -2.0F, 3, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.091106186954104F, 0.0F, 0.0F));
		var snout = head.addOrReplaceChild("snout", CubeListBuilder.create()
				.texOffs(0, 21).addBox(-1.0F, 0.0F, -1.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.5F, -2.0F, 0.4553564018453205F, 0.0F, 0.0F));

		head.addOrReplaceChild("left_sensor_1", CubeListBuilder.create()
				.texOffs(5, 25).addBox(-0.5F, -2.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.5F, 0.0F, -1.5F, 0.8651597102135892F, -0.6829473363053812F, 0.0F));
		snout.addOrReplaceChild("left_sensor_2", CubeListBuilder.create()
				.texOffs(7, 21).addBox(-0.5F, 0.0F, -2.0F, 1, 0, 2),
			PartPose.offsetAndRotation(0.5F, 1.5F, 0.0F, -0.40980330836826856F, -0.5918411493512771F, -0.18203784098300857F));

		head.addOrReplaceChild("right_sensor_1", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-0.5F, -2.0F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.5F, 0.0F, -1.5F, 0.8651597102135892F, 0.6829473363053812F, 0.0F));
		snout.addOrReplaceChild("right_sensor_2", CubeListBuilder.create()
				.texOffs(5, 21).addBox(-0.5F, 0.0F, -2.0F, 1, 0, 2),
			PartPose.offsetAndRotation(-0.5F, 1.5F, 0.0F, -0.40980330836826856F, 0.5918411493512771F, 0.18203784098300857F));

		base.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(0, 10).addBox(-1.5F, -2.0F, 0.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, -0.2F, 6.0F, 0.091106186954104F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public void setupAnim(BloodSnail entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, partialTick, netHeadYaw, headPitch);
		this.leftSensor1.xRot = this.leftSensor2.xRot = Mth.cos(limbSwing + Mth.PI) * 1.5F * limbSwingAmount + 0.5F;
		this.rightSensor1.xRot = this.rightSensor2.xRot = Mth.cos(limbSwing) * 1.5F * limbSwingAmount + 0.5F;
		this.leftSensor1.yRot = this.leftSensor2.yRot = Mth.cos(limbSwing + Mth.PI) * 1.5F * limbSwingAmount + 0.2F;
		this.rightSensor1.yRot = this.rightSensor2.yRot = Mth.cos(limbSwing) * 1.5F * limbSwingAmount - 0.2F;
	}
}
