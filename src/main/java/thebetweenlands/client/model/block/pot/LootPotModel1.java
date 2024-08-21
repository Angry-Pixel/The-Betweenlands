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

public class LootPotModel1 extends Model {

	private final ModelPart root;

	public LootPotModel1(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
			.addBox(-4.0F, -4.0F, -4.0F, 8, 4, 8),
			PartPose.ZERO);

		base.addOrReplaceChild("side1", CubeListBuilder.create()
			.texOffs(0, 13)
			.addBox(-2.0F, -10.0F, -6.0F, 2, 10, 12),
			PartPose.offset(-4.0F, -2.0F, 0.0F));

		base.addOrReplaceChild("side2", CubeListBuilder.create()
				.texOffs(29, 13)
				.addBox(-4.0F, -10.0F, -2.0F, 8, 10, 2),
			PartPose.offset(0.0F, -2.0F, -4.0F));

		base.addOrReplaceChild("side3", CubeListBuilder.create()
				.texOffs(0, 36)
				.addBox(0.0F, -10.0F, -6.0F, 2, 10, 12),
			PartPose.offset(4.0F, -2.0F, 0.0F));

		base.addOrReplaceChild("side4", CubeListBuilder.create()
				.texOffs(29, 26)
				.addBox(-4.0F, -10.0F, 0.0F, 8, 10, 2),
			PartPose.offset(0.0F, -2.0F, 4.0F));

		var lid = base.addOrReplaceChild("lid", CubeListBuilder.create()
				.texOffs(33, 0)
				.addBox(-5.0F, -2.0F, -5.0F, 10, 2, 10),
			PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.045553093477052F, 0.0F));

		lid.addOrReplaceChild("lid_top", CubeListBuilder.create()
				.texOffs(64, 0)
				.addBox(-2.0F, -2.0F, -2.0F, 4, 2, 4),
			PartPose.offset(0.0F, -2.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
