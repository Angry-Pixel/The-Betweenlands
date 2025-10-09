package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.projectile.spear.FishingSpear;

public class FishingSpearModel<T extends FishingSpear> extends MowzieModelBase<T> {



	public FishingSpearModel(ModelPart root) {
		super(root);
	}

	protected static MeshDefinition createBasicSpear() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var shaft = partDefinition.addOrReplaceChild("shaft", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5F, -12.0F, -0.5F, 1, 16, 1),
			PartPose.offset(0.0F, 8.0F, 0.0F));
		var tail1 = shaft.addOrReplaceChild("shaft_tail_1", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1),
			PartPose.offset(0.0F, 4.0F, 0.0F));
		tail1.addOrReplaceChild("shaft_tail_2", CubeListBuilder.create()
				.texOffs(0, 25).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1),
			PartPose.offset(0.0F, 5.0F, 0.0F));
		shaft.addOrReplaceChild("tip_binding", CubeListBuilder.create()
				.texOffs(5, 0).addBox(-1.0F, -11.0F, -1.0F, 2, 5, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("tip", CubeListBuilder.create()
				.texOffs(5, 4).addBox(0.0F, -16.0F, -2.0F, 0, 8, 4),
			PartPose.offset(0.0F, 8.0F, 0.0F));

		return definition;
	}

	public static LayerDefinition create() {
		return LayerDefinition.create(createBasicSpear(), 32, 32);
	}

	public static LayerDefinition createFinned() {
		MeshDefinition definition = createBasicSpear();
		addFins(definition.getRoot());
		return LayerDefinition.create(definition, 32, 32);
	}

	protected static void addFins(PartDefinition partDefinition) {
		var shaft = partDefinition.getChild("shaft");

		shaft.addOrReplaceChild("right_fin", CubeListBuilder.create()
				.texOffs(5, 22).addBox(-4.7F, 0.0F, 0.0F, 4, 4, 0),
			PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.0F, -0.7853981633974483F, 0.0F));
		shaft.addOrReplaceChild("left_fin", CubeListBuilder.create()
				.texOffs(5, 17).addBox(0.7F, 0.0F, 0.0F, 4, 4, 0),
			PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.0F, 0.7853981633974483F, 0.0F));

		shaft.getChild("shaft_tail_1").getChild("shaft_tail_2").addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(5, 22).addBox(0.0F, 0.0F, -2.5F, 0, 4, 5),
			PartPose.offset(0.0F, 3.0F, 0.0F));
	}
}
