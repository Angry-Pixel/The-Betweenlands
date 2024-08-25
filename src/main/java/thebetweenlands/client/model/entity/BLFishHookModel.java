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

public class BLFishHookModel extends Model {

	private final ModelPart root;

	public BLFishHookModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 4, 3),
			PartPose.offset(0.0F, 21.0F, 0.0F));

		base.addOrReplaceChild("tip", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-0.5F, -5.0F, -0.5F, 1, 5, 1),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		var connector = base.addOrReplaceChild("connector", CubeListBuilder.create()
				.texOffs(5, 17)
				.addBox(-0.5F, 0.0F, -0.5F, 1, 1, 1),
			PartPose.offset(0.0F, 2.0F, 0.0F));

		connector.addOrReplaceChild("hook", CubeListBuilder.create()
				.texOffs(5, 16)
				.addBox(0.0F, 0.0F, -2.0F, 0.001F, 4, 4),
			PartPose.offset(0.0F, 1.0F, 0.0F));

		base.addOrReplaceChild("baiter_left_1", CubeListBuilder.create()
				.texOffs(0, 5)
				.addBox(0.0F, 0.0F, -1.5F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(1.5F, 1.0F, 0.0F, 0.0F, 0.0F, -0.18203784098300857F));

		var baiterLeft = base.addOrReplaceChild("baiter_left_2", CubeListBuilder.create()
				.texOffs(0, 8)
				.addBox(0.0F, -2.0F, -1.5F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(1.5F, -1.0F, 0.0F, 0.0F, 0.0F, 0.36425021489121656F));

		baiterLeft.addOrReplaceChild("baiter_left_3", CubeListBuilder.create()
				.texOffs(0, 11)
				.addBox(0.0F, -2.0F, -1.5F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.36425021489121656F));

		base.addOrReplaceChild("baiter_right_1", CubeListBuilder.create()
				.texOffs(7, 5)
				.addBox(0.0F, 0.0F, -1.5F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(-1.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.18203784098300857F));

		var baiterRight = base.addOrReplaceChild("baiter_right_2", CubeListBuilder.create()
				.texOffs(7, 8)
				.addBox(0.0F, -2.0F, -1.5F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(-1.5F, -1.0F, 0.0F, 0.0F, 0.0F, -0.36425021489121656F));

		baiterRight.addOrReplaceChild("baiter_right_3", CubeListBuilder.create()
				.texOffs(7, 11)
				.addBox(0.0F, -2.0F, -1.5F, 0.001F, 2, 3),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.36425021489121656F));

		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
