package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawHatchling;

public class ChiromawEggModel extends MowzieModelBase<ChiromawHatchling> {

	public ChiromawEggModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10),
			PartPose.offset(0.0F, 24.0F, 0.0F));

		var piece1 = base.addOrReplaceChild("egg1", CubeListBuilder.create()
			.texOffs(0, 13).addBox(-6.0F, -8.0F, -6.0F, 12, 8, 12),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		var piece2 = piece1.addOrReplaceChild("egg2", CubeListBuilder.create()
				.texOffs(0, 34).addBox(-5.0F, -4.0F, -5.0F, 10, 4, 10),
			PartPose.offset(0.0F, -8.0F, 0.0F));

		piece2.addOrReplaceChild("egg3", CubeListBuilder.create()
				.texOffs(0, 49).addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
