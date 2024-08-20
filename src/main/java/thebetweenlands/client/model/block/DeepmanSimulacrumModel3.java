package thebetweenlands.client.model.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class DeepmanSimulacrumModel3 extends Model {

	private ModelPart root;

	public DeepmanSimulacrumModel3(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(0.0F, -10.0F, 0.0F, 6, 10, 6),
			PartPose.offset(-4.5F, -24.0F, -3.0F));

		var shoulders = body.addOrReplaceChild("shoulders", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-3.5F, 0.0F, 0.0F, 7, 3, 3),
			PartPose.offsetAndRotation(3.0F, -6.7F, 3.0F, -0.8651597102135892F, 0.0F, 0.0F));

		shoulders.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(0, 24)
				.addBox(-3.01F, 0.0F, -3.0F, 3, 3, 3),
			PartPose.offsetAndRotation(-0.5F, 3.0F, 3.0F, -0.27314402793711257F, 0.0F, 0.0F));

		shoulders.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(13, 24)
				.addBox(-2.99F, 0.0F, -3.0F, 3, 3, 3),
			PartPose.offsetAndRotation(3.5F, 3.0F, 3.0F, -0.5009094953223726F, 0.0F, 0.0F));

		var scroll = partDefinition.addOrReplaceChild("scroll", CubeListBuilder.create()
				.texOffs(0, 32)
				.addBox(-1.0F, -2.0F, -3.4F, 2, 2, 7),
			PartPose.offsetAndRotation(3.5F, -24.0F, 0.0F, 0.0F, -0.18203784098300857F, -0.136659280431156F));

		scroll.addOrReplaceChild("scroll_stick", CubeListBuilder.create()
			.texOffs(0, 42)
			.addBox(-0.5F, -0.5F, -4.0F, 1, 1, 8),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.36425021489121656F));

		var paper = scroll.addOrReplaceChild("paper_1", CubeListBuilder.create()
				.texOffs(0, 52)
				.addBox(0.0F, 0.0F, -3.5F, 1, 0, 7),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.22759093446006054F));

		paper.addOrReplaceChild("paper_2", CubeListBuilder.create()
				.texOffs(-5, 52)
				.addBox(0.0F, 0.0F, -3.5F, 2, 0, 7),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5462880558742251F));


		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
