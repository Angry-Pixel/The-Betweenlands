package thebetweenlands.client.model.block.pot;

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

public class LootUrnModel1 extends Model {

	private final ModelPart root;

	public LootUrnModel1(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.addBox(-3.0F, -3.0F, -3.0F, 6, 3, 6),
			PartPose.ZERO);

		base.addOrReplaceChild("middle", CubeListBuilder.create()
			.texOffs(0, 10)
			.addBox(-4.0F, -7.0F, -4.0F, 8, 7, 8),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		var top = base.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(25, 0)
				.addBox(-3.0F, -2.0F, -3.0F, 6, 2, 6),
			PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, 0.0F, 0.091106186954104F, 0.0F));

		top.addOrReplaceChild("top_left", CubeListBuilder.create()
				.texOffs(25, 9)
				.addBox(-3.0F, -1.0F, 1.0F, 6, 1, 2),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		top.addOrReplaceChild("top_right", CubeListBuilder.create()
				.texOffs(25, 13)
				.addBox(-3.0F, -1.0F, -3.0F, 6, 1, 2),
			PartPose.offset(0.0F, -2.0F, 0.0F));


		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
