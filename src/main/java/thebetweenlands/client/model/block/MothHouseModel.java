package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class MothHouseModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-6.0F, -2.0F, -4.0F, 12, 2, 8),
			PartPose.ZERO);

		base.addOrReplaceChild("screen", CubeListBuilder.create()
			.texOffs(15, 34)
			.addBox(-4.0F, 0.0F, 0.0F, 8, 9, 0),
			PartPose.offsetAndRotation(0.0F, -13.0F, -1.0F, 0.0436F, 0.0F, 0.0F));

		base.addOrReplaceChild("left_base", CubeListBuilder.create()
				.texOffs(0, 51)
				.addBox(0.0F, -1.0F, -3.0F, 2, 1, 3),
			PartPose.offset(4.0F, -2.0F, -1.0F));

		base.addOrReplaceChild("right_base", CubeListBuilder.create()
				.texOffs(36, 34)
				.addBox(-2.0F, -1.0F, -3.0F, 2, 1, 3),
			PartPose.offset(-4.0F, -2.0F, -1.0F));

		base.addOrReplaceChild("front_base", CubeListBuilder.create()
				.texOffs(15, 44)
				.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 2),
			PartPose.offset(0.0F, -2.0F, -4.0F));

		base.addOrReplaceChild("left_mid", CubeListBuilder.create()
				.texOffs(0, 34)
				.addBox(-1.0F, -11.0F, 0.0F, 2, 11, 5),
			PartPose.offset(5.0F, -2.0F, -1.0F));

		base.addOrReplaceChild("right_mid", CubeListBuilder.create()
				.texOffs(56, 17)
				.addBox(-1.0F, -11.0F, 0.0F, 2, 11, 5),
			PartPose.offset(-5.0F, -2.0F, -1.0F));

		var back = base.addOrReplaceChild("back_panel", CubeListBuilder.create()
				.texOffs(33, 17)
				.addBox(-4.0F, -11.0F, -3.0F, 8, 11, 3),
			PartPose.offset(0.0F, -2.0F, 4.0F));

		var roof = back.addOrReplaceChild("roof_base", CubeListBuilder.create()
				.texOffs(41, 0)
				.addBox(-6.0F, -3.0F, -3.0F, 12, 3, 6),
			PartPose.offset(0.0F, -11.0F, -3.0F));

		roof.addOrReplaceChild("silk", CubeListBuilder.create()
				.texOffs(56, 34)
				.addBox(-4.0F, -1.0F, 0.0F, 9, 12, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, -0.0208F, 0.0853F, 0.1779F));

		var frontRoof = roof.addOrReplaceChild("roof_front", CubeListBuilder.create()
			.texOffs(0, 17)
			.addBox(-6.5F, 0.0F, -3.0F, 13, 2, 3),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

		frontRoof.addOrReplaceChild("roof_front_2", CubeListBuilder.create()
				.texOffs(66, 11)
				.addBox(-6.5F, 0.0F, -3.0F, 13, 2, 3, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.0F, 0.3927F, 0.0F, 0.0F));

		var backRoof = roof.addOrReplaceChild("roof_back", CubeListBuilder.create()
				.texOffs(33, 11)
				.addBox( -6.495F, 0.0F, 0.0F, 13, 2, 3),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

		backRoof.addOrReplaceChild("roof_back_2", CubeListBuilder.create()
				.texOffs(0, 11)
				.addBox( -6.495F, 0.0F, 0.0F, 13, 2, 3, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, -0.3927F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
