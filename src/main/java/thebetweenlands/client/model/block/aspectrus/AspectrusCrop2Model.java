package thebetweenlands.client.model.block.aspectrus;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class AspectrusCrop2Model {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var crop = partDefinition.addOrReplaceChild("crop", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-3.0F, -16.0F, -3.0F, 6, 16, 6),
			PartPose.offset(0.0F, 24.0F, 0.0F));

		var leaf1 = crop.addOrReplaceChild("leaf1", CubeListBuilder.create()
				.texOffs(22, 0).addBox(-2.5F, 0.1F, -2.5F, 5, 0, 3),
			PartPose.offsetAndRotation(-1.5F, -2.0F, -3.1F, -0.27314402793711257F, 0.36425021489121656F, 0.0F));
		var leaf1b = leaf1.addOrReplaceChild("leaf1b", CubeListBuilder.create()
				.texOffs(25, 5).addBox(-2.5F, 0.0F, -4.0F, 5, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.4553564018453205F, 0.0F, 0.0F));
		var leaf2 = crop.addOrReplaceChild("leaf2", CubeListBuilder.create()
				.texOffs(40, 0).addBox(-3.0F, 0.0F, -3.5F, 6, 0, 4),
			PartPose.offsetAndRotation(2.5F, -5.0F, 2.0F, -0.136659280431156F, -2.0488420089161434F, 0.0F));
		var leaf2b = leaf2.addOrReplaceChild("leaf2b", CubeListBuilder.create()
				.texOffs(44, 5).addBox(-3.0F, 0.0F, -5.0F, 6, 1, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.5F, 0.31869712141416456F, 0.0F, 0.0F));
		var leaf3 = crop.addOrReplaceChild("leaf3", CubeListBuilder.create()
				.texOffs(21, 11).addBox(-2.5F, 0.0F, -3.5F, 5, 0, 4),
			PartPose.offsetAndRotation(-2.0F, -3.5F, 2.5F, -0.27314402793711257F, 2.5497515042385164F, 0.0F));
		var leaf3b = leaf3.addOrReplaceChild("leaf3b", CubeListBuilder.create()
				.texOffs(25, 16).addBox(-2.5F, 0.0F, -5.0F, 5, 1, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.5F, 0.5462880558742251F, 0.0F, 0.0F));
		var leaf4 = crop.addOrReplaceChild("leaf4", CubeListBuilder.create()
				.texOffs(41, 12).addBox(-2.0F, 0.0F, -3.5F, 4, 0, 4),
			PartPose.offsetAndRotation(1.0F, -7.0F, -3.0F, -0.22759093446006054F, -0.27314402793711257F, 0.0F));
		var leaf4b = leaf4.addOrReplaceChild("leaf4b", CubeListBuilder.create()
				.texOffs(45, 17).addBox(-2.0F, 0.0F, -4.0F, 4, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.5F, 0.4553564018453205F, 0.0F, 0.0F));
		var leaf5 = crop.addOrReplaceChild("leaf5", CubeListBuilder.create()
				.texOffs(58, 12).addBox(-2.5F, 0.0F, -4.0F, 5, 0, 4),
			PartPose.offsetAndRotation(-3.0F, -6.5F, 1.0F, -0.31869712141416456F, 1.7756979809790308F, 0.0F));
		var leaf5b = leaf5.addOrReplaceChild("leaf5b", CubeListBuilder.create()
				.texOffs(62, 17).addBox(-2.5F, 0.0F, -4.0F, 5, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.5918411493512771F, 0.0F, 0.0F));
		var leaf6 = crop.addOrReplaceChild("leaf6", CubeListBuilder.create()
				.texOffs(0, 23).addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3),
			PartPose.offsetAndRotation(3.0F, -9.0F, -2.0F, -0.27314402793711257F, -1.1838568316277536F, 0.0F));
		var leaf6b = leaf6.addOrReplaceChild("leaf6b", CubeListBuilder.create()
				.texOffs(0, 27).addBox(-2.0F, 0.0F, -4.0F, 4, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.5009094953223726F, 0.0F, 0.0F));
		var leaf7 = crop.addOrReplaceChild("leaf7", CubeListBuilder.create()
				.texOffs(0, 33).addBox(-2.5F, 0.0F, -3.5F, 5, 0, 4),
			PartPose.offsetAndRotation(-1.0F, -10.5F, 3.0F, -0.22759093446006054F, 2.9595548126067843F, 0.0F));
		var leaf7b = leaf7.addOrReplaceChild("leaf7b", CubeListBuilder.create()
				.texOffs(0, 38).addBox(-2.5F, 0.0F, -4.0F, 5, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.4F, 0.6373942428283291F, 0.0F, 0.0F));
		var leaf8 = crop.addOrReplaceChild("leaf8", CubeListBuilder.create()
				.texOffs(0, 44).addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3),
			PartPose.offsetAndRotation(3.0F, -11.5F, 1.0F, -0.18203784098300857F, -1.8668041679331349F, 0.0F));
		var leaf8b = leaf8.addOrReplaceChild("leaf8b", CubeListBuilder.create()
				.texOffs(0, 48).addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.4553564018453205F, 0.0F, 0.0F));
		var leaf9 = crop.addOrReplaceChild("leaf9", CubeListBuilder.create()
				.texOffs(22, 23).addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3),
			PartPose.offsetAndRotation(-3.0F, -10.0F, -1.5F, -0.22776546738526002F, 1.0471975511965976F, 0.0F));
		var leaf9b = leaf9.addOrReplaceChild("leaf9b", CubeListBuilder.create()
				.texOffs(22, 27).addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.5009094953223726F, 0.0F, 0.0F));
		var leaf10 = crop.addOrReplaceChild("leaf10", CubeListBuilder.create()
				.texOffs(22, 32).addBox(-2.0F, 0.0F, -2.5F, 4, 0, 3),
			PartPose.offsetAndRotation(0.9F, -13.0F, -3.0F, -0.27314402793711257F, -0.091106186954104F, 0.0F));
		var leaf10b = leaf10.addOrReplaceChild("leaf10b", CubeListBuilder.create()
				.texOffs(22, 36).addBox(-2.0F, 0.0F, -3.0F, 4, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.5918411493512771F, 0.0F, 0.0F));
		var leaf11 = crop.addOrReplaceChild("leaf11", CubeListBuilder.create()
				.texOffs(22, 41).addBox(-2.5F, 0.0F, -2.5F, 5, 0, 3),
			PartPose.offsetAndRotation(3.0F, -14.5F, 2.5F, -0.27314402793711257F, -2.4586453172844123F, 0.0F));
		var leaf11b = leaf11.addOrReplaceChild("leaf11b", CubeListBuilder.create()
				.texOffs(22, 45).addBox(-2.5F, 0.0F, -4.0F, 5, 1, 4),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, 0.5009094953223726F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
