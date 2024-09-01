package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class WaystoneModel {
	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var slate1 = partDefinition.addOrReplaceChild("slate_1", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-7.0F, -19.0F, -4.0F, 14, 19, 8),
			PartPose.ZERO);

		var slate2 = slate1.addOrReplaceChild("slate_2", CubeListBuilder.create()
				.texOffs(0, 28)
				.addBox(-7.0F, -4.0F, -4.0F, 13, 4, 8),
			PartPose.offset(0.0F, -19.0F, 0.0F));

		slate1.addOrReplaceChild("slate_2b", CubeListBuilder.create()
				.texOffs(43, 28)
				.addBox(0.0F, -2.0F, -2.0F, 1, 2, 6),
			PartPose.offset(6.0F, -19.0F, 0.0F));

		var slate3a = slate2.addOrReplaceChild("slate_3a", CubeListBuilder.create()
				.texOffs(0, 41)
				.addBox(-7.0F, -5.0F, -4.0F, 5, 5, 8),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		slate3a.addOrReplaceChild("slate_3a1", CubeListBuilder.create()
				.texOffs(0, 55)
				.addBox(-2.0F, -1.0F, -4.0F, 1, 1, 8),
			PartPose.ZERO);

		var slate3b = slate2.addOrReplaceChild("slate_3b", CubeListBuilder.create()
				.texOffs(27, 41)
				.addBox(2.0F, -5.0F, -4.0F, 4, 5, 8),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		slate3b.addOrReplaceChild("slate_3b1", CubeListBuilder.create()
				.texOffs(27, 55)
				.addBox(1.0F, -1.0F, -4.0F, 1, 1, 8),
			PartPose.ZERO);

		var slate4 = slate2.addOrReplaceChild("slate_4a", CubeListBuilder.create()
				.texOffs(0, 65)
				.addBox(-6.0F, -3.0F, -4.0F, 12, 3, 8),
			PartPose.offset(0.0F, -9.0F, 0.0F));

		slate2.addOrReplaceChild("slate_4b", CubeListBuilder.create()
				.texOffs(41, 65)
				.addBox(-1.0F, -2.0F, -4.0F, 1, 2, 6),
			PartPose.offset(-6.0F, -9.0F, 0.0F));

		var slate5a = slate4.addOrReplaceChild("slate_5a", CubeListBuilder.create()
				.texOffs(0, 77)
				.addBox(-6.0F, -10.0F, -4.0F, 4, 10, 8),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		slate5a.addOrReplaceChild("slate_5a1", CubeListBuilder.create()
				.texOffs(0, 96)
				.addBox(-2.0F, 0.0F, -4.0F, 1, 1, 8),
			PartPose.offset(0.0F, -10.0F, 0.0F));

		var slate5b = slate4.addOrReplaceChild("slate_5b", CubeListBuilder.create()
				.texOffs(25, 77)
				.addBox(2.0F, -10.0F, -4.0F, 4, 10, 8),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		slate5b.addOrReplaceChild("slate_5b1", CubeListBuilder.create()
				.texOffs(25, 96)
				.addBox(1.0F, 0.0F, -4.0F, 1, 1, 8),
			PartPose.offset(0.0F, -10.0F, 0.0F));

		var slate6 = slate4.addOrReplaceChild("slate_6", CubeListBuilder.create()
				.texOffs(0, 106)
				.addBox(-6.0F, -3.0F, -4.0F, 12, 3, 8),
			PartPose.offset(0.0F, -13.0F, 0.0F));

		var slate7 = slate6.addOrReplaceChild("slate_7", CubeListBuilder.create()
				.texOffs(45, 0)
				.addBox(-6.0F, -3.0F, -4.0F, 11, 3, 7),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		slate7.addOrReplaceChild("slate_7b", CubeListBuilder.create()
				.texOffs(45, 11)
				.addBox(-6.0F, -2.0F, 0.0F, 8, 2, 1),
			PartPose.offset(0.0F, 0.0F, 3.0F));

		slate7.addOrReplaceChild("slate_8", CubeListBuilder.create()
				.texOffs(45, 15)
				.addBox(-5.0F, -1.0F, -4.0F, 8, 1, 7),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		var rope1 = partDefinition.addOrReplaceChild("rope_1", CubeListBuilder.create()
				.texOffs(65, 40)
				.addBox(-1.0F, 0.0F, -4.5F, 1, 1, 9),
			PartPose.offsetAndRotation(-7.0F, -17.0F, 0.0F, 0.0F, 0.0F, -0.6829473363053812F));

		rope1.addOrReplaceChild("object_4", CubeListBuilder.create()
				.texOffs(65, 89)
				.addBox(0.0F, 0.0F, -4.0F, 0, 7, 8),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6829473363053812F));

		var rope2 = rope1.addOrReplaceChild("rope_2", CubeListBuilder.create()
				.texOffs(86, 40)
				.addBox(0.0F, 0.0F, -4.5F, 1, 6, 9),
			PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		rope2.addOrReplaceChild("object_3", CubeListBuilder.create()
				.texOffs(104, 88)
				.addBox(-1.0F, 0.0F, 0.0F, 8, 10, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.4F, 0.0F, 0.0F, 0.8651597102135892F));

		var rope3 = rope2.addOrReplaceChild("rope_3", CubeListBuilder.create()
				.texOffs(107, 40)
				.addBox(0.0F, 0.0F, -4.5F, 1, 6, 9),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		rope3.addOrReplaceChild("object_1", CubeListBuilder.create()
				.texOffs(65, 88)
				.addBox(0.0F, 0.0F, 0.0F, 10, 8, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.4F, 0.0F, 0.0F, 1.0471975511965976F));

		var rope4 = rope3.addOrReplaceChild("rope_4", CubeListBuilder.create()
				.texOffs(65, 56)
				.addBox(0.0F, 0.0F, -4.5F, 1, 5, 9),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		rope4.addOrReplaceChild("object_2", CubeListBuilder.create()
				.texOffs(86, 80)
				.addBox(0.0F, 0.0F, -4.0F, 0, 5, 8),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.0F, 0.0F, 1.2292353921796064F));

		var rope5 = partDefinition.addOrReplaceChild("rope_5", CubeListBuilder.create()
				.texOffs(86, 56)
				.addBox(-1.0F, 0.0F, -4.5F, 1, 1, 9),
			PartPose.offsetAndRotation(-7.0F, -23.1F, 0.0F, 0.0F, 0.0F, -0.8196066167365371F));

		var rope6 = rope5.addOrReplaceChild("rope_6", CubeListBuilder.create()
				.texOffs(107, 56)
				.addBox(0.0F, 0.0F, -4.5F, 1, 6, 9),
			PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		rope6.addOrReplaceChild("object_5", CubeListBuilder.create()
				.texOffs(82, 97)
				.addBox(-0.2F, 0.0F, 0.0F, 9, 8, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, -4.4F, 0.0F, 0.0F, 1.0016444577195458F));

		var rope7 = rope6.addOrReplaceChild("rope_7", CubeListBuilder.create()
				.texOffs(65, 72)
				.addBox(0.0F, 0.0F, -4.5F, 1, 5, 9),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		var rope8 = rope7.addOrReplaceChild("rope_8", CubeListBuilder.create()
				.texOffs(86, 72)
				.addBox(0.0F, 0.0F, -4.5F, 1, 5, 9),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		var object6 = rope8.addOrReplaceChild("object_6", CubeListBuilder.create()
				.texOffs(65, 101)
				.addBox(0.0F, 0.0F, -2.0F, 0, 3, 4),
			PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.0F, 0.0F, 1.4570008595648662F));

		var object6a = object6.addOrReplaceChild("object_6a", CubeListBuilder.create()
				.texOffs(65, 105)
				.addBox(0.0F, 0.0F, -2.0F, 0, 2, 4),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.31869712141416456F));

		object6a.addOrReplaceChild("object_6b", CubeListBuilder.create()
				.texOffs(65, 108)
				.addBox(0.0F, 0.0F, -2.0F, 0, 2, 4),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.5462880558742251F));

		return LayerDefinition.create(definition, 128, 128);
	}
}
