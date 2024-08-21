package thebetweenlands.client.model.block;

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

public class CenserModel extends Model {

	private final ModelPart root;

	public CenserModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.addBox(-6.0F, -2.0F, -6.0F, 12, 2, 12),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		var leftFrontLeg = base.addOrReplaceChild("left_front_leg_1", CubeListBuilder.create()
			.texOffs(0, 53)
			.addBox(-3.0F, -1.0F, -2.99F, 3, 2, 3),
			PartPose.offsetAndRotation(6.0F, 0.0F, -3.0F, 0.0F, 0.0F, -0.18203784098300857F));

		leftFrontLeg.addOrReplaceChild("left_front_leg_2", CubeListBuilder.create()
				.texOffs(13, 43)
				.addBox(-5.0F, 0.0F, -3.0F, 5, 2, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));

		var rightFrontLeg = base.addOrReplaceChild("right_front_leg_1", CubeListBuilder.create()
				.texOffs(0, 48)
				.addBox(0.0F, -1.0F, -2.99F, 3, 2, 3),
			PartPose.offsetAndRotation(-6.0F, 0.0F, -3.0F, 0.0F, 0.0F, 0.18203784098300857F));

		rightFrontLeg.addOrReplaceChild("right_front_leg_2", CubeListBuilder.create()
				.texOffs(13, 48)
				.addBox(0.0F, 0.0F, -3.0F, 5, 2, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		var leftBackLeg = base.addOrReplaceChild("left_back_leg_1", CubeListBuilder.create()
				.texOffs(0, 53)
				.addBox(-3.0F, -1.0F, -0.01F, 3, 2, 3),
			PartPose.offsetAndRotation(6.0F, 0.0F, 3.0F, 0.0F, 0.0F, -0.18203784098300857F));

		leftBackLeg.addOrReplaceChild("left_back_leg_2", CubeListBuilder.create()
				.texOffs(13, 53)
				.addBox(-5.0F, 0.0F, 0.0F, 5, 2, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));

		var rightBackLeg = base.addOrReplaceChild("right_back_leg_1", CubeListBuilder.create()
				.texOffs(0, 58)
				.addBox(0.0F, -1.0F, -0.01F, 3, 2, 3),
			PartPose.offsetAndRotation(-6.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.18203784098300857F));

		rightBackLeg.addOrReplaceChild("right_back_leg_2", CubeListBuilder.create()
				.texOffs(13, 58)
				.addBox(0.0F, 0.0F, 0.0F, 5, 2, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		var midPiece = base.addOrReplaceChild("mid_piece", CubeListBuilder.create()
				.texOffs(0, 15)
				.addBox(-5.0F, -6.0F, -5.0F, 10, 6, 10),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		var topPiece = midPiece.addOrReplaceChild("top_piece", CubeListBuilder.create()
				.texOffs(0, 32)
				.addBox(-4.0F, -2.0F, -4.0F, 8, 2, 8),
			PartPose.offset(0.0F, -6.0F, 0.0F));

		var ornamentBase = topPiece.addOrReplaceChild("ornament_1", CubeListBuilder.create()
				.texOffs(49, 0)
				.addBox(-2.0F, -1.0F, -1.5F, 2, 3, 3),
			PartPose.offsetAndRotation(3.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.7285004297824331F));

		var ornamentMid = ornamentBase.addOrReplaceChild("ornament_2", CubeListBuilder.create()
				.texOffs(49, 7)
				.addBox(-2.0F, 0.0F, -1.5F, 2, 3, 3),
			PartPose.offsetAndRotation(-2.0F, -1.0F, -0.01F, 0.0F, 0.0F, -0.36425021489121656F));

		var ornamentTop = ornamentMid.addOrReplaceChild("ornament_3", CubeListBuilder.create()
				.texOffs(49, 14)
				.addBox(-2.0F, 0.0F, -1.5F, 2, 3, 3),
			PartPose.offsetAndRotation(-2.0F, 0.0F, -0.01F, 0.0F, 0.0F, -0.36425021489121656F));

		ornamentTop.addOrReplaceChild("ornament_eyes", CubeListBuilder.create()
			.texOffs(49, 31)
			.addBox(0.0F, 0.0F, -2.0F, 1, 1, 4),
			PartPose.offset(-1.5F, 0.5F, 0.0F));

		ornamentTop.addOrReplaceChild("ornament_upper_mouth", CubeListBuilder.create()
				.texOffs(49, 21)
				.addBox(-2.0F, -1.0F, -1.5F, 2, 1, 3),
			PartPose.offset(-2.0F, 1.0F, 0.0F));

		ornamentTop.addOrReplaceChild("ornament_lower_mouth", CubeListBuilder.create()
				.texOffs(49, 26)
				.addBox(-2.0F, -1.0F, -1.5F, 2, 1, 3),
			PartPose.offsetAndRotation(-2.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.045553093477052F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
