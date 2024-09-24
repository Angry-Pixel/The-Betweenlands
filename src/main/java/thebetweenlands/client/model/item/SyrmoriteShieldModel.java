package thebetweenlands.client.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class SyrmoriteShieldModel extends Model {

	private final ModelPart root;

	public SyrmoriteShieldModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var handle = partDefinition.addOrReplaceChild("handle_1", CubeListBuilder.create()
				.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 7),
			PartPose.offset(-3.0F, 0.0F, 0.0F));

		handle.addOrReplaceChild("handle_2", CubeListBuilder.create()
				.texOffs(19, 0)
				.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 7),
			PartPose.offset(6.0F, 0.0F, 0.0F));

		var main = handle.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 14)
				.addBox(-4.0F, 0.0F, -2.0F, 8, 14, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, -2.0F, 0.045553093477052F, 0.0F, 0.0F));

		main.addOrReplaceChild("rim_plate_1", CubeListBuilder.create()
			.texOffs(0, 31)
			.addBox(0.0F, 0.0F, 0.0F, 3, 15, 2),
			PartPose.offsetAndRotation(4.0F, 0.0F, -2.0F, 0.0F, -0.27314402793711257F, 0.0F));

		main.addOrReplaceChild("rim_plate_2", CubeListBuilder.create()
				.texOffs(11, 31)
				.addBox(-3.0F, 0.0F, 0.0F, 3, 15, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -2.0F, 0.0F, 0.27314402793711257F, 0.0F));

		var plate = main.addOrReplaceChild("plate", CubeListBuilder.create()
				.texOffs(21, 14)
				.addBox(-4.0F, -14.0F, -2.0F, 8, 14, 2),
			PartPose.rotation(-0.091106186954104F, 0.0F, 0.0F));

		plate.addOrReplaceChild("rim_plate_3", CubeListBuilder.create()
				.texOffs(22, 31)
				.addBox(0.0F, -15.0F, 0.0F, 3, 15, 2),
			PartPose.offsetAndRotation(4.0F, 0.0F, -2.0F, 0.0F, -0.27314402793711257F, 0.0F));

		plate.addOrReplaceChild("rim_plate_4", CubeListBuilder.create()
				.texOffs(33, 31)
				.addBox(-3.0F, -15.0F, 0.0F, 3, 15, 2),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -2.0F, 0.0F, 0.27314402793711257F, 0.0F));

		var mid = main.addOrReplaceChild("mid_rim_1", CubeListBuilder.create()
			.texOffs(38, 0)
			.addBox(-4.0F, -1.0F, -1.0F, 8, 2, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.045553093477052F, 0.0F, 0.0F));

		mid.addOrReplaceChild("mid_rim_2", CubeListBuilder.create()
				.texOffs(38, 5)
				.addBox(0.0F, -1.0F, 0.0F, 4, 2, 4),
			PartPose.offsetAndRotation(4.0F, 0.0F, -1.0F, 0.0F, -0.31869712141416456F, 0.0F));

		mid.addOrReplaceChild("mid_rim_3", CubeListBuilder.create()
				.texOffs(55, 5)
				.addBox(-4.0F, -1.0F, 0.0F, 4, 2, 4),
			PartPose.offsetAndRotation(-4.0F, 0.0F, -1.0F, 0.0F, 0.31869712141416456F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
