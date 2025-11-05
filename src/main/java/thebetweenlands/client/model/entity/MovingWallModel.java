package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.MovingWall;

public class MovingWallModel extends MowzieModelBase<MovingWall> {

	public MovingWallModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("wall", CubeListBuilder.create()
				.addBox(-24.0F, -24.0F, -24.0F, 48, 48, 16),
			PartPose.ZERO);

		return LayerDefinition.create(definition, 128, 64);
	}
}
