package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.boss.PrimordialMalevolence;

public class PrimordialMalevolenceModel extends MowzieModelBase<PrimordialMalevolence> {

	public final ModelPart eye;
	public final ModelPart clothes;

	public PrimordialMalevolenceModel(ModelPart root) {
		super(root);
		this.eye = root.getChild("eye");
		this.clothes = root.getChild("cap1");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("eye", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8),
			PartPose.offsetAndRotation(0.0F, -7.0F, -4.0F, 0.045553093477052F, 0.0F, 0.0F));

		var cap1 = partDefinition.addOrReplaceChild("cap1", CubeListBuilder.create()
				.texOffs(214, 0).addBox(-5.5F, 0.0F, -5.5F, 11, 27, 11),
			PartPose.offsetAndRotation(0.0F, -18.12F, -4.09F, 0.045553093477052F, 0.0F, 0.0F));
		cap1.addOrReplaceChild("cap2", CubeListBuilder.create()
				.texOffs(214, 39).addBox(-4.5F, 0.0F, -3.0F, 9, 9, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.22759093446006054F, 0.0F, 0.0F));

		var cape1 = partDefinition.addOrReplaceChild("cape1", CubeListBuilder.create()
				.texOffs(160, 0).addBox(-10.0F, 0.0F, 0.0F, 20, 8, 0),
			PartPose.offsetAndRotation(0.0F, -0.8F, -5.7F, 0.091106186954104F, 0.0F, 0.0F));

		var cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create()
				.texOffs(160, 9).addBox(-11.0F, -6.0F, 0.0F, 22, 6, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.3203415791337103F, 0.0F, 0.0F));

		var cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create()
				.texOffs(160, 16).addBox(-12.0F, -4.0F, 0.0F, 24, 4, 0),
			PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.27314402793711257F, 0.0F, 0.0F));

		var cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create()
				.texOffs(160, 21).addBox(-12.0F, -4.0F, 0.0F, 24, 4, 0),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.8196066167365371F, 0.0F, 0.0F));

		var cape5 = cape4.addOrReplaceChild("cape5", CubeListBuilder.create()
				.texOffs(160, 26).addBox(-12.0F, -8.0F, 0.0F, 24, 8, 0),
			PartPose.offsetAndRotation(0.0F, -4.0F, 0.0F, -0.6829473363053812F, 0.0F, 0.0F));

		cape5.addOrReplaceChild("cape6", CubeListBuilder.create()
				.texOffs(160, 35).addBox(-13.0F, -16.0F, 0.0F, 26, 16, 0),
			PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, -0.18203784098300857F, 0.0F, 0.0F));

		var chainpiece1 = cape1.addOrReplaceChild("chainpiece1", CubeListBuilder.create()
				.texOffs(160, 104).addBox(-1.0F, -1.5F, 0.0F, 6, 4, 0),
			PartPose.offsetAndRotation(-6.0F, 8.0F, 0.0F, -0.045553093477052F, 0.045553093477052F, 0.5009094953223726F));

		cape1.addOrReplaceChild("chainpiece2", CubeListBuilder.create()
				.texOffs(184, 104).addBox(-5.0F, -1.5F, 0.0F, 6, 4, 0),
			PartPose.offsetAndRotation(6.0F, 8.0F, 0.0F, -0.045553093477052F, -0.045553093477052F, -0.5009094953223726F));

		chainpiece1.addOrReplaceChild("chainpiece3", CubeListBuilder.create()
				.texOffs(172, 104).addBox(-2.0F, -2.0F, 0.0F, 7, 4, 0),
			PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, -0.27314402793711257F, 0.0F, -0.5009094953223726F));

		return LayerDefinition.create(definition, 512, 256);
	}
}
