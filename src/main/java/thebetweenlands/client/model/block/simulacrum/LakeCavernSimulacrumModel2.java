package thebetweenlands.client.model.block.simulacrum;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class LakeCavernSimulacrumModel2 extends Model {

	private ModelPart root;

	public LakeCavernSimulacrumModel2(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-5.0F, -2.0F, -2.0F, 10, 2, 4),
			PartPose.ZERO);

		var middle = base.addOrReplaceChild("middle", CubeListBuilder.create()
				.texOffs(0, 7)
				.addBox(-1.0F, -14.0F, -1.0F, 2, 14, 2),
			PartPose.offset(0.0F, -2.0F, -0.25F));

		var left = middle.addOrReplaceChild("left", CubeListBuilder.create()
				.texOffs(9, 7)
				.addBox(0.0F, -14.0F, 0.0F, 2, 14, 2),
			PartPose.offsetAndRotation(1.0F, 0.0F, -1.0F, 0.0F, -0.22759093446006054F, 0.0F));

		var right = middle.addOrReplaceChild("right", CubeListBuilder.create()
				.texOffs(18, 7)
				.addBox(-2.0F, -14.0F, 0.0F, 2, 14, 2),
			PartPose.offsetAndRotation(-1.0F, 0.0F, -1.0F, 0.0F, 0.22759093446006054F, 0.0F));

		left.addOrReplaceChild("top_left", CubeListBuilder.create()
				.texOffs(9, 24)
				.addBox(0.0F, 0.0F, 0.0F, 1, 4, 2, new CubeDeformation(0.01F)),
			PartPose.offset(2.0F, -14.0F, 0.0F));

		right.addOrReplaceChild("top_right", CubeListBuilder.create()
				.texOffs(18, 24)
				.addBox(-1.0F, 0.0F, 0.0F, 1, 4, 2, new CubeDeformation(0.00F)),
			PartPose.offset(-2.0F, -14.0F, 0.0F));


		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
