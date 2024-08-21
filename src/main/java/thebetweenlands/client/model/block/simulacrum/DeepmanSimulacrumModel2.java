package thebetweenlands.client.model.block.simulacrum;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class DeepmanSimulacrumModel2 extends Model {

	private ModelPart root;

	public DeepmanSimulacrumModel2(ModelPart root) {
		super(RenderType::entityCutout);
		this.root = root;
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.5F, -8.0F, -3.5F, 7, 8, 7),
			PartPose.ZERO);

		var bod_top = body.addOrReplaceChild("body_top", CubeListBuilder.create()
				.texOffs(0, 16)
				.addBox(-3.5F, -8.0F, 0.0F, 7, 8, 7),
			PartPose.offset(0.0F, -8.0F, -3.5F));

		var arms = bod_top.addOrReplaceChild("arms_1", CubeListBuilder.create()
				.texOffs(0, 32)
				.addBox(-4.0F, 0.0F, 0.0F, 8, 4, 3),
			PartPose.offsetAndRotation(0.0F, -2.0F, 4.0F, -0.7285004297824331F, 0.0F, 0.0F));

		arms.addOrReplaceChild("arms_2", CubeListBuilder.create()
				.texOffs(0, 40)
				.addBox(-4.01F, 0.0F, -3.0F, 8, 4, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 3.0F, -0.36425021489121656F, 0.0F, 0.0F));

		var rope1 = bod_top.addOrReplaceChild("rope_1", CubeListBuilder.create()
			.texOffs(40, 0)
			.addBox(-0.5F, -0.5F, -4.0F, 3, 1, 8),
			PartPose.offsetAndRotation(-3.7F, 0.9F, 3.5F, 0.0F, 0.0F, -0.18203784098300857F));

		var rope2 = rope1.addOrReplaceChild("rope_2", CubeListBuilder.create()
			.texOffs(40, 10)
			.addBox(0.0F, -1.0F, -4.0F, 3, 1, 8),
			PartPose.offsetAndRotation(2.5F, 0.5F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		var rope3 = rope2.addOrReplaceChild("rope_3", CubeListBuilder.create()
			.texOffs(40, 20)
			.addBox(0.0F, -1.0F, -4.0F, 3, 1, 8),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		rope1.addOrReplaceChild("paper_1", CubeListBuilder.create()
			.texOffs(30, 0)
			.addBox(0.0F, 0.0F, 0.0F, 2, 3, 0),
			PartPose.offsetAndRotation(1.5F, 0.0F, 3.7F, 0.18203784098300857F, -0.091106186954104F, 0.5918411493512771F));

		rope1.addOrReplaceChild("paper_2", CubeListBuilder.create()
				.texOffs(30, 1)
				.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(-0.3F, 0.0F, 1.2F, -0.4553564018453205F, 0.27314402793711257F, 0.5009094953223726F));

		rope1.addOrReplaceChild("paper_3", CubeListBuilder.create()
				.texOffs(30, 5)
				.addBox(0.0F, 0.0F, 0.0F, 0, 3, 2),
			PartPose.offsetAndRotation(-0.4F, 0.0F, -2.5F, -0.5462880558742251F, -0.31869712141416456F, 0.36425021489121656F));

		rope1.addOrReplaceChild("paper_4", CubeListBuilder.create()
				.texOffs(30, 12)
				.addBox(0.0F, 0.0F, 0.0F, 3, 2, 0),
			PartPose.offsetAndRotation(2.0F, 0.0F, -3.7F, -0.4553564018453205F, 0.4553564018453205F, 1.0016444577195458F));

		rope3.addOrReplaceChild("paper_5", CubeListBuilder.create()
				.texOffs(30, 16)
				.addBox(0.0F, 0.0F, 0.0F, 3, 2, 0),
			PartPose.offsetAndRotation(0.8F, -0.5F, -3.7F, -0.22759093446006054F, 0.31869712141416456F, 1.0927506446736497F));

		rope3.addOrReplaceChild("paper_6", CubeListBuilder.create()
				.texOffs(30, 17)
				.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3),
			PartPose.offsetAndRotation(2.7F, -0.5F, -2.5F, -0.9560913642424937F, 0.0F, 0.36425021489121656F));

		rope3.addOrReplaceChild("paper_7", CubeListBuilder.create()
				.texOffs(30, 21)
				.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3),
			PartPose.offsetAndRotation(2.7F, -0.5F, 2.0F, -0.9560913642424937F, -0.22759093446006054F, 0.27314402793711257F));

		rope3.addOrReplaceChild("paper_8", CubeListBuilder.create()
				.texOffs(30, 28)
				.addBox(0.0F, 0.0F, 0.0F, 2, 3, 0),
			PartPose.offsetAndRotation(0.5F, -0.5F, 3.7F, 0.31869712141416456F, 0.0F, 1.0471975511965976F));


		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
