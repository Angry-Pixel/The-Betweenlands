package thebetweenlands.client.model.block.aspectrus;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class AspectrusCrop3Model {

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
			PartPose.offsetAndRotation(-3.000000000000001F, -10.0F, -1.4999999999999982F, -0.22776546738526054F, 1.0471975511965976F, 0.0F));
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


		var fruit1 = partDefinition.addOrReplaceChild("fruit1", CubeListBuilder.create()
				.texOffs(80, 0).addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(7.317074020041375F, 18.93442199684759F, 4.495867469902567F, -2.9595548126067848F, -1.09275064467365F, 3.141592653589793F));
		var fruit2 = partDefinition.addOrReplaceChild("fruit2", CubeListBuilder.create()
				.texOffs(80, 5).addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(-4.9471169857299575F, 20.14350602214442F, 6.884119689159518F, -2.868448625652681F, 0.5918411493512769F, 3.141592653589793F));
		var fruit3 = partDefinition.addOrReplaceChild("fruit3", CubeListBuilder.create()
				.texOffs(80, 10).addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(-3.0550790773897885F, 21.737183197472437F, -7.178756458034324F, 0.182212373908208F, 0.36425021489121645F, 0.0F));
		var fruit4 = partDefinition.addOrReplaceChild("fruit4", CubeListBuilder.create()
				.texOffs(80, 15).addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(-1.928125790107764F, 13.575571611437608F, 8.042089363476917F, -2.7317893452215247F, 0.18203784098300893F, 3.141592653589793F));
		var fruit5 = partDefinition.addOrReplaceChild("fruit5", CubeListBuilder.create()
				.texOffs(80, 20).addBox(-1.0F, 0.0F, -0.9F, 2, 2, 2),
			PartPose.offsetAndRotation(5.741657442714528F, 9.325910613055095F, 5.8700019458238115F, -2.913827186204533F, -0.6829473363053811F, 3.141592653589793F));
		var fruit6 = partDefinition.addOrReplaceChild("fruit6", CubeListBuilder.create()
				.texOffs(80, 25).addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(-8.59147759539003F, 16.834348626269843F, 2.162010931109819F, -2.868448625652681F, 1.3658946726107624F, 3.141592653589793F));


		return LayerDefinition.create(definition, 128, 64);
	}

	public static LayerDefinition makeAspectModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var fruit1aspect = partDefinition.addOrReplaceChild("fruit1aspect", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1),
			PartPose.offsetAndRotation(7.16F, 19.92F, 4.41F, -2.9595548126067843F, -1.0927506446736497F, 3.141592653589793F));
		var fruit2aspect = partDefinition.addOrReplaceChild("fruit2aspect", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5F, -0.6F, -0.5F, 1, 1, 1),
			PartPose.offsetAndRotation(-4.796620457114321F, 21.106433545412088F, 6.66024164242818F, -2.868448625652681F, 0.5918411493512769F, 3.141592653589793F));
		var fruit3aspect = partDefinition.addOrReplaceChild("fruit3aspect", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5F, -0.5F, -0.6F, 1, 1, 1),
			PartPose.offsetAndRotation(-2.9905247411599842F, 22.720628402467767F, -7.009439399101613F, 0.19904780787443266F, 0.3556171785337475F, 0.047790710573048026F));
		var fruit4aspect = partDefinition.addOrReplaceChild("fruit4aspect", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1),
			PartPose.offsetAndRotation(-1.8559965604071729F, 14.492770819632247F, 7.650243747564328F, -2.731789345221525F, 0.182037840983009F, 3.141592653589793F));
		var fruit5aspect = partDefinition.addOrReplaceChild("fruit5aspect", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1),
			PartPose.offsetAndRotation(5.613408227357616F, 10.20266666132796F, 5.712360039665306F, -2.913827186204533F, -0.6829473363053811F, 3.141592653589793F));
		var fruit6aspect = partDefinition.addOrReplaceChild("fruit6aspect", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1),
			PartPose.offsetAndRotation(-8.327360473818414F, 17.79727614953751F, 2.107122580557809F, -2.868448625652681F, 1.3658946726107624F, 3.141592653589793F));

		return LayerDefinition.create(definition, 128, 64);
	}
}
