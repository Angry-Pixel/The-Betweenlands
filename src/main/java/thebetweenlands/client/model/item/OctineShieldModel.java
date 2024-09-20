package thebetweenlands.client.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class OctineShieldModel extends Model {

	private final ModelPart root;

	public OctineShieldModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var handle = partDefinition.addOrReplaceChild("handle_1", CubeListBuilder.create()
				.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6),
			PartPose.offset(-3.0F, 0.0F, 0.0F));

		handle.addOrReplaceChild("handle_2", CubeListBuilder.create()
				.texOffs(17, 0)
				.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6),
			PartPose.offset(6.0F, 0.0F, 0.0F));

		var main = handle.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(-7.0F, -4.0F, -2.0F, 14, 8, 2),
			PartPose.offset(3.0F, 0.0F, -1.0F));

		var plate1 = main.addOrReplaceChild("plate_1", CubeListBuilder.create()
				.texOffs(0, 24)
				.addBox(-6.0F, -5.0F, 0.0F, 12, 5, 2),
			PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.091106186954104F, 0.0F, 0.0F));

		var piece1 = plate1.addOrReplaceChild("piece_1", CubeListBuilder.create()
			.texOffs(34, 0)
			.addBox(-7.0F, -2.0F, 0.0F, 14, 2, 3),
			PartPose.offset(0.0F, -5.0F, 0.0F));

		piece1.addOrReplaceChild("rim_1", CubeListBuilder.create()
			.texOffs(34, 12)
			.addBox(-7.0F, -2.0F, -2.0F, 14, 2, 2, new CubeDeformation(0.001F)),
			PartPose.rotation(-0.18203784098300857F, 0.0F, 0.0F));

		var plate2 = main.addOrReplaceChild("plate_2", CubeListBuilder.create()
				.texOffs(0, 32)
				.addBox(-6.0F, 0.0F, 0.0F, 12, 5, 2),
			PartPose.offsetAndRotation(0.0F, 4.0F, -2.0F, 0.091106186954104F, 0.0F, 0.0F));

		var piece2 = plate2.addOrReplaceChild("piece_2", CubeListBuilder.create()
				.texOffs(34, 6)
				.addBox(-7.0F, 0.0F, 0.0F, 14, 2, 3),
			PartPose.offset(0.0F, 5.0F, 0.0F));

		piece2.addOrReplaceChild("rim_2", CubeListBuilder.create()
				.texOffs(34, 17)
				.addBox(-7.0F, 0.0F, -2.0F, 14, 2, 2, new CubeDeformation(0.001F)),
			PartPose.rotation(0.18203784098300857F, 0.0F, 0.0F));

		main.addOrReplaceChild("bump", CubeListBuilder.create()
			.texOffs(52, 22)
			.addBox(-2.0F, -2.0F, -1.0F, 4, 4, 1),
			PartPose.offset(0.0F, 0.0F, -2.0F));

		main.addOrReplaceChild("rim_3", CubeListBuilder.create()
				.texOffs(34, 22)
				.addBox(0.0F, -4.0F, -2.0F, 2, 8, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(5.0F, 0.0F, -2.0F, 0.0F, -0.18203784098300857F, 0.0F));

		main.addOrReplaceChild("rim_4", CubeListBuilder.create()
				.texOffs(43, 22)
				.addBox(-2.0F, -4.0F, -2.0F, 2, 8, 2, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(-5.0F, 0.0F, -2.0F, 0.0F, 0.18203784098300857F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
