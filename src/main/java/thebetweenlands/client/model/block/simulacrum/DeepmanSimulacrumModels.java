package thebetweenlands.client.model.block.simulacrum;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class DeepmanSimulacrumModels {

	public static LayerDefinition makeSimulacrum1() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-4.5F, -7.0F, -4.5F, 9, 7, 9),
			PartPose.ZERO);

		var face = body.addOrReplaceChild("face", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-4.0F, -7.0F, -4.0F, 8, 7, 8),
			PartPose.offset(0.0F, -7.0F, 0.0F));

		var shoulders = face.addOrReplaceChild("shoulders", CubeListBuilder.create()
				.texOffs(0, 49)
				.addBox(-5.005F, -1.0F, -1.0F, 10, 1, 2),
			PartPose.offset(0.0F, -2.5F, 0.0F));

		var armsFront = shoulders.addOrReplaceChild("arms_front_1", CubeListBuilder.create()
				.texOffs(0, 33)
				.addBox(-5.01F, 0.0F, 0.0F, 10, 4, 3, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, -0.6373942428283291F, 0.0F, 0.0F));

		armsFront.addOrReplaceChild("arms_front_2", CubeListBuilder.create()
				.texOffs(27, 33)
				.addBox(-5.0F, 0.0F, -3.0F, 10, 4, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 3.0F, -0.5462880558742251F, 0.0F, 0.0F));

		var armsBack = shoulders.addOrReplaceChild("arms_back_1", CubeListBuilder.create()
			.texOffs(0, 41)
			.addBox(-5.0F, 0.0F, -3.0F, 10, 4, 3, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, 0.6373942428283291F, 0.0F, 0.0F));

		armsBack.addOrReplaceChild("arms_back_2", CubeListBuilder.create()
				.texOffs(27, 41)
				.addBox(-5.0F, 0.0F, 0.0F, 10, 4, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, -3.0F, 0.5462880558742251F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	public static LayerDefinition makeSimulacrum2() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.5F, -8.0F, -3.5F, 7, 8, 7),
			PartPose.ZERO);

		var bod_top = body.addOrReplaceChild("body_top", CubeListBuilder.create()
				.texOffs(0, 16)
				.addBox(-3.5F, -8.0F, 0.0F, 7, 8, 7),
			PartPose.offset(0.0F, -8.0F, -3.5F));

		var arms = bod_top.addOrReplaceChild("arms_1", CubeListBuilder.create()
				.texOffs(0, 32)
				.addBox(-4.0F, 0.0F, 0.0F, 8, 4, 3),
			PartPose.offsetAndRotation(0.0F, -2.0F, 4.0F, -0.7285004297824331F, 0.0F, 0.0F));

		arms.addOrReplaceChild("arms_2", CubeListBuilder.create()
				.texOffs(0, 40)
				.addBox(-4.01F, 0.0F, -3.0F, 8, 4, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 3.0F, -0.36425021489121656F, 0.0F, 0.0F));

		var rope1 = bod_top.addOrReplaceChild("rope_1", CubeListBuilder.create()
				.texOffs(40, 0)
				.addBox(-0.5F, -0.5F, -4.0F, 3, 1, 8),
			PartPose.offsetAndRotation(-3.7F, 0.9F, 3.5F, 0.0F, 0.0F, -0.18203784098300857F));

		var rope2 = rope1.addOrReplaceChild("rope_2", CubeListBuilder.create()
				.texOffs(40, 10)
				.addBox(0.0F, -1.0F, -4.0F, 3, 1, 8),
			PartPose.offsetAndRotation(2.5F, 0.5F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		var rope3 = rope2.addOrReplaceChild("rope_3", CubeListBuilder.create()
				.texOffs(40, 20)
				.addBox(0.0F, -1.0F, -4.0F, 3, 1, 8),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		rope1.addOrReplaceChild("paper_1", CubeListBuilder.create()
				.texOffs(30, 0)
				.addBox(0.0F, 0.0F, 0.0F, 2, 3, 0.01F),
			PartPose.offsetAndRotation(1.5F, 0.0F, 3.7F, 0.18203784098300857F, -0.091106186954104F, 0.5918411493512771F));

		rope1.addOrReplaceChild("paper_2", CubeListBuilder.create()
				.texOffs(30, 1)
				.addBox(0.0F, 0.0F, 0.0F, 0.01F, 3, 2),
			PartPose.offsetAndRotation(-0.3F, 0.0F, 1.2F, -0.4553564018453205F, 0.27314402793711257F, 0.5009094953223726F));

		rope1.addOrReplaceChild("paper_3", CubeListBuilder.create()
				.texOffs(30, 5)
				.addBox(0.0F, 0.0F, 0.0F, 0.01F, 3, 2),
			PartPose.offsetAndRotation(-0.4F, 0.0F, -2.5F, -0.5462880558742251F, -0.31869712141416456F, 0.36425021489121656F));

		rope1.addOrReplaceChild("paper_4", CubeListBuilder.create()
				.texOffs(30, 12)
				.addBox(0.0F, 0.0F, 0.0F, 3, 2, 0.01F),
			PartPose.offsetAndRotation(2.0F, 0.0F, -3.7F, -0.4553564018453205F, 0.4553564018453205F, 1.0016444577195458F));

		rope3.addOrReplaceChild("paper_5", CubeListBuilder.create()
				.texOffs(30, 16)
				.addBox(0.0F, 0.0F, 0.0F, 3, 2, 0.01F),
			PartPose.offsetAndRotation(0.8F, -0.5F, -3.7F, -0.22759093446006054F, 0.31869712141416456F, 1.0927506446736497F));

		rope3.addOrReplaceChild("paper_6", CubeListBuilder.create()
				.texOffs(30, 17)
				.addBox(0.0F, 0.0F, 0.0F, 0.01F, 2, 3),
			PartPose.offsetAndRotation(2.7F, -0.5F, -2.5F, -0.9560913642424937F, 0.0F, 0.36425021489121656F));

		rope3.addOrReplaceChild("paper_7", CubeListBuilder.create()
				.texOffs(30, 21)
				.addBox(0.0F, 0.0F, 0.0F, 0.01F, 2, 3),
			PartPose.offsetAndRotation(2.7F, -0.5F, 2.0F, -0.9560913642424937F, -0.22759093446006054F, 0.27314402793711257F));

		rope3.addOrReplaceChild("paper_8", CubeListBuilder.create()
				.texOffs(30, 28)
				.addBox(0.0F, 0.0F, 0.0F, 2, 3, 0.01F),
			PartPose.offsetAndRotation(0.5F, -0.5F, 3.7F, 0.31869712141416456F, 0.0F, 1.0471975511965976F));

		return LayerDefinition.create(definition, 64, 64);
	}

	public static LayerDefinition makeSimulacrum3() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(0.0F, -10.0F, 0.0F, 6, 10, 6),
			PartPose.offset(-4.5F, 0.0F, -3.0F));

		var shoulders = body.addOrReplaceChild("shoulders", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-3.5F, 0.0F, 0.0F, 7, 3, 3),
			PartPose.offsetAndRotation(3.0F, -6.7F, 3.0F, -0.8651597102135892F, 0.0F, 0.0F));

		shoulders.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(0, 24)
				.addBox(-3.01F, 0.0F, -3.0F, 3, 3, 3),
			PartPose.offsetAndRotation(-0.5F, 3.0F, 3.0F, -0.27314402793711257F, 0.0F, 0.0F));

		shoulders.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(13, 24)
				.addBox(-2.99F, 0.0F, -3.0F, 3, 3, 3),
			PartPose.offsetAndRotation(3.5F, 3.0F, 3.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var scroll = partDefinition.addOrReplaceChild("scroll", CubeListBuilder.create()
				.texOffs(0, 32)
				.addBox(-1.0F, -2.0F, -3.4F, 2, 2, 7),
			PartPose.offsetAndRotation(3.5F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F, -0.136659280431156F));

		scroll.addOrReplaceChild("scroll_stick", CubeListBuilder.create()
				.texOffs(0, 42)
				.addBox(-0.5F, -0.5F, -4.0F, 1, 1, 8),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.36425021489121656F));

		var paper = scroll.addOrReplaceChild("paper_1", CubeListBuilder.create()
				.texOffs(0, 52)
				.addBox(0.0F, 0.0F, -3.5F, 1, 0, 7),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));

		paper.addOrReplaceChild("paper_2", CubeListBuilder.create()
				.texOffs(-5, 52)
				.addBox(0.0F, 0.0F, -3.5F, 2, 0, 7),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5462880558742251F));


		return LayerDefinition.create(definition, 64, 64);
	}
}
