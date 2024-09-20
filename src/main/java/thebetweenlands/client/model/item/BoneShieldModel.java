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

public class BoneShieldModel extends Model {

	private final ModelPart root;

	public BoneShieldModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("handle", CubeListBuilder.create()
			.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6),
			PartPose.ZERO);

		var main = partDefinition.addOrReplaceChild("main", CubeListBuilder.create()
			.texOffs(0, 13)
			.addBox(-1.0F, -8.0F, -2.0F, 2, 14, 2),
			PartPose.offset(0.0F, -3.0F, -1.0F));

		var bone1 = main.addOrReplaceChild("bone_1", CubeListBuilder.create()
				.texOffs(9, 13)
				.addBox(-2.0F, -14.0F, -2.0F, 2, 14, 2),
			PartPose.offsetAndRotation(-1.0F, 6.0F, 0.0F, -0.045553093477052F, 0.091106186954104F, 0.0F));

		var bone2 = bone1.addOrReplaceChild("bone_2", CubeListBuilder.create()
				.texOffs(18, 13)
				.addBox(-2.0F, -12.0F, -2.0F, 2, 12, 2),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.045727626402251434F, 0.091106186954104F, 0.0F));

		bone2.addOrReplaceChild("bone_3", CubeListBuilder.create()
				.texOffs(27, 13)
				.addBox(-2.0F, -14.0F, -2.0F, 2, 14, 2),
			PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		var bone4 = main.addOrReplaceChild("bone_4", CubeListBuilder.create()
				.texOffs(36, 13)
				.addBox(0.0F, -13.0F, -2.0F, 2, 13, 2),
			PartPose.offsetAndRotation(1.0F, 6.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		var bone5 = bone4.addOrReplaceChild("bone_5", CubeListBuilder.create()
				.texOffs(45, 13)
				.addBox(0.0F, -14.0F, -2.0F, 2, 14, 2),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.0F));

		bone5.addOrReplaceChild("bone_6", CubeListBuilder.create()
				.texOffs(54, 13)
				.addBox(0.0F, -14.0F, -2.0F, 2, 14, 2),
			PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.091106186954104F, 0.026354471705114374F));

		var rope1 = main.addOrReplaceChild("rope_1", CubeListBuilder.create()
				.texOffs(17, 0)
				.addBox(-3.0F, 0.0F, -1.5F, 6, 2, 3),
			PartPose.offset(0.0F, 5.5F, -1.0F));

		rope1.addOrReplaceChild("rope_2", CubeListBuilder.create()
				.texOffs(17, 6)
				.addBox(0.0F, 0.0F, 0.0F, 5, 2, 3),
			PartPose.offsetAndRotation(3.0F, 0.0F, -1.5F, 0.0F, -0.22759093446006054F, 0.0F));

		rope1.addOrReplaceChild("rope_3", CubeListBuilder.create()
				.texOffs(36, 0)
				.addBox(-5.0F, 0.0F, 0.0F, 5, 2, 3),
			PartPose.offsetAndRotation(-3.0F, 0.0F, -1.5F, 0.0F, 0.22759093446006054F, 0.0F));

		var rope4 = main.addOrReplaceChild("rope_4", CubeListBuilder.create()
				.texOffs(53, 0)
				.addBox(-3.0F, 0.0F, -1.5F, 6, 2, 3),
			PartPose.offset(0.0F, -1.5F, -1.0F));

		rope4.addOrReplaceChild("rope_5", CubeListBuilder.create()
				.texOffs(53, 6)
				.addBox(0.0F, 0.0F, 0.0F, 5, 2, 3),
			PartPose.offsetAndRotation(3.0F, 0.0F, -1.5F, 0.0F, -0.22759093446006054F, 0.0F));

		rope4.addOrReplaceChild("rope_6", CubeListBuilder.create()
				.texOffs(72, 0)
				.addBox(-5.0F, 0.0F, 0.0F, 5, 2, 3),
			PartPose.offsetAndRotation(-3.0F, 0.0F, -1.5F, 0.0F, 0.22759093446006054F, 0.0F));

		var blade1 = main.addOrReplaceChild("shoulder_blade_1", CubeListBuilder.create()
				.texOffs(0, 30)
				.addBox(0.0F, 0.0F, -2.0F, 7, 8, 2),
			PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.045553093477052F, -0.136659280431156F, 0.0F));

		blade1.addOrReplaceChild("shoulder_blade_2", CubeListBuilder.create()
				.texOffs(19, 30)
				.addBox(0.0F, 0.0F, -2.0F, 5, 2, 2),
			PartPose.offset(0.0F, 8.0F, 0.0F));

		var blade3 = main.addOrReplaceChild("shoulder_blade_3", CubeListBuilder.create()
				.texOffs(0, 41)
				.addBox(-7.0F, 0.0F, -2.0F, 7, 8, 2),
			PartPose.offsetAndRotation(0.0F, 7.0F, 0.0F, 0.045553093477052F, 0.136659280431156F, 0.0F));

		blade3.addOrReplaceChild("shoulder_blade_4", CubeListBuilder.create()
				.texOffs(19, 41)
				.addBox(-5.0F, 0.0F, -2.0F, 5, 2, 2),
			PartPose.offset(0.0F, 8.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
