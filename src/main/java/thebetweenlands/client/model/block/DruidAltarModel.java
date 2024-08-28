package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class DruidAltarModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("altar1", CubeListBuilder.create()
			.addBox(-16.0F, 0.0F, -16.0F, 32, 3, 32, new CubeDeformation(-0.001F)),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar2", CubeListBuilder.create()
				.texOffs(0, 35)
				.addBox(-13.0F, -6.0F, -13.0F, 26, 6, 26),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar3", CubeListBuilder.create()
				.texOffs(0, 68)
				.addBox(-15.0F, -9.0F, -15.0F, 30, 3, 30, new CubeDeformation(-0.001F)),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar4", CubeListBuilder.create()
				.texOffs(0, 102)
				.addBox(-14.0F, -11.0F, -14.0F, 28, 2, 9),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar5", CubeListBuilder.create()
				.texOffs(0, 115)
				.addBox(-14.0F, -11.0F, 5.0F, 28, 2, 9),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar6", CubeListBuilder.create()
				.texOffs(76, 102)
				.addBox(5.0F, -11.0F, -5.0F, 9, 2, 10),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar7", CubeListBuilder.create()
				.texOffs(76, 116)
				.addBox(-14.0F, -11.0F, -5.0F, 9, 2, 10),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar8", CubeListBuilder.create()
				.texOffs(129, 0)
				.addBox(-17.0F, -1.0F, -17.0F, 6, 4, 6),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar9", CubeListBuilder.create()
				.texOffs(129, 11)
				.addBox(11.0F, -1.0F, -17.0F, 6, 4, 6),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar10", CubeListBuilder.create()
				.texOffs(129, 22)
				.addBox(11.0F, -1.0F, 11.0F, 6, 4, 6),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar11", CubeListBuilder.create()
				.texOffs(129, 34)
				.addBox(-17.0F, -1.0F, 11.0F, 6, 4, 6),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar12", CubeListBuilder.create()
				.texOffs(129, 47)
				.addBox(-8.0F, -12.0F, -8.0F, 16, 1, 3, new CubeDeformation(0.001F)),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar13", CubeListBuilder.create()
				.texOffs(129, 53)
				.addBox(-8.0F, -12.0F, 5.0F, 16, 1, 3, new CubeDeformation(0.001F)),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar14", CubeListBuilder.create()
				.texOffs(129, 59)
				.addBox(-8.0F, -12.0F, -5.0F, 3, 1, 10, new CubeDeformation(0.001F)),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("altar15", CubeListBuilder.create()
				.texOffs(129, 72)
				.addBox(5.0F, -12.0F, -5.0F, 3, 1, 10, new CubeDeformation(0.001F)),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		partDefinition.addOrReplaceChild("grass", CubeListBuilder.create()
				.texOffs(190, 0)
				.addBox(0.0F, 0.0F, -9.0F, 0, 4, 18),
			PartPose.offset(15.0F, -9.0F, 3.0F));

		partDefinition.addOrReplaceChild("shroom1", CubeListBuilder.create()
				.texOffs(190, 0)
				.addBox(0.0F, -3.0F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(8.0F, -2.0F, -14.0F, 0.3346075F, 0.0F, 0.2602503F));

		partDefinition.addOrReplaceChild("shroom2", CubeListBuilder.create()
				.texOffs(190, 6)
				.addBox(-0.5F, -4.5F, -2.0F, 3, 2, 3),
			PartPose.offsetAndRotation(8.0F, -2.0F, -14.0F, 0.0743572F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("shroom3", CubeListBuilder.create()
				.texOffs(190, 0)
				.addBox(0.0F, -2.5F, 0.0F, 1, 3, 1),
			PartPose.offsetAndRotation(5.0F, -2.0F, -14.0F, 0.4833219F, 0.4089647F, 0.0F));

		partDefinition.addOrReplaceChild("shroom4", CubeListBuilder.create()
				.texOffs(203, 6)
				.addBox(-0.5F, -3.0F, -1.5F, 2, 1, 2),
			PartPose.offsetAndRotation(5.0F, -2.0F, -14.0F, 0.0F, 0.4089656F, 0.0F));

		partDefinition.addOrReplaceChild("shroom5", CubeListBuilder.create()
				.texOffs(202, 0)
				.addBox(-1.0F, -2.0F, -1.0F, 1, 2, 1),
			PartPose.offsetAndRotation(8.0F, 0.0F, -16.0F, 0.3346075F, -0.2230717F, 0.0F));

		partDefinition.addOrReplaceChild("shroom6", CubeListBuilder.create()
				.texOffs(203, 12)
				.addBox(-1.5F, -2.0F, -2.0F, 2, 1, 2),
			PartPose.offsetAndRotation(8.0F, 0.0F, -16.0F, 0.0F, -0.2230705F, 0.0F));

		return LayerDefinition.create(definition, 256, 128);
	}

	public static LayerDefinition makeStones() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("stone1", CubeListBuilder.create()
				.texOffs(160, 0)
				.addBox(-1.0F, -1.0F, -1.0F, 4, 4, 4),
			PartPose.offsetAndRotation(0.0F, -12.0F, -2.0F, 0.5711987F, 0.3046393F, -0.1903996F));

		partDefinition.addOrReplaceChild("stone2", CubeListBuilder.create()
				.texOffs(160, 10)
				.addBox(-1.0F, -2.0F, -2.0F, 3, 3, 3),
			PartPose.offsetAndRotation(-4.0F, -4.0F, -3.0F, -0.4089647F, 0F, 0.2602503F));

		partDefinition.addOrReplaceChild("stone3", CubeListBuilder.create()
				.texOffs(160, 18)
				.addBox(-2F, -2F, -2F, 5, 5, 5),
			PartPose.offsetAndRotation(4.0F, 1.0F, -1.0F, 0F, -0.4089647F, 0.3892394F));

		partDefinition.addOrReplaceChild("stone4", CubeListBuilder.create()
				.texOffs(160, 30)
				.addBox(-.01F, -1.0F, -2.0F, 3, 3, 3),
			PartPose.offsetAndRotation(-2.0F, -4.0F, 5.0F, 0.7807508F, -0.7261189F, 0.3346075F));


		return LayerDefinition.create(definition, 256, 128);
	}
}
