package thebetweenlands.client.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class ValoniteShieldModel extends Model {

	private final ModelPart root;

	public ValoniteShieldModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("handle", CubeListBuilder.create()
				.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 6),
			PartPose.ZERO);

		var main = partDefinition.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 16)
				.addBox(-3.0F, -3.0F, -2.0F, 6, 14, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.091106186954104F, 0.0F, 0.0F));

		var top = main.addOrReplaceChild("top", CubeListBuilder.create()
			.texOffs(19, 0)
			.addBox(-3.0F, -4.0F, -2.0F, 6, 4, 2),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.22759093446006054F, 0.0F, 0.0F));

		var topLeft = top.addOrReplaceChild("top_left", CubeListBuilder.create()
				.texOffs(36, 0)
				.addBox(0.0F, -2.0F, 0.0F, 3, 4, 2),
			PartPose.offsetAndRotation(3.0F, -2.0F, -2.0F, 0.0F, -0.136659280431156F, 0.0F));

		topLeft.addOrReplaceChild("left_horn", CubeListBuilder.create()
				.texOffs(47, 0)
				.addBox(0.0F, -3.0F, 0.0F, 3, 5, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F, 0.0F));

		var leftSide = main.addOrReplaceChild("left_side", CubeListBuilder.create()
				.texOffs(17, 16)
				.addBox(0.0F, 0.0F, 0.0F, 3, 15, 2),
			PartPose.offsetAndRotation(3.0F, -3.0F, -2.0F, 0.0F, -0.136659280431156F, 0.0F));

		leftSide.addOrReplaceChild("left_bottom", CubeListBuilder.create()
				.texOffs(28, 16)
				.addBox(0.0F, 0.0F, 0.0F, 3, 7, 2),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, -0.136659280431156F, 0.0F));

		var topright = top.addOrReplaceChild("top_right", CubeListBuilder.create()
				.texOffs(36, 8)
				.addBox(-3.0F, -2.0F, 0.0F, 3, 4, 2),
			PartPose.offsetAndRotation(-3.0F, -2.0F, -2.0F, 0.0F, 0.136659280431156F, 0.0F));

		topright.addOrReplaceChild("right_horn", CubeListBuilder.create()
				.texOffs(47, 8)
				.addBox(-3.0F, -3.0F, 0.0F, 3, 5, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F, 0.0F));

		var rightSide = main.addOrReplaceChild("right_side", CubeListBuilder.create()
				.texOffs(39, 16)
				.addBox(-3.0F, 0.0F, 0.0F, 3, 16, 2),
			PartPose.offsetAndRotation(-3.0F, -3.0F, -2.0F, 0.0F, 0.136659280431156F, 0.0F));

		rightSide.addOrReplaceChild("right_bottom", CubeListBuilder.create()
				.texOffs(50, 16)
				.addBox(-3.0F, 0.0F, 0.0F, 3, 9, 2),
			PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F, 0.0F));

		var mid = main.addOrReplaceChild("mid_piece_1", CubeListBuilder.create()
				.texOffs(0, 35)
				.addBox(-3.0F, 0.0F, -1.0F, 6, 5, 4, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.091106186954104F, 0.0F, 0.0F));

		mid.addOrReplaceChild("mid_piece_2", CubeListBuilder.create()
				.texOffs(21, 35)
				.addBox(0.0F, 0.0F, 0.0F, 7, 2, 4),
			PartPose.offsetAndRotation(3.0F, 0.0F, -1.0F, 0.0F, -0.31869712141416456F, 0.0F));

		mid.addOrReplaceChild("mid_piece_3", CubeListBuilder.create()
				.texOffs(21, 42)
				.addBox(-7.0F, 0.0F, 0.0F, 7, 2, 4),
			PartPose.offsetAndRotation(-3.0F, 0.0F, -1.0F, 0.0F, 0.31869712141416456F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
