package thebetweenlands.client.model.block;

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

public class LakeCavernSimulacrumModel1 extends Model {

	private ModelPart root;

	public LakeCavernSimulacrumModel1(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, -3.0F, -2.0F, 6, 3, 4),
			PartPose.offset(0.0F, -24.0F, 0.0F));

		var main = base.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 8)
				.addBox(-2.0F, -12.0F, -1.0F, 4, 12, 2),
			PartPose.offset(0.0F, -3.0F, 0.0F));

		main.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(0, 23)
				.addBox(-2.0F, -3.0F, -1.0F, 3, 1, 2),
			PartPose.offset(0.0F, -10.0F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
