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
import net.minecraft.util.Mth;

public class PossessedBlockModel extends Model {

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart leftArm;
	private final ModelPart rightArm;

	public PossessedBlockModel(ModelPart root) {
		super(RenderType::entityTranslucent);
		this.root = root;
		this.head = root.getChild("head");
		this.leftArm = root.getChild("left_arm");
		this.rightArm = root.getChild("right_arm");
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(22, 0).addBox("head", -2.5F, -3.5F, -5.0F, 5.0F, 4.0F, 5.0F)
				.texOffs(27, 10).addBox("forehead", -2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 1.0F)
				.texOffs(30, 14).addBox("nose", -0.5F, -1.0F, -6.0F, 1.0F, 1.0F, 1.0F)
				.texOffs(38, 10).addBox("left_cheek", 2.0F, -2.0F, -6.0F, 1.0F, 3.0F, 5.0F)
				.texOffs(13, 10).addBox("right_cheek", -3.0F, -2.0F, -6.0F, 1.0F, 3.0F, 5.0F)
				.texOffs(27, 17).addBox("top_jaw", -2.0F, 0.0F, -6.0F, 4.0F, 2.0F, 1.0F),
			PartPose.offsetAndRotation(0.0F, -10.0F, 2.0F, -0.5236F, 0.0F, 0.2618F));

		head.addOrReplaceChild("fangs", CubeListBuilder.create()
				.texOffs(27, 21).addBox("left_fang", -0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F)
				.texOffs(33, 21).addBox("right_fang", 1.5F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F),
			PartPose.offsetAndRotation(-1.0F, 2.0F, -6.0F, 0.5236F, 0.0F, 0.0F));

		PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(24, 36).addBox("jaw", -2.0F, 0.0F, -3.5F, 4.0F, 1.0F, 4.0F)
				.texOffs(36, 42).addBox("left_fang", 1.0F, -1.0F, -4.5F, 1.0F, 2.0F, 1.0F)
				.texOffs(24, 42).addBox("right_fang", -2.0F, -1.0F, -4.5F, 1.0F, 2.0F, 1.0F)
				.texOffs(26, 25).addBox("tongue", -1.0F, -1.0F, -3.5F, 2.0F, 1.0F, 4.0F),
			PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 1.5708F, 0.0F, 0.0F));

		jaw.addOrReplaceChild("toungue", CubeListBuilder.create()
				.texOffs(27, 31).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 1.0F, 3.0F),
			PartPose.offsetAndRotation(0.0F, -1.0F, -3.5F, 0.5236F, 0.0F, 0.0F));

		PartDefinition leftarm = partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(47, 32).addBox(0.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F),
			PartPose.offsetAndRotation(3.0F, -9.0F, 3.0F, 0.6981F, 0.1745F, -0.5236F));

		PartDefinition leftarm2 = leftarm.addOrReplaceChild("left_arm2", CubeListBuilder.create()
				.texOffs(43, 41).addBox("arm", -1.0F, 0.0F, -6.0F, 2.0F, 2.0F, 6.0F)
				.texOffs(47, 50).addBox("palm", -1.5F, -0.5F, -7.0F, 3.0F, 3.0F, 1.0F),
			PartPose.offsetAndRotation(1.0F, 4.0F, 0.0F, -0.5236F, 0.5236F, 0.4363F));

		leftarm2.addOrReplaceChild("left_thumb", CubeListBuilder.create()
				.texOffs(43, 55).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F),
			PartPose.offsetAndRotation(-0.5F, 1.5F, -7.0F, 0.3491F, 0.3491F, 0.0F));

		leftarm2.addOrReplaceChild("left_index", CubeListBuilder.create()
				.texOffs(56, 50).addBox(-0.5F, -1.0F, -3.0F, 1.0F, 1.0F, 3.0F),
			PartPose.offsetAndRotation(-1.0F, 0.5F, -7.0F, -0.1745F, 0.0F, 0.0F));

		leftarm2.addOrReplaceChild("left_pinky", CubeListBuilder.create()
				.texOffs(56, 55).addBox(-0.5F, -1.0F, -3.0F, 1.0F, 1.0F, 3.0F),
			PartPose.offsetAndRotation(1.0F, 0.5F, -7.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition rightarm = partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(8, 32).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F),
			PartPose.offsetAndRotation(-3.0F, -8.0F, 3.0F, -0.5236F, 0.3491F, 0.3491F));

		PartDefinition rightarm2 = rightarm.addOrReplaceChild("right_arm2", CubeListBuilder.create()
				.texOffs(4, 41).addBox("arm", -2.0F, -1.0F, -6.0F, 2.0F, 2.0F, 6.0F)
				.texOffs(9, 50).addBox("palm", -2.5F, -1.5F, -7.0F, 3.0F, 3.0F, 1.0F),
			PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.2618F, -0.2618F, -0.3491F));

		rightarm2.addOrReplaceChild("right_thumb", CubeListBuilder.create()
				.texOffs(15, 55).addBox(0.0F, 0.0F, -2.0F, 1.0F, 1.0F, 2.0F),
			PartPose.offsetAndRotation(-0.5F, 0.5F, -7.0F, 0.3491F, -0.3491F, 0.0F));

		rightarm2.addOrReplaceChild("right_index", CubeListBuilder.create()
				.texOffs(0, 50).addBox(-0.5F, -1.0F, -3.0F, 1.0F, 1.0F, 3.0F),
			PartPose.offsetAndRotation(0.0F, -0.5F, -7.0F, -0.1745F, 0.0F, 0.0F));

		rightarm2.addOrReplaceChild("right_pinky", CubeListBuilder.create()
				.texOffs(0, 55).addBox(-0.5F, -1.0F, -3.0F, 1.0F, 1.0F, 3.0F),
			PartPose.offsetAndRotation(-2.0F, -0.5F, -7.0F, -0.2618F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(22, 46).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 8.0F, 4.0F),
			PartPose.offsetAndRotation(0.0F, -10.0F, 2.0F, 0.6981F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
		this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
	}

	public void animate(boolean active, int animTicks, float headBob, float partialTick) {
		this.root.visible = active || animTicks > 8;

		if (animTicks <= 8) {
			this.root.z = ((-1.0F / 8 * animTicks) * 15.0F) + partialTick;
		} else {
			this.root.z = -15.0F;
			this.head.yRot = headBob * Mth.DEG_TO_RAD;
			this.rightArm.yRot = (30.0F - animTicks) * Mth.DEG_TO_RAD;
			this.leftArm.yRot = (-10.0F + animTicks) * Mth.DEG_TO_RAD;
		}
	}
}
