package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.SludgeWormEggSac;

public class SludgeWormEggSacModel extends MowzieModelBase<SludgeWormEggSac> {

	private final ModelPart root;

	public SludgeWormEggSacModel(ModelPart root) {
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var egg1 = partDefinition.addOrReplaceChild("egg1", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4, 5, 4),
			PartPose.offsetAndRotation(-2.0F, 24.0F, -4.0F, 0.136659280431156F, 0.22759093446006054F, 0.0F));
		egg1.addOrReplaceChild("webbing1a", CubeListBuilder.create()
				.texOffs(17, 0).addBox(-2.0F, 0.0F, 0.0F, 4, 4, 0),
			PartPose.offsetAndRotation(0.0F, -2.0F, 2.0F, 0.40980330836826856F, 0.0F, -0.091106186954104F));
		egg1.addOrReplaceChild("webbing1b", CubeListBuilder.create()
				.texOffs(26, 0).addBox(0.0F, 0.0F, -2.0F, 0, 3, 4),
			PartPose.offsetAndRotation(-2.0F, -2.0F, 0.0F, -0.091106186954104F, 0.0F, 0.4553564018453205F));

		var egg2 = partDefinition.addOrReplaceChild("egg2", CubeListBuilder.create()
				.texOffs(0, 10).addBox(-3.0F, -6.0F, -3.0F, 6, 7, 6),
			PartPose.offsetAndRotation(2.0F, 24.0F, 2.0F, -0.045553093477052F, -0.18203784098300857F, 0.0F));
		egg2.addOrReplaceChild("egg2b", CubeListBuilder.create()
				.texOffs(25, 10).addBox(-2.0F, -1.0F, -2.0F, 4, 1, 4),
			PartPose.offset(0.0F, -6.0F, 0.0F));
		var webbing2a = egg2.addOrReplaceChild("webbing2a", CubeListBuilder.create()
				.texOffs(42, 10).addBox(0.0F, 0.0F, 0.0F, 3, 2, 0),
			PartPose.offsetAndRotation(0.0F, -4.0F, -3.0F, -0.36425021489121656F, 0.0F, -0.091106186954104F));
		webbing2a.addOrReplaceChild("webbing2a2", CubeListBuilder.create()
				.texOffs(49, 10).addBox(0.0F, 0.0F, 0.0F, 3, 4, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.18203784098300857F, 0.0F, 0.0F));
		egg2.addOrReplaceChild("webbing2b", CubeListBuilder.create()
				.texOffs(80, 10).addBox(-3.0F, 0.0F, 0.0F, 5, 4, 0),
			PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, -0.40980330836826856F, 0.0F, 0.0F));
		egg2.addOrReplaceChild("webbing2c", CubeListBuilder.create()
				.texOffs(56, 10).addBox(0.0F, 0.0F, -3.0F, 0, 4, 6),
			PartPose.offsetAndRotation(-3.0F, -3.0F, 0.0F, 0.091106186954104F, 0.0F, 0.27314402793711257F));
		egg2.addOrReplaceChild("webbing2d", CubeListBuilder.create()
				.texOffs(69, 10).addBox(-2.9F, 0.0F, 0.0F, 5, 4, 0),
			PartPose.offsetAndRotation(0.0F, -3.0F, 3.0F, 0.31869712141416456F, 0.0F, 0.18203784098300857F));

		var egg3 = partDefinition.addOrReplaceChild("egg3", CubeListBuilder.create()
				.texOffs(0, 24).addBox(-2.0F, -4.0F, -2.0F, 4, 5, 4),
			PartPose.offsetAndRotation(2.0F, 24.0F, 2.0F, -0.045553093477052F, -0.36425021489121656F, 0.045553093477052F));
		egg3.addOrReplaceChild("webbing3a", CubeListBuilder.create()
				.texOffs(17, 24).addBox(-2.0F, 0.0F, 0.0F, 4, 4, 0),
			PartPose.offsetAndRotation(0.0F, -3.0F, 2.0F, 0.31869712141416456F, 0.0F, 0.0F));

		var egg4 = partDefinition.addOrReplaceChild("egg4", CubeListBuilder.create()
				.texOffs(0, 34).addBox(-1.5F, -3.0F, -1.5F, 3, 4, 3),
			PartPose.offsetAndRotation(-5.0F, 24.0F, 1.0F, -0.045553093477052F, -0.40980330836826856F, -0.091106186954104F));
		egg4.addOrReplaceChild("webbing4a", CubeListBuilder.create()
				.texOffs(13, 34).addBox(0.0F, 0.0F, -1.5F, 0, 3, 3),
			PartPose.offsetAndRotation(1.5F, -2.0F, 0.0F, 0.0F, 0.0F, -0.31869712141416456F));

		var egg5 = partDefinition.addOrReplaceChild("egg5", CubeListBuilder.create()
				.texOffs(0, 42).addBox(-3.0F, -5.0F, -3.0F, 6, 6, 6),
			PartPose.offsetAndRotation(3.0F, 24.0F, -4.0F, 0.045553093477052F, -0.7740535232594852F, 0.091106186954104F));
		egg5.addOrReplaceChild("egg5b", CubeListBuilder.create()
				.texOffs(25, 42).addBox(-2.0F, -1.0F, -2.0F, 4, 1, 4),
			PartPose.offset(0.0F, -5.0F, 0.0F));
		egg5.addOrReplaceChild("webbing5a", CubeListBuilder.create()
				.texOffs(42, 42).addBox(-3.0F, 0.0F, 0.0F, 6, 4, 0),
			PartPose.offsetAndRotation(0.0F, -3.0F, 3.0F, 0.36425021489121656F, 0.0F, 0.0F));
		var webbing5b = egg5.addOrReplaceChild("webbing5b", CubeListBuilder.create()
				.texOffs(55, 42).addBox(0.0F, 0.0F, 0.0F, 3, 2, 0),
			PartPose.offsetAndRotation(0.0F, -4.0F, -3.0F, -0.22759093446006054F, 0.0F, -0.091106186954104F));
		webbing5b.addOrReplaceChild("webbing5b2", CubeListBuilder.create()
				.texOffs(62, 42).addBox(-1.0F, 0.0F, 0.0F, 4, 2, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.27314402793711257F, 0.0F, 0.0F));

		var egg6 = partDefinition.addOrReplaceChild("egg6", CubeListBuilder.create()
				.texOffs(0, 55).addBox(-2.5F, -5.0F, -2.5F, 5, 6, 5),
			PartPose.offsetAndRotation(-2.0F, 24.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F));
		egg6.addOrReplaceChild("webbing6a", CubeListBuilder.create()
				.texOffs(21, 55).addBox(0.0F, 0.0F, -2.5F, 0, 4, 5),
			PartPose.offsetAndRotation(-2.5F, -3.0F, 0.0F, 0.0F, 0.0F, 0.36425021489121656F));
		egg6.addOrReplaceChild("webbing6b", CubeListBuilder.create()
				.texOffs(32, 55).addBox(-2.5F, 0.0F, 0.0F, 5, 4, 0),
			PartPose.offsetAndRotation(0.0F, -3.0F, 2.5F, 0.36425021489121656F, 0.0F, 0.045553093477052F));

		var egg7 = partDefinition.addOrReplaceChild("egg7", CubeListBuilder.create()
				.texOffs(0, 67).addBox(-2.0F, -4.0F, -2.0F, 4, 5, 4),
			PartPose.offsetAndRotation(-1.5F, 24.0F, 4.5F, -0.18203784098300857F, -0.4553564018453205F, -0.136659280431156F));
		egg7.addOrReplaceChild("webbing7a", CubeListBuilder.create()
				.texOffs(17, 67).addBox(-2.0F, 0.0F, 0.0F, 4, 3, 0),
			PartPose.offsetAndRotation(0.0F, -3.0F, 2.0F, 0.27314402793711257F, 0.0F, 0.0F));
		egg7.addOrReplaceChild("webbing7b", CubeListBuilder.create()
				.texOffs(26, 67).addBox(0.0F, 0.0F, -2.0F, 0, 4, 4),
			PartPose.offsetAndRotation(-2.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.22759093446006054F));

		var egg8 = partDefinition.addOrReplaceChild("egg8", CubeListBuilder.create()
				.texOffs(0, 77).addBox(-1.5F, -3.0F, -1.5F, 3, 4, 3),
			PartPose.offsetAndRotation(6.0F, 24.0F, -0.5F, -0.045553093477052F, 0.0017453292519943296F, 0.136659280431156F));
		egg8.addOrReplaceChild("webbing8a", CubeListBuilder.create()
				.texOffs(13, 77).addBox(-1.5F, 0.0F, 0.0F, 3, 3, 0),
			PartPose.offsetAndRotation(0.0F, -2.0F, 1.5F, 0.31869712141416456F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 128);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(SludgeWormEggSac entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}
}
