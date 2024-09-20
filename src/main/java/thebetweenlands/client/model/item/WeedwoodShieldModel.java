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

public class WeedwoodShieldModel extends Model {

	private final ModelPart root;

	public WeedwoodShieldModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var handle = partDefinition.addOrReplaceChild("handle", CubeListBuilder.create()
				.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6),
			PartPose.ZERO);

		var main = handle.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(-6.0F, -4.0F, -2.0F, 12, 8, 2),
			PartPose.offset(0.0F, 2.0F, -1.0F));

		var plate = main.addOrReplaceChild("plate", CubeListBuilder.create()
				.texOffs(0, 24)
				.addBox(-6.0F, -6.0F, 0.0F, 12, 6, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.091106186954104F, 0.0F, 0.0F));

		var top = plate.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(29, 0)
				.addBox(-5.0F, -2.0F, 0.0F, 10, 2, 2),
			PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.091106186954104F, 0.0F, 0.0F));

		top.addOrReplaceChild("top_2", CubeListBuilder.create()
				.texOffs(29, 5)
				.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 2),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		top.addOrReplaceChild("top_3", CubeListBuilder.create()
				.texOffs(29, 9)
				.addBox(-2.0F, -1.0F, 0.0F, 4, 1, 2),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		var bottom = main.addOrReplaceChild("bottom", CubeListBuilder.create()
				.texOffs(29, 13)
				.addBox(0.0F, 0.0F, 0.0F, 3, 3, 2),
			PartPose.offset(-6.0F, 4.0F, -2.0F));

		bottom.addOrReplaceChild("bottom_2", CubeListBuilder.create()
				.texOffs(29, 19)
				.addBox(0.0F, 0.0F, 0.0F, 4, 4, 2),
			PartPose.offset(6.0F, 0.0F, 0.0F));

		bottom.addOrReplaceChild("bottom_3", CubeListBuilder.create()
				.texOffs(29, 26)
				.addBox(0.0F, 0.0F, 0.0F, 3, 2, 2),
			PartPose.offset(3.0F, 0.0F, 0.0F));

		var leaf1 = main.addOrReplaceChild("leaf_1", CubeListBuilder.create()
			.texOffs(42, 9)
			.addBox(-2.0F, 0.0F, 0.0F, 4, 2, 0),
			PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.36425021489121656F, 0.0F, 0.0F));

		var leaf2 = leaf1.addOrReplaceChild("leaf_2", CubeListBuilder.create()
				.texOffs(42, 12)
				.addBox(-3.0F, 0.0F, 0.0F, 6, 3, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.27314402793711257F, 0.0F, 0.0F));

		var leaf3 = leaf2.addOrReplaceChild("leaf_3", CubeListBuilder.create()
				.texOffs(42, 16)
				.addBox(-3.0F, 0.0F, 0.0F, 6, 3, 0),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.27314402793711257F, 0.0F, 0.0F));

		var leaf4 = leaf3.addOrReplaceChild("leaf_4", CubeListBuilder.create()
				.texOffs(42, 20)
				.addBox(-3.0F, 0.0F, 0.0F, 6, 2, 0),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, -0.5009094953223726F, 0.0F, 0.0F));

		leaf4.addOrReplaceChild("leaf_5", CubeListBuilder.create()
				.texOffs(42, 23)
				.addBox(-2.0F, 0.0F, 0.0F, 4, 2, 0),
			PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.8651597102135892F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
