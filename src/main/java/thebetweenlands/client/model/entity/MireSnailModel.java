package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.MireSnail;

public class MireSnailModel extends MowzieModelBase<MireSnail> {

	private final ModelPart root;
	private final ModelPart sensor1;
	private final ModelPart sensor2;

	public MireSnailModel(ModelPart root) {
		this.root = root;
		this.sensor1 = root.getChild("sensor_1");
		this.sensor2 = root.getChild("sensor_2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("torso_1", CubeListBuilder.create()
			.addBox(-1.5F, 0.0F, -7.0F, 3, 2, 14),
			PartPose.offset(0.0F, 22.0F, 0.0F));

		partDefinition.addOrReplaceChild("torso_2", CubeListBuilder.create()
				.texOffs(0, 22)
				.addBox(-2F, -1F, -5.5F, 4, 3, 6),
			PartPose.offset(0.0F, 22.0F, 0.0F));

		partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-1F, -4.8F, -7F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, 22.0F, 0.0F, 0.7807508F, 0F, 0F));

		partDefinition.addOrReplaceChild("shell_1", CubeListBuilder.create()
				.texOffs(22, 0)
				.addBox(-2.5F, -5F, -3F, 5, 6, 6),
			PartPose.offsetAndRotation(0.0F, 22.0F, 0.0F, -0.3717861F, 0F, 0F));

		partDefinition.addOrReplaceChild("shell_2", CubeListBuilder.create()
				.texOffs(35, 13)
				.addBox(-3F, -4F, -2F, 6, 4, 4),
			PartPose.offsetAndRotation(0.0F, 22.0F, 0.0F, -0.37179F, 0F, 0F));

		partDefinition.addOrReplaceChild("sensor_1", CubeListBuilder.create()
				.texOffs(9, 16)
				.addBox(-0.5F, -3F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(1F, 22F, -7F, 0.669215F, -0.5576792F, 0F));

		partDefinition.addOrReplaceChild("sensor_2", CubeListBuilder.create()
				.texOffs(14, 16)
				.addBox(-0.5F, -3F, -0.5F, 1, 4, 1),
			PartPose.offsetAndRotation(-1F, 22F, -7F, 0.669215F, 0.5576851F, 0F));

		partDefinition.addOrReplaceChild("sensor_3", CubeListBuilder.create()
				.texOffs(17, 16)
				.addBox(-0.5F, 1.5F, -2.5F, 1, 0, 2, new CubeDeformation(0.01F)),
			PartPose.offsetAndRotation(0F, 22F, -7F, -0.1115358F, -0.8179294F, 0F));

		partDefinition.addOrReplaceChild("sensor_4", CubeListBuilder.create()
				.texOffs(17, 19)
				.addBox(-0.5F, 1.5F, -2.5F, 1, 0, 2, new CubeDeformation(0.01F)),
			PartPose.offsetAndRotation(0F, 22F, -7F, -0.1115358F, 0.8179294F, 0F));

		partDefinition.addOrReplaceChild("sensor_5", CubeListBuilder.create()
				.texOffs(22, 16)
				.addBox(-0.5F, -1F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(1.5F, 21F, -5.5F, 0.7063936F, -0.9666439F, 0F));

		partDefinition.addOrReplaceChild("sensor_6", CubeListBuilder.create()
				.texOffs(27, 16)
				.addBox(-0.5F, -1F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-1.5F, 21F, -5.5F, 0.7063936F, 0.9666506F, 0F));

		partDefinition.addOrReplaceChild("tree_1", CubeListBuilder.create()
				.texOffs(21, 22)
				.addBox(-2.5F, -2F, 0F, 1, 3, 1),
			PartPose.offsetAndRotation(0F, 16F, 0F, 0F, -0.3346075F, -0.1858931F));

		partDefinition.addOrReplaceChild("tree_2", CubeListBuilder.create()
				.texOffs(21, 27)
				.addBox(-2.5F, -1F, 0F, 1, 2, 1),
			PartPose.offsetAndRotation(0F, 16F, 0F, 0F, -0.3346145F, 0.1858911F));

		partDefinition.addOrReplaceChild("tree_3", CubeListBuilder.create()
				.texOffs(21, 30)
				.addBox(-2.5F, 0.5F, 0F, 3, 1, 1),
			PartPose.offsetAndRotation(0F, 16F, 0F, 0F, -0.3346145F, 0.3346055F));

		partDefinition.addOrReplaceChild("tree_4", CubeListBuilder.create()
				.texOffs(29, 29)
				.addBox(0F, 0F, 1F, 1, 1, 2),
			PartPose.offsetAndRotation(0F, 16F, 0F, -0.5948578F, 0.2230717F, 0.1115358F));

		partDefinition.addOrReplaceChild("tree_5", CubeListBuilder.create()
				.texOffs(35, 29)
				.addBox(-2.5F, 0F, 0.5F, 1, 1, 2),
			PartPose.offsetAndRotation(0F, 16F, 0F, -0.4833219F, -0.1115358F, 0F));

		partDefinition.addOrReplaceChild("leaf_1", CubeListBuilder.create()
				.texOffs(26, 22)
				.addBox(-4F, -3F, 0F, 3, 2, 3),
			PartPose.offsetAndRotation(0F, 16F, 0F, 0F, -0.4461433F, 0F));

		partDefinition.addOrReplaceChild("leaf_2", CubeListBuilder.create()
				.texOffs(39, 21)
				.addBox(-2F, -4F, -3F, 4, 2, 4),
			PartPose.offsetAndRotation(0F, 16F, 0F, 0F, 0.4089647F, 0.0743572F));

		partDefinition.addOrReplaceChild("leaf_3", CubeListBuilder.create()
				.texOffs(45, 0)
				.addBox(-1.5F, 2F, -4F, 4, 2, 4),
			PartPose.offsetAndRotation(0F, 16F, 0F, 0.0743572F, -0.6320364F, 0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(MireSnail entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.sensor1.xRot = Mth.cos(limbSwing + Mth.PI) * 1.5F * limbSwingAmount + 0.5F;
		this.sensor2.xRot = Mth.cos(limbSwing) * 1.5F * limbSwingAmount + 0.5F;
		this.sensor1.yRot = Mth.cos(limbSwing + Mth.PI) * 1.5F * limbSwingAmount - 0.2F;
		this.sensor2.yRot = Mth.cos(limbSwing) * 1.5F * limbSwingAmount + 0.2F;
	}
}
