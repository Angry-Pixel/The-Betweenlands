package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class OfferingTableModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-6.0F, -2.0F, -3.0F, 12, 2, 6),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		base.addOrReplaceChild("front_edge", CubeListBuilder.create()
				.texOffs(0, 14)
				.addBox(-6.01F, -2.0F, -2.0F, 12, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, -0.4553564018453205F, 0.0F, 0.0F));

		base.addOrReplaceChild("back_edge", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-5.99F, -2.0F, 0.0F, 12, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.4553564018453205F, 0.0F, 0.0F));

		var rightLeg = base.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(0, 28)
				.addBox(-2.0F, 0.0F, -3.0F, 2, 2, 6),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4553564018453205F));

		rightLeg.addOrReplaceChild("right_leg_2", CubeListBuilder.create()
				.texOffs(17, 28)
				.addBox(0.0F, 0.0F, -3.01F, 3, 2, 6),
			PartPose.offsetAndRotation(-2.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		var leftLeg = base.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(0, 19)
				.addBox(0.0F, 0.0F, -3.0F, 2, 2, 6),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.4553564018453205F));

		leftLeg.addOrReplaceChild("left_leg_2", CubeListBuilder.create()
				.texOffs(17, 19)
				.addBox(-3.0F, 0.0F, -3.01F, 3, 2, 6),
			PartPose.offsetAndRotation(2.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
