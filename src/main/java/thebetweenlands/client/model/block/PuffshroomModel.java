package thebetweenlands.client.model.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.common.block.entity.PuffshroomBlockEntity;

public class PuffshroomModel {

	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart frontL1;
	private final ModelPart frontL2;
	private final ModelPart frontL3;
	private final ModelPart frontC1;
	private final ModelPart frontC2;
	private final ModelPart frontC3;
	private final ModelPart frontR1;
	private final ModelPart frontR2;
	private final ModelPart frontR3;
	private final ModelPart leftC1;
	private final ModelPart leftC2;
	private final ModelPart leftC3;
	private final ModelPart rightC1;
	private final ModelPart rightC2;
	private final ModelPart rightC3;
	private final ModelPart backL1;
	private final ModelPart backL2;
	private final ModelPart backL3;
	private final ModelPart backC1;
	private final ModelPart backC2;
	private final ModelPart backC3;
	private final ModelPart backR1;
	private final ModelPart backR2;
	private final ModelPart backR3;

	public PuffshroomModel(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.head = root.getChild("head");

		this.frontL1 = this.body.getChild("front_left_tentacle").getChild("piece1");
		this.frontL2 = this.frontL1.getChild("piece2");
		this.frontL3 = this.frontL2.getChild("piece3");
		this.frontC1 = this.body.getChild("front_center_tentacle").getChild("piece1");
		this.frontC2 = this.frontC1.getChild("piece2");
		this.frontC3 = this.frontC2.getChild("piece3");
		this.frontR1 = this.body.getChild("front_right_tentacle").getChild("piece1");
		this.frontR2 = this.frontR1.getChild("piece2");
		this.frontR3 = this.frontR2.getChild("piece3");

		this.leftC1 = this.body.getChild("left_tentacle").getChild("piece1");
		this.leftC2 = this.leftC1.getChild("piece2");
		this.leftC3 = this.leftC2.getChild("piece3");
		this.rightC1 = this.body.getChild("right_tentacle").getChild("piece1");
		this.rightC2 = this.rightC1.getChild("piece2");
		this.rightC3 = this.rightC2.getChild("piece3");

		this.backL1 = this.body.getChild("back_left_tentacle").getChild("piece1");
		this.backL2 = this.backL1.getChild("piece2");
		this.backL3 = this.backL2.getChild("piece3");
		this.backC1 = this.body.getChild("back_center_tentacle").getChild("piece1");
		this.backC2 = this.backC1.getChild("piece2");
		this.backC3 = this.backC2.getChild("piece3");
		this.backR1 = this.body.getChild("back_right_tentacle").getChild("piece1");
		this.backR2 = this.backR1.getChild("piece2");
		this.backR3 = this.backR2.getChild("piece3");
	}

	public static LayerDefinition makeModel() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var base = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-2.5F, -2.51F, -2.5F, 5, 3, 5)
				.texOffs(15, 0)
				.addBox(-1.0F, -3.5F, -1.0F, 2, 1, 2),
			PartPose.ZERO);

		var fLTent = base.addOrReplaceChild("front_left_tentacle", CubeListBuilder.create()
			.texOffs(40, 4)
			.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(-2.0F, -1.5F, -2.0F, 0.7853981633974483F, 0.7853981633974483F, 0.0F));

		var fL1 = fLTent.addOrReplaceChild("piece1", CubeListBuilder.create()
				.texOffs(40, 8)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3),
			PartPose.offset(0.0F, -1.0F, 0.0F));

		var fL2 = fL1.addOrReplaceChild("piece2", CubeListBuilder.create()
				.texOffs(52, 10)
				.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		fL2.addOrReplaceChild("piece3", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		var fCTent = base.addOrReplaceChild("front_center_tentacle", CubeListBuilder.create()
				.texOffs(54, 6)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, -1.5F, -2.2F, 0.6981317007977318F, 0.0F, 0.0F));

		var fC1 = fCTent.addOrReplaceChild("piece1", CubeListBuilder.create()
				.texOffs(20, 12)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3),
			PartPose.offset(0.0F, -1.0F, 0.0F));

		var fC2 = fC1.addOrReplaceChild("piece2", CubeListBuilder.create()
				.texOffs(32, 12)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		fC2.addOrReplaceChild("piece3", CubeListBuilder.create()
				.texOffs(44, 0)
				.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		var fRTent = base.addOrReplaceChild("front_right_tentacle", CubeListBuilder.create()
				.texOffs(48, 4)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(2.0F, -1.5F, -2.0F, 0.7853981633974483F, -0.7853981633974483F, 0.0F));

		var fR1 = fRTent.addOrReplaceChild("piece1", CubeListBuilder.create()
				.texOffs(0, 12)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3),
			PartPose.offset(0.0F, -1.0F, 0.0F));

		var fR2 = fR1.addOrReplaceChild("piece2", CubeListBuilder.create()
				.texOffs(12, 12)
				.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		fR2.addOrReplaceChild("piece3", CubeListBuilder.create()
				.texOffs(59, 0)
				.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		var lTent = base.addOrReplaceChild("left_tentacle", CubeListBuilder.create()
				.texOffs(8, 8)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(-2.2F, -1.5F, 0.0F, 0.6981317007977318F, 1.5707963267948966F, 0.0F));

		var l1 = lTent.addOrReplaceChild("piece1", CubeListBuilder.create()
				.texOffs(46, 16)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3),
			PartPose.offset(0.0F, -1.0F, 0.0F));

		var l2 = l1.addOrReplaceChild("piece2", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		l2.addOrReplaceChild("piece3", CubeListBuilder.create()
				.texOffs(59, 0)
				.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		var rTent = base.addOrReplaceChild("right_tentacle", CubeListBuilder.create()
				.texOffs(16, 8)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(2.2F, -1.5F, 0.0F, 0.6981317007977318F, -1.5707963267948966F, 0.0F));

		var r1 = rTent.addOrReplaceChild("piece1", CubeListBuilder.create()
				.texOffs(17, 17)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3),
			PartPose.offset(0.0F, -1.0F, 0.0F));

		var r2 = r1.addOrReplaceChild("piece2", CubeListBuilder.create()
				.texOffs(8, 18)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		r2.addOrReplaceChild("piece3", CubeListBuilder.create()
				.texOffs(59, 15)
				.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		var bLTent = base.addOrReplaceChild("back_left_tentacle", CubeListBuilder.create()
				.texOffs(24, 8)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(-2.0F, -1.5F, 2.0F, 0.7853981633974483F, 2.356194490192345F, 0.0F));

		var bL1 = bLTent.addOrReplaceChild("piece1", CubeListBuilder.create()
				.texOffs(35, 18)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3),
			PartPose.offset(0.0F, -1.0F, 0.0F));

		var bL2 = bL1.addOrReplaceChild("piece2", CubeListBuilder.create()
				.texOffs(27, 20)
				.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		bL2.addOrReplaceChild("piece3", CubeListBuilder.create()
				.texOffs(58, 18)
				.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		var bCTent = base.addOrReplaceChild("back_center_tentacle", CubeListBuilder.create()
				.texOffs(0, 8)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, -1.5F, 2.2F, 0.6981317007977318F, 3.141592653589793F, 0.0F));

		var bC1 = bCTent.addOrReplaceChild("piece1", CubeListBuilder.create()
				.texOffs(37, 13)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3),
			PartPose.offset(0.0F, -1.0F, 0.0F));

		var bC2 = bC1.addOrReplaceChild("piece2", CubeListBuilder.create()
				.texOffs(30, 16)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		bC2.addOrReplaceChild("piece3", CubeListBuilder.create()
				.texOffs(49, 8)
				.addBox(-0.5F, -1.5F, -0.5F, 1, 2, 1),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		var bRTent = base.addOrReplaceChild("back_right_tentacle", CubeListBuilder.create()
				.texOffs(32, 8)
				.addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(2.0F, -1.5F, 2.0F, 0.7853981633974483F, -2.356194490192345F, 0.0F));

		var bR1 = bRTent.addOrReplaceChild("piece1", CubeListBuilder.create()
				.texOffs(44, 21)
				.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3),
			PartPose.offset(0.0F, -1.0F, 0.0F));

		var bR2 = bR1.addOrReplaceChild("piece2", CubeListBuilder.create()
				.texOffs(0, 21)
				.addBox(-1.0F, -3.0F, -1.0F, 2, 4, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		bR2.addOrReplaceChild("piece3", CubeListBuilder.create()
				.texOffs(8, 22)
				.addBox(-0.5F, -2.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, -0.7853981633974483F, 0.0F, 0.0F));

		var head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(20, 0)
				.addBox(-2.5F, -7.5F, -2.5F, 5, 3, 5)
				.texOffs(47, 0)
				.addBox(-1.5F, -4.5F, -1.5F, 3, 1, 3)
			.texOffs(35, 0)
			.addBox(-1.5F, -8.5F, -1.5F, 3, 1, 3),
			PartPose.ZERO);

		head.addOrReplaceChild("left_funnel", CubeListBuilder.create()
			.texOffs(19, 3)
			.addBox(-0.5F, -1.0F, 0.1F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -8.5F, 0.0F, -0.20943951023931953F, 1.5707963267948966F, 0.0F));

		head.addOrReplaceChild("front_funnel", CubeListBuilder.create()
				.texOffs(21, 0)
				.addBox(-0.5F, -1.0F, -1.2F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -8.5F, 0.0F, 0.20943951023931953F, 0.5759586531581287F, 0.0F));

		head.addOrReplaceChild("back_funnel", CubeListBuilder.create()
				.texOffs(15, 3)
				.addBox(-0.5F, -1.0F, 0.2F, 1, 1, 1),
			PartPose.offsetAndRotation(0.0F, -8.5F, 0.0F, -0.20943951023931953F, -0.5759586531581287F, 0.0F));

		return LayerDefinition.create(definition, 64, 32);
	}

	public void renderAndAnimate(PuffshroomBlockEntity entity, float partialTicks, PoseStack stack, VertexConsumer consumer, int light, int overlay) {
		float interAnimationTicks_1 = entity.prev_animation_1 + (entity.animation_1 - entity.prev_animation_1) * partialTicks;
		float interAnimationTicks_2 = entity.prev_animation_2 + (entity.animation_2 - entity.prev_animation_2) * partialTicks;
		float interAnimationTicks_3 = entity.prev_animation_3 + (entity.animation_3 - entity.prev_animation_3) * partialTicks;
		float interAnimationTicks_4 = entity.prev_animation_4 + (entity.animation_4 - entity.prev_animation_4) * partialTicks;
		float smoothedTicks = entity.prev_renderTicks + (entity.renderTicks - entity.prev_renderTicks) * partialTicks;
		float flap = Mth.sin((smoothedTicks) * 0.325F) * 0.125F;
		float flap2 = Mth.cos((smoothedTicks) * 0.325F) * 0.125F;

		if (entity.active_1) {
			flap = 0.0F;
			flap2 = 0.0F;
		}

		this.backC1.xRot = -0.7853981633974483F + interAnimationTicks_2 / Mth.RAD_TO_DEG * 11.25F;
		this.frontC1.xRot = -0.7853981633974483F + interAnimationTicks_2 / Mth.RAD_TO_DEG * 11.25F;
		this.leftC1.xRot = -0.7853981633974483F + interAnimationTicks_2 / Mth.RAD_TO_DEG * 11.25F;
		this.rightC1.xRot = -0.7853981633974483F + interAnimationTicks_2 / Mth.RAD_TO_DEG * 11.25F;

		this.backC2.xRot = 0.0F + interAnimationTicks_2 / Mth.RAD_TO_DEG;
		this.frontC2.xRot = 0.0F + interAnimationTicks_2 / Mth.RAD_TO_DEG;
		this.leftC2.xRot = 0.0F + interAnimationTicks_2 / Mth.RAD_TO_DEG;
		this.rightC2.xRot = 0.0F + interAnimationTicks_2 / Mth.RAD_TO_DEG;

		this.backC3.xRot = 0.0F + interAnimationTicks_2 / Mth.RAD_TO_DEG;
		this.frontC3.xRot = 0.0F + interAnimationTicks_2 / Mth.RAD_TO_DEG;
		this.leftC3.xRot = 0.0F + interAnimationTicks_2 / Mth.RAD_TO_DEG;
		this.rightC3.xRot = 0.0F + interAnimationTicks_2 / Mth.RAD_TO_DEG;


		this.backR1.xRot = -0.7853981633974483F + interAnimationTicks_3 / Mth.RAD_TO_DEG * 11.25F + flap;
		this.frontR1.xRot = -0.7853981633974483F + interAnimationTicks_3 / Mth.RAD_TO_DEG * 11.25F - flap2;
		this.backL1.xRot = -0.7853981633974483F + interAnimationTicks_3 / Mth.RAD_TO_DEG * 11.25F - flap2;
		this.frontL1.xRot = -0.7853981633974483F + interAnimationTicks_3 / Mth.RAD_TO_DEG * 11.25F + flap;

		this.backR2.xRot = 0F - interAnimationTicks_3 / Mth.RAD_TO_DEG * 1.40625F - flap * 2.0F;
		this.frontR2.xRot = 0F - interAnimationTicks_3 / Mth.RAD_TO_DEG * 1.40625F + flap2 * 2.0F;
		this.backL2.xRot = 0F - interAnimationTicks_3 / Mth.RAD_TO_DEG * 1.40625F + flap2 * 2.0F;
		this.frontL2.xRot = 0F - interAnimationTicks_3 / Mth.RAD_TO_DEG * 1.40625F - flap * 2.0F;

		this.backR3.xRot = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / Mth.RAD_TO_DEG * 11.25F + flap * 4.0F;
		this.frontR3.xRot = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / Mth.RAD_TO_DEG * 11.25F - flap2 * 4.0F;
		this.backL3.xRot = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / Mth.RAD_TO_DEG * 11.25F - flap2 * 4.0F;
		this.frontL3.xRot = -0.7853981633974483F + (interAnimationTicks_3 - interAnimationTicks_4 * 0.5F) / Mth.RAD_TO_DEG * 11.25F + flap * 4.0F;


		if (entity.animation_1 < 8) {
			stack.pushPose();
			stack.translate(0.0F, 0.0F - interAnimationTicks_1 * 0.0625F, 0.0F);
			stack.mulPose(Axis.YP.rotationDegrees(0.0F + interAnimationTicks_1 * 22.5F));
			this.root.render(stack, consumer, light, overlay);
			stack.popPose();
		} else {
			stack.pushPose();
			stack.translate(0F, 0F - interAnimationTicks_1 * 0.0625F, 0.0F);
			stack.mulPose(Axis.YP.rotationDegrees(0.0F + interAnimationTicks_1 * 22.55F));
			this.body.render(stack, consumer, light, overlay);

			stack.pushPose();
			if (entity.animation_4 <= 8)
				stack.scale(1.0F + interAnimationTicks_4 * 0.125F, 1.0F, 1.0F + interAnimationTicks_4 * 0.125F);
			if (entity.animation_4 >= 10)
				stack.scale(1.75F - interAnimationTicks_4 * 0.0625F, 1.0F, 1.75F - interAnimationTicks_4 * 0.0625F);
			this.head.render(stack, consumer, light, overlay);
			stack.popPose();

			stack.popPose();
		}
	}
}
