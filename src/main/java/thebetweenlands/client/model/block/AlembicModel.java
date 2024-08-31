package thebetweenlands.client.model.block;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class AlembicModel {

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var alembic = partDefinition.addOrReplaceChild("alembic", CubeListBuilder.create(), PartPose.ZERO);

		partDefinition.addOrReplaceChild("alembic_liquid", CubeListBuilder.create()
			.addBox(-2.5F, -4.5F, -2.5F, 5, 5, 5),
			PartPose.offsetAndRotation(-2.0F, -5.0F, 2.2F, 0.0F, 0.6829473363053812F, 0.0F));

		partDefinition.addOrReplaceChild("jar_liquid", CubeListBuilder.create()
				.texOffs(43, 6)
				.addBox(-1.5F, -4.5F, -1.5F, 3, 3, 3),
			PartPose.offsetAndRotation(4.8F, -0.01F, -3.8F, 0.0F, -0.6373942428283291F, 0.0F));

		var base = alembic.addOrReplaceChild("base", CubeListBuilder.create()
			.addBox(-3.0F, -6.0F, -3.0F, 6, 7, 6),
			PartPose.offsetAndRotation(-2.0F, -4.0F, 2.2F, 0.091106186954104F, 0.6829473363053812F, 0.091106186954104F));

		base.addOrReplaceChild("midpiece", CubeListBuilder.create()
			.texOffs(0, 13)
			.addBox(-2.0F, -1.0F, -2.0F, 4, 1, 4),
			PartPose.offset(0.0F, -6.0F, 0.0F));

		base.addOrReplaceChild("top_1", CubeListBuilder.create()
				.texOffs(0, 19)
				.addBox(-2.5F, -2.0F, -2.5F, 5, 2, 5),
			PartPose.offset(0.0F, -7.0F, 0.0F));

		var top = base.addOrReplaceChild("top_2", CubeListBuilder.create()
				.texOffs(0, 27)
				.addBox(0.0F, 0.0F, -2.0F, 4, 1, 4),
			PartPose.offsetAndRotation(-2.5F, -9.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));

		var pipe1 = top.addOrReplaceChild("pipe_1", CubeListBuilder.create()
				.texOffs(25, 0)
				.addBox(0.0F, 0.0F, -0.5F, 3, 1, 1),
			PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.36425021489121656F));

		var pipe2 = pipe1.addOrReplaceChild("pipe_2", CubeListBuilder.create()
				.texOffs(25, 3)
				.addBox(0.0F, 0.0F, -0.49F, 4, 1, 1),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5462880558742251F));

		pipe2.addOrReplaceChild("pipe_3", CubeListBuilder.create()
				.texOffs(25, 6)
				.addBox(0.0F, 0.0F, -0.49F, 4, 1, 1),
			PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5462880558742251F));

		var jar = alembic.addOrReplaceChild("jar", CubeListBuilder.create()
				.texOffs(43, 6)
				.addBox(-2.0F, -4.0F, -2.0F, 4, 4, 4),
			PartPose.offsetAndRotation(4.8F, -0.01F, -3.8F, 0.0F, -0.6373942428283291F, 0.0F));

		jar.addOrReplaceChild("top_1", CubeListBuilder.create()
				.texOffs(55, 2)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 1, 2),
			PartPose.offset(0.0F, -4.0F, 0.0F));

		jar.addOrReplaceChild("top_2", CubeListBuilder.create()
				.texOffs(51, 15)
				.addBox(-1.5F, -1.0F, -1.5F, 3, 1, 3),
			PartPose.offset(0.0F, -5.0F, 0.0F));

		var stand = alembic.addOrReplaceChild("stand", CubeListBuilder.create()
				.texOffs(18, 9)
				.addBox(-4.0F, 0.0F, -4.0F, 8, 2, 8),
			PartPose.offsetAndRotation(-2.0F, -7.0F, 2.0F, 0.0F, 0.6829473363053812F, 0.0F));

		stand.addOrReplaceChild("leg_1", CubeListBuilder.create()
				.texOffs(21, 20)
				.addBox(-2.1F, 0.0F, 0.1F, 2, 6, 2),
			PartPose.offsetAndRotation(4.0F, 1.0F, -4.0F, -0.091106186954104F, 0.0F, -0.091106186954104F));

		stand.addOrReplaceChild("leg_2", CubeListBuilder.create()
				.texOffs(30, 20)
				.addBox(-2.1F, 0.0F, -2.1F, 2, 6, 2),
			PartPose.offsetAndRotation(4.0F, 1.0F, 4.0F, 0.091106186954104F, 0.0F, -0.091106186954104F));

		stand.addOrReplaceChild("leg_3", CubeListBuilder.create()
				.texOffs(39, 20)
				.addBox(0.1F, 0.0F, -2.1F, 2, 6, 2),
			PartPose.offsetAndRotation(-4.0F, 1.0F, 4.0F, 0.091106186954104F, 0.0F, 0.091106186954104F));

		stand.addOrReplaceChild("leg_4", CubeListBuilder.create()
				.texOffs(48, 20)
				.addBox(0.1F, 0.0F, 0.1F, 2, 6, 2),
			PartPose.offsetAndRotation(-4.0F, 1.0F, -4.0F, -0.091106186954104F, 0.0F, 0.091106186954104F));

		stand.addOrReplaceChild("firebowl", CubeListBuilder.create()
				.texOffs(36, 0)
				.addBox(-2.0F, -1.01F, -2.0F, 4, 1, 4),
			PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.0F, 0.22759093446006054F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}
}
