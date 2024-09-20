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

public class LurkerSkinShieldModel extends Model {

	private final ModelPart root;

	public LurkerSkinShieldModel(ModelPart root) {
		super(RenderType::entityCutoutNoCull);
		this.root = root;
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var handle = partDefinition.addOrReplaceChild("handle", CubeListBuilder.create()
				.addBox(-1.0F, -3.0F, -2.0F, 2, 6, 7),
			PartPose.ZERO);

		var main = handle.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 14)
				.addBox(-6.0F, 0.0F, -2.0F, 12, 10, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.045553093477052F, 0.0F, 0.0F));

		var plate1 = main.addOrReplaceChild("plate_1", CubeListBuilder.create()
				.texOffs(0, 27)
				.addBox(-6.0F, -10.0F, 0.0F, 12, 10, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.091106186954104F, 0.0F, 0.0F));

		plate1.addOrReplaceChild("plate_2", CubeListBuilder.create()
				.texOffs(0, 40)
				.addBox(-6.0F, -3.0F, 0.0F, 12, 3, 2),
			PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -0.5009094953223726F, 0.0F, 0.0F));

		main.addOrReplaceChild("plate_3", CubeListBuilder.create()
				.texOffs(0, 46)
				.addBox(-6.0F, 0.0F, 0.0F, 12, 3, 2),
			PartPose.offsetAndRotation(0.0F, 10.0F, -2.0F, 0.5009094953223726F, 0.0F, 0.0F));

		var skin = main.addOrReplaceChild("skin_1", CubeListBuilder.create()
				.texOffs(56, 0)
				.addBox(-5.0F, 0.0F, 0.0F, 10, 10, 0),
			PartPose.offsetAndRotation(0.0F, 0.0F, -3.5F, 0.091106186954104F, 0.0F, 0.0F));

		skin.addOrReplaceChild("skin_2", CubeListBuilder.create()
				.texOffs(56, 11)
				.addBox(-5.0F, -10.0F, 0.0F, 10, 10, 0),
			PartPose.rotation(-0.27314402793711257F, 0.0F, 0.0F));

		var midBeam1 = main.addOrReplaceChild("middle_beam_1", CubeListBuilder.create()
				.texOffs(29, 0)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 11, 2),
			PartPose.offsetAndRotation(0.0F, 0.09F, -4.0F, 0.091106186954104F, 0.0F, 0.0F));

		var midBeam2 = midBeam1.addOrReplaceChild("middle_beam_2", CubeListBuilder.create()
				.texOffs(29, 14)
				.addBox(-1.01F, -11.0F, 0.0F, 2, 11, 2),
			PartPose.rotation(-0.27314402793711257F, 0.0F, 0.0F));

		midBeam2.addOrReplaceChild("middle_beam_3", CubeListBuilder.create()
				.texOffs(29, 28)
				.addBox(-1.0F, -5.0F, 0.0F, 2, 5, 2),
			PartPose.offsetAndRotation(0.0F, -11.0F, 0.0F, -0.5009094953223726F, 0.0F, 0.0F));

		midBeam1.addOrReplaceChild("middle_beam_4", CubeListBuilder.create()
				.texOffs(29, 36)
				.addBox(-1.01F, 0.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, 0.5009094953223726F, 0.0F, 0.0F));

		var leftBeam1 = main.addOrReplaceChild("left_beam_1", CubeListBuilder.create()
				.texOffs(38, 0)
				.addBox(-1.01F, 0.0F, 0.0F, 2, 10, 2),
			PartPose.offsetAndRotation(6.0F, 0.0F, -3.5F, 0.091106186954104F, 0.0F, 0.0F));

		var leftBeam2 = leftBeam1.addOrReplaceChild("left_beam_2", CubeListBuilder.create()
				.texOffs(38, 13)
				.addBox(-1.0F, -10.0F, 0.0F, 2, 10, 2),
			PartPose.rotation(-0.27314402793711257F, 0.0F, 0.0F));

		var leftBeam3 = leftBeam2.addOrReplaceChild("left_beam_3", CubeListBuilder.create()
				.texOffs(38, 26)
				.addBox(-1.01F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -0.5009094953223726F, 0.0F, 0.0F));

		leftBeam3.addOrReplaceChild("left_beam_4", CubeListBuilder.create()
				.texOffs(38, 33)
				.addBox(-2.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(1.0F, -4.0F, 1.0F, 0.0F, 0.0F, -0.5462880558742251F));

		var leftBeam5 = leftBeam1.addOrReplaceChild("left_beam_5", CubeListBuilder.create()
				.texOffs(38, 39)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.5009094953223726F, 0.0F, 0.0F));

		leftBeam5.addOrReplaceChild("left_beam_6", CubeListBuilder.create()
				.texOffs(38, 46)
				.addBox(-2.0F, 0.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(1.0F, 4.0F, 1.0F, 0.0F, 0.0F, 0.5462880558742251F));

		var rightBeam1 = main.addOrReplaceChild("right_beam_1", CubeListBuilder.create()
				.texOffs(38, 0)
				.addBox(-1.01F, 0.0F, 0.0F, 2, 10, 2),
			PartPose.offsetAndRotation(-6.0F, 0.0F, -3.5F, 0.091106186954104F, 0.0F, 0.0F));

		var rightBeam2 = rightBeam1.addOrReplaceChild("right_beam_2", CubeListBuilder.create()
				.texOffs(38, 13)
				.addBox(-1.0F, -10.0F, 0.0F, 2, 10, 2),
			PartPose.rotation(-0.27314402793711257F, 0.0F, 0.0F));

		var rightBeam3 = rightBeam2.addOrReplaceChild("right_beam_3", CubeListBuilder.create()
				.texOffs(38, 26)
				.addBox(-1.01F, -4.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -0.5009094953223726F, 0.0F, 0.0F));

		rightBeam3.addOrReplaceChild("right_beam_4", CubeListBuilder.create()
				.texOffs(38, 33)
				.addBox(0.0F, -3.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-1.0F, -4.0F, 1.0F, 0.0F, 0.0F, 0.5462880558742251F));

		var rightBeam5 = rightBeam1.addOrReplaceChild("right_beam_5", CubeListBuilder.create()
				.texOffs(38, 39)
				.addBox(-1.0F, 0.0F, 0.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.5009094953223726F, 0.0F, 0.0F));

		rightBeam5.addOrReplaceChild("right_beam_6", CubeListBuilder.create()
				.texOffs(38, 46)
				.addBox(0.0F, 0.0F, -1.0F, 2, 3, 2),
			PartPose.offsetAndRotation(-1.0F, 4.0F, 1.0F, 0.0F, 0.0F, -0.5462880558742251F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		this.root.render(stack, consumer, light, overlay, color);
	}
}
