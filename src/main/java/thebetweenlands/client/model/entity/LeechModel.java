package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.Leech;

public class LeechModel extends MowzieModelBase<Leech> {

	public LeechModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("part1", CubeListBuilder.create()
			.texOffs(0, 11).addBox(0.0F, 0.0F, 0.0F, 2, 2, 1),
			PartPose.offset(-1.0F, 22.0F, -7.0F));

		partDefinition.addOrReplaceChild("part2", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 3, 3, 2),
			PartPose.offset(-1.5F, 21.0F, -6.0F));

		partDefinition.addOrReplaceChild("part3", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 4, 4, 7),
			PartPose.offset(-2.0F, 20.0F, -4.0F));

		partDefinition.addOrReplaceChild("part4", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 3, 3, 2),
			PartPose.offset(-1.5F, 21.0F, 3.0F));

		partDefinition.addOrReplaceChild("part5", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2, 2, 2),
			PartPose.offset(-1.0F, 22.0F, 5.0F));

		partDefinition.addOrReplaceChild("part6", CubeListBuilder.create()
				.texOffs(6, 11).addBox(0.0F, 0.0F, 0.0F, 1, 1, 2),
			PartPose.offset(-0.5F, 23.0F, 7.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public void setupAnim(Leech entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, partialTick, netHeadYaw, headPitch);
		if (!entity.isPassenger()) {
			entity.moveProgress = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		}
	}
}
