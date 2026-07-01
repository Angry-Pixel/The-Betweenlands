package thebetweenlands.client.model.entity.rowboat;

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
import thebetweenlands.common.entity.rowboat.RowboatLantern;

public class RowboatLanternModel {

	private final ModelPart root;

	public RowboatLanternModel(ModelPart root) {
		this.root = root;
	}

	public static LayerDefinition createLantern() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
				.texOffs(218, 11).addBox(-2.5F, 0.0F, -2.5F, 5, 7, 5)
				.texOffs(239, 13).addBox(-1.5F, 2.0F, -1.5F, 3, 4, 3),
			PartPose.ZERO);

		base.addOrReplaceChild("top", CubeListBuilder.create()
				.texOffs(218, 24).addBox(-3.0F, -1.0F, -3.0F, 6, 2, 6),
			PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.13F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 256, 128);
	}

	public void render(RowboatLantern lantern, float partialTick, PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.xRot = lantern.getAngle(partialTick);
		this.root.render(stack, consumer, light, overlay, color);
	}
}
