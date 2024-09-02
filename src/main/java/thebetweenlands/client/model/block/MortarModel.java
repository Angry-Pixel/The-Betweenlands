package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class MortarModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var mortar = partDefinition.addOrReplaceChild("mortar", CubeListBuilder.create(), PartPose.ZERO);

		mortar.addOrReplaceChild("waste", CubeListBuilder.create()
				.texOffs(51, 42)
				.addBox(-3.0F, 0.0F, -4.0F, 10, 0, 10),
			PartPose.offset(0.0F, -0.01F, 0.0F));

		mortar.addOrReplaceChild("mortar_bottom", CubeListBuilder.create()
			.texOffs(0, 35)
			.addBox(-4.0F, 0.0F, -4.0F, 8, 6, 8),
			PartPose.offset(0.0F, -10.0F, 0.0F));

		mortar.addOrReplaceChild("mortar_front", CubeListBuilder.create()
				.texOffs(17, 4)
				.addBox(-5.0F, 0.0F, -2.0F, 10, 10, 2),
			PartPose.offset(0.0F, -16.0F, -3.0F));

		mortar.addOrReplaceChild("mortar_left", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(0.0F, 0.0F, -3.0F, 2, 10, 6),
			PartPose.offset(3.0F, -16.0F, 0.0F));

		mortar.addOrReplaceChild("mortar_back", CubeListBuilder.create()
				.texOffs(17, 17)
				.addBox(-5.0F, 0.0F, 0.0F, 10, 10, 2),
			PartPose.offset(0.0F, -16.0F, 3.0F));

		mortar.addOrReplaceChild("mortar_right", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.0F, 0.0F, -3.0F, 2, 10, 6),
			PartPose.offset(-3.0F, -16.0F, 0.0F));

		var stand = mortar.addOrReplaceChild("stand", CubeListBuilder.create()
			.texOffs(60, 0)
			.addBox(-6.0F, 0.0F, -6.0F, 12, 2, 12),
			PartPose.offset(0.0F, -12.0F, 0.0F));

		stand.addOrReplaceChild("leg_1", CubeListBuilder.create()
			.texOffs(60, 15)
			.addBox(-1.1F, 0.0F, -0.9F, 2, 12, 2),
			PartPose.offsetAndRotation(5.0F, 1.0F, -5.0F, -0.091106186954104F, 0.0F, -0.091106186954104F));

		stand.addOrReplaceChild("leg_2", CubeListBuilder.create()
				.texOffs(69, 15)
				.addBox(-0.9F, 0.0F, -0.9F, 2, 12, 2),
			PartPose.offsetAndRotation(-5.0F, 1.0F, -5.0F, -0.091106186954104F, 0.0F, 0.091106186954104F));

		stand.addOrReplaceChild("leg_3", CubeListBuilder.create()
				.texOffs(78, 15)
				.addBox(-0.9F, 0.0F, -1.1F, 2, 12, 2),
			PartPose.offsetAndRotation(-5.0F, 1.0F, 5.0F, 0.091106186954104F, 0.0F, 0.091106186954104F));

		stand.addOrReplaceChild("leg_4", CubeListBuilder.create()
				.texOffs(87, 15)
				.addBox(-1.1F, 0.0F, -1.1F, 2, 12, 2),
			PartPose.offsetAndRotation(5.0F, 1.0F, 5.0F, 0.091106186954104F, 0.0F, -0.091106186954104F));

		stand.addOrReplaceChild("brace_1", CubeListBuilder.create()
				.texOffs(60, 30)
				.addBox(-5.0F, 0.0F, 0.0F, 10, 5, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, -5.5F, -0.091106186954104F, 0.0F, 0.0F));

		stand.addOrReplaceChild("brace_2", CubeListBuilder.create()
				.texOffs(60, 26)
				.addBox(0.0F, 0.0F, -5.0F, 0, 5, 10),
			PartPose.offsetAndRotation(-5.5F, 2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F));

		stand.addOrReplaceChild("brace_3", CubeListBuilder.create()
				.texOffs(81, 30)
				.addBox(-5.0F, 0.0F, 0.0F, 10, 5, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 5.5F, 0.091106186954104F, 0.0F, 0.0F));

		stand.addOrReplaceChild("brace_4", CubeListBuilder.create()
				.texOffs(81, 26)
				.addBox(0.0F, 0.0F, -5.0F, 0, 5, 10),
			PartPose.offsetAndRotation(5.5F, 2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));

		var pestle1 = partDefinition.addOrReplaceChild("pestle", CubeListBuilder.create()
			.texOffs(45, 0)
			.addBox(-1.5F, -4.0F, -1.5F, 3, 6, 3),
			PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, -0.40980330836826856F, -0.31869712141416456F, -0.18203784098300857F));

		var pestle2 = pestle1.addOrReplaceChild("pestle_2", CubeListBuilder.create()
				.texOffs(45, 10)
				.addBox(-1.0F, -6.0F, -1.0F, 2, 6, 2),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		pestle2.addOrReplaceChild("pestle_3", CubeListBuilder.create()
				.texOffs(45, 19)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3),
			PartPose.offset(0.0F, -6.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
