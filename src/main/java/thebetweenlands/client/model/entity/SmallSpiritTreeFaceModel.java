package thebetweenlands.client.model.entity;

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

public class SmallSpiritTreeFaceModel extends Model {

	private final ModelPart root;

	public SmallSpiritTreeFaceModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition createFace2() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
			.texOffs(0, 0)
			.addBox(-5.0F, -4.0F, -2.0F, 10, 8, 2),
			PartPose.offset(0.0F, -7.5F, 8.0F));

		head.addOrReplaceChild("chin", CubeListBuilder.create()
			.texOffs(0, 11)
			.addBox(-3.0F, 0.0F, 0.0F, 6, 4, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, 0.091106186954104F, 0.0F, 0.0F));

		var nose = head.addOrReplaceChild("nose", CubeListBuilder.create()
			.texOffs(0, 18)
			.addBox(-1.5F, -3.0F, -2.0F, 3, 3, 2),
			PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, 0.045553093477052F, 0.0F, 0.0F));

		nose.addOrReplaceChild("nose2", CubeListBuilder.create()
				.texOffs(0, 24)
				.addBox(-1.5F, -6.0F, 0.0F, 3, 6, 3),
			PartPose.offsetAndRotation(0.0F, -3.0F, -2.0F, -0.18203784098300857F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
