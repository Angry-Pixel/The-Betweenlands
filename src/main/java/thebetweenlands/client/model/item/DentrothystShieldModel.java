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

public class DentrothystShieldModel extends Model {

	private final ModelPart root;

	public DentrothystShieldModel(ModelPart root) {
		super(RenderType::entityTranslucent);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var handle = partDefinition.addOrReplaceChild("handle", CubeListBuilder.create()
				.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 6),
			PartPose.ZERO);

		var main = handle.addOrReplaceChild("main", CubeListBuilder.create()
			.texOffs(0, 13)
			.addBox(-6.0F, -6.0F, -2.0F, 11, 12, 2),
			PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 0.0F, 0.0F, -0.045553093477052F));

		main.addOrReplaceChild("plate_1", CubeListBuilder.create()
				.texOffs(0, 28)
				.addBox(-4.0F, 0.0F, 0.0F, 9, 3, 2),
			PartPose.offsetAndRotation(0.0F, 6.0F, -2.01F, 0.136659280431156F, 0.0F, 0.0F));

		main.addOrReplaceChild("plate_2", CubeListBuilder.create()
				.texOffs(0, 34)
				.addBox(-4.0F, -2.0F, 0.0F, 8, 2, 2),
			PartPose.offsetAndRotation(0.0F, -6.0F, -2.01F, -0.136659280431156F, 0.0F, 0.0F));

		var beam1 = main.addOrReplaceChild("beam_1", CubeListBuilder.create()
			.texOffs(27, 0)
			.addBox(0.0F, -5.0F, -1.5F, 3, 11, 3),
			PartPose.offsetAndRotation(3.5F, 0.0F, -1.0F, 0.0F, 0.0F, 0.27314402793711257F));

		var beam2 = beam1.addOrReplaceChild("beam_2", CubeListBuilder.create()
				.texOffs(27, 15)
				.addBox(-3.0F, -6.0F, -1.49F, 3, 6, 3),
			PartPose.offsetAndRotation(3.0F, -5.0F, 0.0F, 0.0F, 0.0F, -0.7285004297824331F));

		var beam3 = beam2.addOrReplaceChild("beam_3", CubeListBuilder.create()
				.texOffs(27, 25)
				.addBox(-3.0F, -7.0F, -1.48F, 3, 7, 3),
			PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.9105382707654417F));

		beam3.addOrReplaceChild("beam_4", CubeListBuilder.create()
				.texOffs(27, 36)
				.addBox(0.0F, -3.0F, -1.47F, 3, 3, 3),
			PartPose.offsetAndRotation(-3.0F, -7.0F, 0.0F, 0.0F, 0.0F, 0.27314402793711257F));

		var beam5 = beam1.addOrReplaceChild("beam_5", CubeListBuilder.create()
				.texOffs(40, 0)
				.addBox(0.0F, 0.0F, -1.51F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, -0.5918411493512771F));

		beam5.addOrReplaceChild("beam_6", CubeListBuilder.create()
				.texOffs(40, 7)
				.addBox(0.0F, 0.0F, -1.52F, 3, 3, 3),
			PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.5918411493512771F));

		var leaf1 = beam1.addOrReplaceChild("leaf_1", CubeListBuilder.create()
				.texOffs(53, -4)
				.addBox(0.0F, -2.0F, -2.5F, 0, 2, 4),
			PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5009094953223726F));

		var leaf2 = leaf1.addOrReplaceChild("leaf_2", CubeListBuilder.create()
				.texOffs(53, -1)
				.addBox(0.0F, -2.0F, -2.5F, 0, 2, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.9105382707654417F));

		var leaf3 = leaf2.addOrReplaceChild("leaf_3", CubeListBuilder.create()
				.texOffs(53, 2)
				.addBox(0.0F, -2.0F, -2.5F, 0, 2, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.7285004297824331F));

		leaf3.addOrReplaceChild("leaf_4", CubeListBuilder.create()
				.texOffs(53, 5)
				.addBox(0.0F, -2.0F, -2.5F, 0, 2, 4),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -1.1383037381507017F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
