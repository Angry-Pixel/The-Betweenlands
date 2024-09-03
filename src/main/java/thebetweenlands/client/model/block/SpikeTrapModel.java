package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SpikeTrapModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("spike1", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-1.0F, -11.0F, -1.0F, 2, 16, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.091106186954104F, 0.6373942428283291F, 0.0F));

		partDefinition.addOrReplaceChild("spike2", CubeListBuilder.create()
				.texOffs(9, 0)
				.addBox(-1.1F, -9.0F, -1.0F, 2, 13, 2),
			PartPose.offsetAndRotation(4.0F, 5.0F, -4.0F, 0.136659280431156F, 0.31869712141416456F, 0.136659280431156F));

		partDefinition.addOrReplaceChild("spike3", CubeListBuilder.create()
				.texOffs(18, 0)
				.addBox(-1.0F, -9.0F, -1.0F, 2, 13, 2),
			PartPose.offsetAndRotation(-4.0F, 5.0F, -3.0F, 0.18203784098300857F, 0.6373942428283291F, 0.0F));

		partDefinition.addOrReplaceChild("spike4", CubeListBuilder.create()
				.texOffs(27, 0)
				.addBox(-1.0F, -7.0F, -1.0F, 2, 11, 2),
			PartPose.offsetAndRotation(-4.0F, 5.0F, 3.0F, -0.18203784098300857F, 0.091106186954104F, -0.18203784098300857F));

		partDefinition.addOrReplaceChild("spike5", CubeListBuilder.create()
				.texOffs(36, 0)
				.addBox(-1.0F, -7.0F, -1.0F, 2, 11, 2),
			PartPose.offsetAndRotation(4.0F, 5.0F, 4.0F, -0.136659280431156F, 0.40980330836826856F, 0.045553093477052F));

		partDefinition.addOrReplaceChild("spike6", CubeListBuilder.create()
				.texOffs(45, 0)
				.addBox(-1.0F, -5.0F, -1.0F, 2, 9, 2),
			PartPose.offsetAndRotation(0.0F, 5.0F, 5.0F, -0.27314402793711257F, -0.6373942428283291F, 0.18203784098300857F));

		partDefinition.addOrReplaceChild("spike7", CubeListBuilder.create()
				.texOffs(54, 0)
				.addBox(-1.0F, -5.0F, -1.0F, 2, 9, 2),
			PartPose.offsetAndRotation(5.0F, 5.0F, 0.0F, 0.0F, -0.40980330836826856F, 0.136659280431156F));

		partDefinition.addOrReplaceChild("spike8", CubeListBuilder.create()
				.texOffs(54, 0)
				.addBox(-1.0F, -3.0F, -1.0F, 2, 7, 2),
			PartPose.offsetAndRotation(0.0F, 5.0F, -5.0F, 0.18203784098300857F, 0.4553564018453205F, 0.091106186954104F));

		partDefinition.addOrReplaceChild("spike9", CubeListBuilder.create()
				.texOffs(54, 0)
				.addBox(-1.0F, -3.0F, -1.0F, 2, 7, 2),
			PartPose.offsetAndRotation(-5.0F, 5.0F, 0.0F, 0.0F, 0.7740535232594852F, -0.22759093446006054F));

		return LayerDefinition.create(definition, 64, 32);
	}

	public static LayerDefinition makeSpoop() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("spoop", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-8.0F, 15.01F, -8.0F, 16, 1, 16),
			PartPose.offset(0.0F, -8.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}
}
