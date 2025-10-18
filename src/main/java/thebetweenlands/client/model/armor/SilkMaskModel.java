package thebetweenlands.client.model.armor;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SilkMaskModel extends BLArmorModel {

	public SilkMaskModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create() {
		MeshDefinition definition = HumanoidArmorModel.createBodyLayer(CubeDeformation.NONE);
		PartDefinition partDefinition = definition.getRoot();

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

		var mask = head.addOrReplaceChild("mask", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -3.0F, 0.0F, 8, 3, 0),
			PartPose.offset(0.0F, -0.5F, -4.6F));
		mask.addOrReplaceChild("mask2", CubeListBuilder.create()
			.texOffs(0, 11).addBox(-3.0F, 0.0F, 0.0F, 6, 2, 0),
			PartPose.rotation(0.8727F, 0.0F, 0.0F));

		var lowBinder = mask.addOrReplaceChild("low_binder", CubeListBuilder.create(), PartPose.ZERO);

		var binder1a = lowBinder.addOrReplaceChild("binder1a", CubeListBuilder.create()
			.texOffs(15, 24).addBox(0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0436F, 0.0F));
		var binder1b = binder1a.addOrReplaceChild("binder1b", CubeListBuilder.create()
				.texOffs(10, 24).addBox(0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, -0.0436F, 0.0F));
		var binder1c = binder1b.addOrReplaceChild("binder1c", CubeListBuilder.create()
				.texOffs(20, 11).addBox(0.0F, -1.0F, 0.0F, 0, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.1309F, 0.0F, 0.0F));
		var binder1d = binder1c.addOrReplaceChild("binder1d", CubeListBuilder.create()
				.texOffs(5, 24).addBox(0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, -0.0436F, 0.0F));
		binder1d.addOrReplaceChild("binder1e", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-4.0F, -1.0F, 2.0F, 4, 1, 0),
			PartPose.ZERO);

		var binder2a = lowBinder.addOrReplaceChild("binder2a", CubeListBuilder.create()
				.texOffs(0, 24).addBox( 0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, -0.0436F, 0.0F));
		var binder2b = binder2a.addOrReplaceChild("binder2b", CubeListBuilder.create()
				.texOffs(20, 20).addBox( 0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 0.0436F, 0.0F));
		var binder2c = binder2b.addOrReplaceChild("binder2c", CubeListBuilder.create()
				.texOffs(13, 11).addBox( 0.0F, -1.0F, 0.0F, 0, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.1309F, 0.0F, 0.0F));
		var binder2d = binder2c.addOrReplaceChild("binder2d", CubeListBuilder.create()
				.texOffs(15, 20).addBox( 0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.0436F, 0.0F));
		binder2d.addOrReplaceChild("binder2e", CubeListBuilder.create()
				.texOffs(0, 16).addBox( 0.0F, -1.0F, 2.0F, 4, 1, 0),
			PartPose.ZERO);

		lowBinder.addOrReplaceChild("connector", CubeListBuilder.create()
				.texOffs(10, 20).addBox(-0.5F, -2.0F, 0.0F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, -0.5F, 8.25F, -0.0873F, 0.0F, 0.0F));

		var highBinder = mask.addOrReplaceChild("high_binder", CubeListBuilder.create(), PartPose.ZERO);

		var binder3a = highBinder.addOrReplaceChild("binder3a", CubeListBuilder.create()
				.texOffs(5, 20).addBox(0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0436F, 0.0F));
		var binder3b = binder3a.addOrReplaceChild("binder3b", CubeListBuilder.create()
				.texOffs(11, 4).addBox(0.0F, -1.0F, 0.0F, 0, 1, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, -0.0436F, 0.0F));
		var binder3c = binder3b.addOrReplaceChild("binder3c", CubeListBuilder.create()
				.texOffs(0, 20).addBox(0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.0F, -0.0436F, 0.0F));
		binder3c.addOrReplaceChild("binder3d", CubeListBuilder.create()
				.texOffs(17, 0).addBox(-8.0F, -1.0F, 0.0F, 8, 1, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 0.0436F, 0.0F));

		var binder4a = highBinder.addOrReplaceChild("binder4a", CubeListBuilder.create()
				.texOffs(14, 16).addBox( 0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, -0.0436F, 0.0F));
		var binder4b = binder4a.addOrReplaceChild("binder4b", CubeListBuilder.create()
				.texOffs(0, 4).addBox( 0.0F, -1.0F, 0.0F, 0, 1, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, 0.0436F, 0.0F));
		binder4b.addOrReplaceChild("binder4c", CubeListBuilder.create()
				.texOffs(9, 16).addBox( 0.0F, -1.0F, 0.0F, 0, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.0F, 0.0F, 0.0436F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}
}
