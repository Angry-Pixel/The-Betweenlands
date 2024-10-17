package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.CaveFish;

public class CaveFishModel extends MowzieModelBase<CaveFish> {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart leftVentralFin;
	private final ModelPart rightVentralFin;
	private final ModelPart leftPectoralFin;
	private final ModelPart rightPectoralFin;
	private final ModelPart tail;
	private final ModelPart tailFin;

	public CaveFishModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head_1").getChild("head_2");
		this.leftVentralFin = root.getChild("body_1").getChild("left_ventral_fin");
		this.rightVentralFin = root.getChild("body_1").getChild("right_ventral_fin");
		this.leftPectoralFin = root.getChild("body_1").getChild("left_pectoral_fin");
		this.rightPectoralFin = root.getChild("body_1").getChild("right_pectoral_fin");
		this.tail = root.getChild("body_1").getChild("body_2").getChild("body_3").getChild("body_4").getChild("tail");
		this.tailFin = this.tail.getChild("tail_fin");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var head = partDefinition.addOrReplaceChild("head_1", CubeListBuilder.create()
				.texOffs(12, 0).addBox(-1.0F, -1.5F, -0.5F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, 14.1F, -3.8F, 0.8726646259971648F, 0.0F, 0.0F));
		head.addOrReplaceChild("head_2", CubeListBuilder.create()
				.texOffs(21, 0).addBox(-1.0F, -0.8F, -1.0F, 2, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.5F, -0.5F, -0.4553564018453205F, 0.0F, 0.0F));

		var eye_left = head.addOrReplaceChild("left_eye_1", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offsetAndRotation(1.0F, -0.5F, 2.0F, 0.0F, 0.17453292519943295F, -0.17453292519943295F));
		eye_left.addOrReplaceChild("left_eye_2", CubeListBuilder.create()
				.texOffs(0, 7).addBox(1.5F, -1.0F, -1.0F, 1, 2, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var eye_right = head.addOrReplaceChild("right_eye_1", CubeListBuilder.create()
				.texOffs(22, 3).addBox(-1.5F, -1.5F, -1.5F, 2, 3, 3),
			PartPose.offsetAndRotation(-1.0F, -0.5F, 2.0F, 0.0F, -0.17453292519943295F, 0.17453292519943295F));
		eye_right.addOrReplaceChild("right_eye_2", CubeListBuilder.create()
				.texOffs(24, 10).addBox(-2.5F, -1.0F, -1.0F, 1, 2, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		var body = partDefinition.addOrReplaceChild("body_1", CubeListBuilder.create()
				.texOffs(11, 5).addBox(-1.0F, -2.0F, 0.0F, 2, 3, 3),
			PartPose.offset(0.0F, 14.0F, -4.0F));
		var body_2 = body.addOrReplaceChild("body_2", CubeListBuilder.create()
				.texOffs(13, 17).addBox(-1.0F, -1.5F, 0.0F, 2, 3, 1),
			PartPose.offsetAndRotation(0.0F, -0.53F, 2.7F, -0.14433872913993104F, 0.0F, 0.0F));
		var body_3 = body_2.addOrReplaceChild("body_3", CubeListBuilder.create()
				.texOffs(11, 12).addBox(-1.5F, -1.0F, -1.0F, 3, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7853981633974483F, 0.0F, 0.0F));
		var body_4 = body_3.addOrReplaceChild("body_4", CubeListBuilder.create()
				.texOffs(13, 22).addBox(-0.5F, 0.0F, -1.0F, 1, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.3F, 1.0F, 0.6981317007977318F, 0.0F, 0.0F));

		var tail = body_4.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(13, 27).addBox(-0.5F, 0.0F, -1.0F, 1, 1, 2),
			PartPose.offset(0.0F, 2.0F, 0.0F));
		tail.addOrReplaceChild("tail_fin", CubeListBuilder.create()
				.texOffs(0, 14).addBox(0.0F, -0.5F, -3.0F, 0, 5, 5),
			PartPose.offset(0.0F, 0.5F, 0.0F));

		body.addOrReplaceChild("left_ventral_fin", CubeListBuilder.create()
				.texOffs(8, 13).addBox(0.0F, -0.5F, -0.5F, 0, 2, 1),
			PartPose.offsetAndRotation(-1.0F, 1.0F, 2.0F, 0.6981317007977318F, 0.0F, 0.3490658503988659F));
		body.addOrReplaceChild("right_ventral_fin", CubeListBuilder.create()
				.texOffs(0, 13).addBox(0.0F, -0.5F, -0.5F, 0, 2, 1),
			PartPose.offsetAndRotation(1.0F, 1.0F, 2.0F, 0.6981317007977318F, 0.0F, -0.3490658503988659F));

		body.addOrReplaceChild("left_pectoral_fin", CubeListBuilder.create()
				.texOffs(6, 10).addBox(0.0F, -0.5F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(-1.5F, -0.5F, 2.3F, 0.0F, -0.3490658503988659F, 0.0F));
		body.addOrReplaceChild("right_pectoral_fin", CubeListBuilder.create()
				.texOffs(0, 10).addBox(0.0F, -0.5F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(1.5F, -0.5F, 2.3F, 0.0F, 0.3490658503988659F, 0.0F));

		body.addOrReplaceChild("dorsal_fin", CubeListBuilder.create()
				.texOffs(3, 12).addBox(0.0F, -2.0F, 0.0F, 0, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 1.8F, -0.4363323129985824F, 0.0F, 0.0F));
		body_4.addOrReplaceChild("dorsal_fin_2", CubeListBuilder.create()
				.texOffs(7, 16).addBox(0.0F, 0.0F, 0.0F, 0, 1, 1),
			PartPose.offset(0.0F, 1.0F, 1.0F));
		body_4.addOrReplaceChild("anal_fin", CubeListBuilder.create()
				.texOffs(4, 16).addBox(0.0F, -0.5F, -1.0F, 0, 1, 1),
			PartPose.offset(0.0F, 0.7F, -1.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(CaveFish entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.setInitPose();
		this.walk(this.head, 0.25F, 0.35F, false, 0.0F, 0.0F, ageInTicks, 1.0F - limbSwingAmount);

		this.flap(this.tail, 0.75F, 0.5F, false, 0.0F, 0.0F, ageInTicks, 0.0625F + limbSwingAmount);
		this.flap(this.tailFin, 0.75F, 0.5F, false, 1.0F, 0.0F, ageInTicks, 0.0625F + limbSwingAmount);

		this.swing(this.leftVentralFin, 0.75F, 0.5F, false, 2.0F, 0.0F, ageInTicks, 0.125F + limbSwingAmount);
		this.swing(this.rightVentralFin, 0.75F, 0.5F, true, 2.0F, 0.0F, ageInTicks, 0.125F + limbSwingAmount);

		this.swing(this.leftPectoralFin, 0.5F, 0.75F, true, 1.0F, 0.0F, ageInTicks, 0.125F + limbSwingAmount);
		this.swing(this.rightPectoralFin, 0.5F, 0.75F, false, 1.0F, 0.0F, ageInTicks, 0.125F + limbSwingAmount);

		this.walk(this.leftPectoralFin, 0.5F, 0.75F, false, 0.0F, 0.0F, ageInTicks, 0.125F + limbSwingAmount);
		this.walk(this.rightPectoralFin, 0.5F, 0.75F, false, 0.0F, 0.0F, ageInTicks, 0.125F + limbSwingAmount);

		this.flap(this.leftPectoralFin, 0.5F, 0.75F, false, 0.0F, 0.0F, ageInTicks, 0.125F + limbSwingAmount);
		this.flap(this.rightPectoralFin, 0.5F, 0.75F, true, 0.0F, 0.0F, ageInTicks, 0.125F + limbSwingAmount);
	}
}
