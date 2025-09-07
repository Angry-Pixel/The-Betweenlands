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

public class SwordEnergyModel extends Model {

	private final ModelPart root;

	public SwordEnergyModel(ModelPart root) {
		super(RenderType::entityTranslucentEmissive);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("jewel_1", CubeListBuilder.create()
			.texOffs(0, 7).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
			PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0F, 0.7853982F, 0.0F));

		partDefinition.addOrReplaceChild("jewel_2", CubeListBuilder.create()
				.texOffs(0, 7).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
			PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.7853982F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("jewel_3", CubeListBuilder.create()
				.texOffs(0, 7).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F),
			PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.7853982F));

		return LayerDefinition.create(definition, 32, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
