package thebetweenlands.client.model.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class DeepmanSimulacrumModel1 extends Model {

	private ModelPart root;

	public DeepmanSimulacrumModel1(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-4.5F, -7.0F, -4.5F, 9, 7, 9),
			PartPose.ZERO);

		var face = body.addOrReplaceChild("face", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-4.0F, -7.0F, -4.0F, 8, 7, 8),
			PartPose.offset(0.0F, -7.0F, 0.0F));

		var shoulders = face.addOrReplaceChild("shoulders", CubeListBuilder.create()
				.texOffs(0, 49)
				.addBox(-5.005F, -1.0F, -1.0F, 10, 1, 2),
			PartPose.offset(0.0F, -2.5F, 0.0F));

		var armsFront = shoulders.addOrReplaceChild("arms_front_1", CubeListBuilder.create()
				.texOffs(0, 33)
				.addBox(-5.01F, 0.0F, 0.0F, 10, 4, 3, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -1.0F, -1.0F, -0.6373942428283291F, 0.0F, 0.0F));

		armsFront.addOrReplaceChild("arms_front_2", CubeListBuilder.create()
				.texOffs(27, 33)
				.addBox(-5.0F, 0.0F, -3.0F, 10, 4, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, 3.0F, -0.5462880558742251F, 0.0F, 0.0F));

		var armsBack = shoulders.addOrReplaceChild("arms_back_1", CubeListBuilder.create()
			.texOffs(0, 41)
			.addBox(-5.0F, 0.0F, -3.0F, 10, 4, 3, new CubeDeformation(0.001F)),
			PartPose.offsetAndRotation(0.0F, -1.0F, 1.0F, 0.6373942428283291F, 0.0F, 0.0F));

		armsBack.addOrReplaceChild("arms_back_2", CubeListBuilder.create()
				.texOffs(27, 41)
				.addBox(-5.0F, 0.0F, 0.0F, 10, 4, 3),
			PartPose.offsetAndRotation(0.0F, 4.0F, -3.0F, 0.5462880558742251F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
