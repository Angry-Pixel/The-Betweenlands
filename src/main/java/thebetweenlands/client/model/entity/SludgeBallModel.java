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
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class SludgeBallModel extends Model {

	private final ModelPart root;

	public SludgeBallModel(ModelPart root) {
		super(RenderType::entityTranslucent);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("slime_1", CubeListBuilder.create()
				.texOffs(40, 0)
				.addBox(-9.0F, -7.0F, -9.0F, 18, 14, 18),
			PartPose.offset(0.0F, 15.0F, 0.0F));

		partDefinition.addOrReplaceChild("slime_2", CubeListBuilder.create()
				.texOffs(40, 32)
				.addBox(-7.0F, -9.0F, -7.0F, 14, 2, 14),
			PartPose.offset(0.0F, 15.0F, 0.0F));

		partDefinition.addOrReplaceChild("slime_3", CubeListBuilder.create()
				.texOffs(40, 48)
				.addBox(-7.0F, 7.0F, -7.0F, 14, 2, 14),
			PartPose.offset(0.0F, 15.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
