package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class LootPotModels {

	public static LayerDefinition makePot1() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.addBox(-4.0F, -4.0F, -4.0F, 8, 4, 8),
			PartPose.ZERO);

		base.addOrReplaceChild("side1", CubeListBuilder.create()
			.texOffs(0, 13)
			.addBox(-2.0F, -10.0F, -6.0F, 2, 10, 12),
			PartPose.offset(-4.0F, -2.0F, 0.0F));

		base.addOrReplaceChild("side2", CubeListBuilder.create()
				.texOffs(29, 13)
				.addBox(-4.0F, -10.0F, -2.0F, 8, 10, 2),
			PartPose.offset(0.0F, -2.0F, -4.0F));

		base.addOrReplaceChild("side3", CubeListBuilder.create()
				.texOffs(0, 36)
				.addBox(0.0F, -10.0F, -6.0F, 2, 10, 12),
			PartPose.offset(4.0F, -2.0F, 0.0F));

		base.addOrReplaceChild("side4", CubeListBuilder.create()
				.texOffs(29, 26)
				.addBox(-4.0F, -10.0F, 0.0F, 8, 10, 2),
			PartPose.offset(0.0F, -2.0F, 4.0F));

		var lid = base.addOrReplaceChild("lid", CubeListBuilder.create()
				.texOffs(33, 0)
				.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10),
			PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.045553093477052F, 0.0F));

		lid.addOrReplaceChild("lid_top", CubeListBuilder.create()
				.texOffs(64, 0)
				.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	public static LayerDefinition makePot2() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8),
			PartPose.ZERO);

		base.addOrReplaceChild("base_2", CubeListBuilder.create()
				.texOffs(0, 11)
				.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		base.addOrReplaceChild("cup1", CubeListBuilder.create()
				.texOffs(0, 20)
				.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		base.addOrReplaceChild("cup2", CubeListBuilder.create()
				.texOffs(0, 33)
				.addBox(-6.0F, -6.0F, -6.0F, 12, 6, 12),
			PartPose.offset(0.0F, -6.0F, 0.0F));

		var top = base.addOrReplaceChild("cup3", CubeListBuilder.create()
				.texOffs(40, 0)
				.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8),
			PartPose.offset(0.0F, -12.0F, 0.0F));

		base.addOrReplaceChild("ear_left", CubeListBuilder.create()
				.texOffs(65, 27)
				.addBox(0.0F, -1.0F, -1.5F, 4, 8, 3),
			PartPose.offsetAndRotation(6.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));

		base.addOrReplaceChild("ear_right", CubeListBuilder.create()
				.texOffs(50, 27)
				.addBox(-4.0F, -1.0F, -1.5F, 4, 8, 3),
			PartPose.offsetAndRotation(-6.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.27314402793711257F));

		top.addOrReplaceChild("rim_1", CubeListBuilder.create()
				.texOffs(40, 11)
				.addBox(-3.0F, -2.0F, -2.0F, 8, 2, 2),
			PartPose.offset(0.0F, -2.0F, -3.0F));

		top.addOrReplaceChild("rim_2", CubeListBuilder.create()
				.texOffs(40, 16)
				.addBox(0.0F, -2.0F, -3.0F, 2, 2, 8),
			PartPose.offset(3.0F, -2.0F, 0.0F));

		top.addOrReplaceChild("rim_3", CubeListBuilder.create()
				.texOffs(61, 11)
				.addBox(-5.0F, -2.0F, 0.0F, 8, 2, 2),
			PartPose.offset(0.0F, -2.0F, 3.0F));

		top.addOrReplaceChild("rim_4", CubeListBuilder.create()
				.texOffs(61, 16)
				.addBox(-2.0F, -2.0F, -5.0F, 2, 2, 8),
			PartPose.offset(-3.0F, -2.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	public static LayerDefinition makePot3() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10),
			PartPose.ZERO);

		base.addOrReplaceChild("cup1", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(-7.0F, -10.0F, -7.0F, 14, 10, 14),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		var top = base.addOrReplaceChild("cup2", CubeListBuilder.create()
				.texOffs(0, 38)
				.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8),
			PartPose.offset(0.0F, -12.0F, 0.0F));

		top.addOrReplaceChild("rim_1", CubeListBuilder.create()
				.texOffs(50, 0)
				.addBox(-3.0F, -2.0F, -2.0F, 8, 2, 2),
			PartPose.offset(0.0F, -2.0F, -3.0F));

		top.addOrReplaceChild("rim_2", CubeListBuilder.create()
				.texOffs(50, 5)
				.addBox(0.0F, -2.0F, -3.0F, 2, 2, 8),
			PartPose.offset(3.0F, -2.0F, 0.0F));

		top.addOrReplaceChild("rim_3", CubeListBuilder.create()
				.texOffs(71, 0)
				.addBox(-5.0F, -2.0F, 0.0F, 8, 2, 2),
			PartPose.offset(0.0F, -2.0F, 3.0F));

		top.addOrReplaceChild("rim_4", CubeListBuilder.create()
				.texOffs(71, 5)
				.addBox(-2.0F, -2.0F, -5.0F, 2, 2, 8),
			PartPose.offset(-3.0F, -2.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
