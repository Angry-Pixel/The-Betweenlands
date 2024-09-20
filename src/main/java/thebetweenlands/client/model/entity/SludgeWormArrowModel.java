package thebetweenlands.client.model.entity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.common.entity.projectile.arrow.SludgeWormArrow;

public class SludgeWormArrowModel extends HierarchicalModel<SludgeWormArrow> {

	private final ModelPart root;

	public SludgeWormArrowModel(ModelPart root) {
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
			.addBox(-1.5F, -1.5F, -6.5F, 3, 3, 3),
			PartPose.offset(0.0F, -1.5F, 0.0F));

		head.addOrReplaceChild("left_beak", CubeListBuilder.create()
				.texOffs(0, 14)
				.addBox(-3.5F, -2.0F, -6.7F, 2, 3, 3),
			PartPose.offsetAndRotation(1.5F, 0.5F, -1.5F, 0.0F, -0.31869712141416456F, 0.0F));

		head.addOrReplaceChild("right_beak", CubeListBuilder.create()
				.texOffs(0, 7)
				.addBox(1.5F, -1.5F, -6.7F, 2, 3, 3),
			PartPose.offsetAndRotation(-1.5F, 0.0F, -1.5F, 0.0F, 0.31869712141416456F, 0.0F));


		head.addOrReplaceChild("body_1", CubeListBuilder.create()
			.texOffs(13, 0)
			.addBox(-1.5F, -1.5F, -3.5F, 3, 3, 3),
			PartPose.ZERO);

		head.addOrReplaceChild("body_2", CubeListBuilder.create()
				.texOffs(13, 0)
				.addBox(-1.5F, -1.5F, -0.5F, 3, 3, 3),
			PartPose.ZERO);

		head.addOrReplaceChild("body_3", CubeListBuilder.create()
				.texOffs(13, 0)
				.addBox(-1.5F, -1.5F, 2.5F, 3, 3, 3),
			PartPose.ZERO);

		var rear = head.addOrReplaceChild("rear", CubeListBuilder.create()
				.texOffs(13, 7)
				.addBox(-1.0F, -1.0F, 5.5F, 2, 2, 2),
			PartPose.ZERO);

		rear.addOrReplaceChild("stinger", CubeListBuilder.create()
			.texOffs(13, 11)
			.addBox(-0.5F, -0.8F, 6.5F, 1, 2, 2),
			PartPose.offsetAndRotation(0.0F, -1.5F, 1.0F, -0.18203784098300857F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(SludgeWormArrow entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}
}
