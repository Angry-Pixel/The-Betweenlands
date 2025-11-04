package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.LargeSludgeWorm;

public class LargeSludgeWormModel extends MowzieModelBase<LargeSludgeWorm> {

	private final ModelPart body;
	private final ModelPart tail;
	private ModelPart head;
	private ModelPart leftLowerJaw;
	private ModelPart rightLowerJaw;
	private ModelPart heart1;
	private ModelPart heart2;
	private ModelPart spine5;
	private ModelPart spine6;
	private ModelPart spine7;
	private ModelPart spine8;
	private ModelPart spine9;

	public LargeSludgeWormModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.tail = root.getChild("tail");

		if (this.body.hasChild("spine1")) {
			this.head = this.body.getChild("spine1").getChild("spine3").getChild("head1");
			this.leftLowerJaw = this.head.getChild("head2").getChild("left_lower_jaw");
			this.rightLowerJaw = this.head.getChild("head2").getChild("right_lower_jaw");
			this.heart1 = this.body.getChild("spine1").getChild("artery1").getChild("artery2").getChild("artery3").getChild("heart1");
			this.heart2 = this.heart1.getChild("heart2");
			this.spine5 = root.getChild("spine5");
			this.spine6 = root.getChild("spine6");
			this.spine7 = root.getChild("spine7");
			this.spine8 = root.getChild("spine8");
			this.spine9 = root.getChild("spine9");
		}
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var body_base = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 97).addBox(-4.0F, 0.0F, 0.0F, 8, 6, 6),
			PartPose.offsetAndRotation(0.0F, 14.0F, -3.5F, -0.18203784098300857F, 0.0F, 0.0F));
		body_base.addOrReplaceChild("ribs", CubeListBuilder.create()
				.texOffs(0, 110).addBox(-3.0F, 0.0F, 0.0F, 6, 5, 5),
			PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 0.091106186954104F, 0.0F, 0.0F));

		var spine1 = body_base.addOrReplaceChild("spine1", CubeListBuilder.create()
				.texOffs(29, 97).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 6),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		var spine2 = spine1.addOrReplaceChild("spine2", CubeListBuilder.create()
				.texOffs(46, 97).addBox(-1.0F, -2.0F, 0.0F, 2, 2, 6, new CubeDeformation(-0.001F)),
			PartPose.offsetAndRotation(0.0F, 1.0F, 6.0F, 0.091106186954104F, 0.0F, 0.0F));
		var spine3 = spine1.addOrReplaceChild("spine3", CubeListBuilder.create()
				.texOffs(63, 97).addBox(-1.0F, 0.0F, -3.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.091106186954104F, 0.0F, 0.0F));
		partDefinition.addOrReplaceChild("spine4", CubeListBuilder.create()
				.texOffs(74, 97).addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2),
			PartPose.offsetAndRotation(0.0F, 16.0F, 11.0F, 0.0F, 0.0F, 0.27314402793711257F));
		partDefinition.addOrReplaceChild("spine5", CubeListBuilder.create()
				.texOffs(83, 97).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.091106186954104F, -0.091106186954104F, -0.045553093477052F));
		partDefinition.addOrReplaceChild("spine6", CubeListBuilder.create()
				.texOffs(90, 97).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.0F, 0.27314402793711257F, -0.31869712141416456F));
		partDefinition.addOrReplaceChild("spine7", CubeListBuilder.create()
				.texOffs(97, 97).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.0F, -0.136659280431156F, -0.091106186954104F));
		partDefinition.addOrReplaceChild("spine8", CubeListBuilder.create()
				.texOffs(104, 97).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.1F, 0.091106186954104F, -0.136659280431156F, 0.18203784098300857F));
		partDefinition.addOrReplaceChild("spine9", CubeListBuilder.create()
				.texOffs(111, 97).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.045553093477052F, 0.0F, 0.045553093477052F));
		var spine10 = partDefinition.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(118, 97).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, 16.0F, 30.0F, 0.0F, 0.0F, 0.045553093477052F));
		spine10.addOrReplaceChild("tailbone", CubeListBuilder.create()
				.texOffs(129, 97).addBox(-1.0F, 0.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, -1.0F, 3.0F, -0.5918411493512771F, 0.0F, 0.0F));

		var artery1 = spine1.addOrReplaceChild("artery1", CubeListBuilder.create()
				.texOffs(23, 110).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -1.0016444577195458F, 0.0F, 0.0F));
		var artery2 = artery1.addOrReplaceChild("artery2", CubeListBuilder.create()
				.texOffs(30, 110).addBox(-0.5F, -1.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 0.18203784098300857F, 0.0F, 0.0F));
		var artery3 = artery2.addOrReplaceChild("artery3", CubeListBuilder.create()
				.texOffs(37, 110).addBox(-0.5F, -1.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, 0.36425021489121656F, 0.0F, 0.0F));
		var heart1 = artery3.addOrReplaceChild("heart1", CubeListBuilder.create()
				.texOffs(44, 110).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3),
			PartPose.offsetAndRotation(0.0F, -1.0F, 1.6F, -0.7285004297824331F, 0.0F, 0.36425021489121656F));
		heart1.addOrReplaceChild("heart2", CubeListBuilder.create()
				.texOffs(55, 110).addBox(-1.0F, -1.0F, 0.0F, 1, 2, 2),
			PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.136659280431156F, 0.0F));
		heart1.addOrReplaceChild("artery4", CubeListBuilder.create()
				.texOffs(62, 110).addBox(-0.5F, 0.0F, 0.0F, 2, 1, 1),
			PartPose.offsetAndRotation(1.0F, 0.0F, 0.5F, 0.0F, 0.31869712141416456F, 0.40980330836826856F));
		var artery5 = artery3.addOrReplaceChild("artery5", CubeListBuilder.create()
				.texOffs(69, 110).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(-0.5F, -1.0F, 2.0F, 1.2747884856566583F, 0.0F, 0.136659280431156F));
		var artery6 = artery5.addOrReplaceChild("artery6", CubeListBuilder.create()
				.texOffs(76, 110).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.4553564018453205F, 0.0F, 0.0F));
		var artery7 = artery6.addOrReplaceChild("artery7", CubeListBuilder.create()
				.texOffs(83, 110).addBox(-0.5F, 0.0F, 0.0F, 1, 1, 2),
			PartPose.offsetAndRotation(0.0F, 0.0F, 2.0F, -0.18203784098300857F, 0.0F, 0.0F));
		artery7.addOrReplaceChild("artery8", CubeListBuilder.create()
				.texOffs(90, 110).addBox(-0.5F, -1.0F, 0.0F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 1.0F, 2.0F, 0.40980330836826856F, 0.0F, 0.0F));

		var head1 = spine3.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(99, 110).addBox(-4.0F, -5.0F, -8.0F, 8, 5, 8),
			PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, -0.045553093477052F, 0.0F, 0.0F));
		var head2 = head1.addOrReplaceChild("head2", CubeListBuilder.create()
				.texOffs(132, 110).addBox(-3.0F, 0.0F, -2.0F, 6, 3, 2),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("upper_jaw", CubeListBuilder.create()
				.texOffs(217, 110).addBox(-4.0F, 0.0F, -6.0F, 8, 1, 6),
			PartPose.offset(0.0F, 0.0F, -2.0F));
		head2.addOrReplaceChild("right_lower_jaw", CubeListBuilder.create()
				.texOffs(184, 110).addBox(-3.0F, -1.5F, -11.0F, 4, 3, 12),
			PartPose.offsetAndRotation(-1.0F, 1.5F, -1.0F, 0.5918411493512771F, 0.18203784098300857F, -0.091106186954104F));
		head2.addOrReplaceChild("left_lower_jaw", CubeListBuilder.create()
				.texOffs(151, 110).addBox(-1.0F, -1.5F, -11.0F, 4, 3, 12),
			PartPose.offsetAndRotation(1.0F, 1.5F, -1.0F, 0.5918411493512771F, -0.18203784098300857F, 0.091106186954104F));

		return LayerDefinition.create(definition, 256, 128);
	}

	public static LayerDefinition createSludgeLayer() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var sludge_front1 = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-9.0F, -7.0F, -11.0F, 18, 14, 20),
			PartPose.offset(0.0F, 15.0F, -1.0F));
		sludge_front1.addOrReplaceChild("sludge_front2", CubeListBuilder.create()
				.texOffs(77, 0).addBox(-7.0F, -9.0F, -9.0F, 14, 2, 18),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		sludge_front1.addOrReplaceChild("sludge_front3", CubeListBuilder.create()
				.texOffs(142, 0).addBox(-7.0F, 7.0F, -9.0F, 14, 2, 18),
			PartPose.offset(0.0F, 0.0F, 0.0F));


		var sludge_mid1 = partDefinition.addOrReplaceChild("sludge_mid1", CubeListBuilder.create()
				.texOffs(0, 35).addBox(-9.0F, -7.0F, 0.0F, 18, 14, 16),
			PartPose.offset(0.0F, 15.0F, 8.0F));
		sludge_mid1.addOrReplaceChild("sludge_mid2", CubeListBuilder.create()
				.texOffs(69, 35).addBox(-7.0F, -9.0F, 0.0F, 14, 2, 16),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		sludge_mid1.addOrReplaceChild("sludge_mid3", CubeListBuilder.create()
				.texOffs(130, 35).addBox(-7.0F, 7.0F, 0.0F, 14, 2, 16),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		var sludge_back1 = partDefinition.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(0, 66).addBox(-9.0F, -7.0F, 0.0F, 18, 14, 16),
			PartPose.offset(0.0F, 15.0F, 24.0F));
		sludge_back1.addOrReplaceChild("sludge_back2", CubeListBuilder.create()
				.texOffs(69, 66).addBox(-7.0F, -9.0F, 0.0F, 14, 2, 14),
			PartPose.offset(0.0F, 0.0F, 0.0F));
		sludge_back1.addOrReplaceChild("sludge_back3", CubeListBuilder.create()
				.texOffs(126, 66).addBox(-7.0F, 7.0F, 0.0F, 14, 2, 14),
			PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 256, 128);
	}

	public void renderHead(LargeSludgeWorm entity, PoseStack stack, VertexConsumer consumer, int light, int overlay, int color, float partialTicks, boolean renderSolid) {
		stack.pushPose();
		LargeSludgeWorm.HullSegment headSegment = entity.segments[0];

		if (headSegment != null) {
			float headYaw = Mth.lerp(partialTicks, headSegment.prevYaw, headSegment.yaw);

			stack.scale(-1.0F, -1.0F, 1.0F);
			stack.mulPose(Axis.YP.rotationDegrees(headYaw));
			stack.translate(0.0D, -1.0D, 0.0D);
			if (renderSolid) {
				float smoothedTicks = entity.tickCount + (entity.tickCount - (entity.tickCount - 1)) * partialTicks;
				float jawWibbleLeft = Mth.sin(1F + (smoothedTicks) * 0.5F) * 0.5F;
				float jawWibbleRight = Mth.sin(1F + (smoothedTicks) * 0.5F + 0.1F) * 0.5F;

				this.leftLowerJaw.xRot = 0.5918411493512771F - 0.2F + jawWibbleLeft * 0.8F;
				this.leftLowerJaw.yRot = -0.18203784098300857F - jawWibbleRight * 0.2F;

				this.rightLowerJaw.xRot = 0.5918411493512771F - 0.2F + jawWibbleRight * 0.8F;
				this.rightLowerJaw.yRot = 0.18203784098300857F + jawWibbleRight * 0.2F;

				this.head.xRot = -0.045553093477052F - jawWibbleLeft * 0.1F;

				this.beatHeart(entity, this.heart1, 0, partialTicks);
				this.beatHeart(entity, this.heart2, 15, partialTicks);

				stack.pushPose();

				stack.translate(0.0D, 1.0D, 0.8D);
				stack.mulPose(Axis.XP.rotationDegrees(Mth.sin(smoothedTicks * 0.25F) * 3));
				stack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(smoothedTicks * 0.125F) * 13));
				stack.translate(0, -0.96D, -0.8D);

				this.body.render(stack, consumer, light, overlay, color);
				stack.popPose();
			} else {
				this.body.render(stack, consumer, light, overlay, color);
			}
		}
		stack.popPose();
	}

	private void beatHeart(LargeSludgeWorm entity, ModelPart heartPart, float tickOffset, float partialTicks) {
		float x = (entity.tickCount + tickOffset + partialTicks) * 0.15F;

		float L = 2.8F;

		x = x - (float) Math.ceil(x / L - 0.5F) * L;

		float s = 0.035F;
		float h = 3.5F;
		float d = 1.4F;
		float w = 0.01F;
		float a = 0.03F;

		float beat = (float) (a * (Math.exp(-(x + d) * (x + d) / (2 * w)) + Math.exp(-(x - d) * (x - d) / (2 * w)) + (h - Math.abs(x / s) - x) * Math.exp(-(7 * x) * (7 * x) / 2)));

		float heartScale = 1.0F + beat;

		heartPart.xScale = heartScale;
		heartPart.yScale = heartScale;
		heartPart.zScale = heartScale;
	}

	public void renderSpinePiece(int piece, float boneYaw, PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		stack.pushPose();

		stack.mulPose(Axis.YP.rotationDegrees(boneYaw));
		switch (piece) {
			case 1 -> this.spine5.render(stack, consumer, light, overlay, color);
			case 2 -> this.spine6.render(stack, consumer, light, overlay, color);
			case 3 -> this.spine7.render(stack, consumer, light, overlay, color);
			case 4 -> this.spine8.render(stack, consumer, light, overlay, color);
			case 5 -> this.spine9.render(stack, consumer, light, overlay, color);
		}
		stack.popPose();
	}

	public void renderTail(LargeSludgeWorm entity, PoseStack stack, VertexConsumer consumer, int light, int overlay, int color, float partialTicks, boolean renderSolid) {
		stack.pushPose();

		LargeSludgeWorm.HullSegment tailSegment = entity.segments[entity.segments.length - 1];

		if (tailSegment != null) {
			Vec3 pos = tailSegment.pos;
			Vec3 prevPos = tailSegment.prevPos;

			double x = Mth.lerp(partialTicks, prevPos.x, pos.x);
			double y = Mth.lerp(partialTicks, prevPos.y, pos.y);
			double z = Mth.lerp(partialTicks, prevPos.z, pos.z);

			float tailYaw = Mth.lerp(partialTicks, tailSegment.prevYaw, tailSegment.yaw);

			stack.translate(x, y, z);
			stack.scale(-1.0F, -1.0F, 1.0F);
			stack.mulPose(Axis.YP.rotationDegrees(tailYaw));
			stack.translate(0.0D, -1.0D, -1.8D);

			if (renderSolid) {
				float smoothedTicks = entity.tickCount + (entity.tickCount - (entity.tickCount - 1)) * partialTicks;
				stack.pushPose();
				stack.translate(0.0D, 1.0D, 1.6D);
				stack.mulPose(Axis.XP.rotationDegrees(Mth.sin(smoothedTicks * 0.25F) * 6));
				stack.translate(0.0D, -1.0D, -1.6D);
				this.tail.render(stack, consumer, light, overlay, color);
				stack.popPose();
			} else {
				this.tail.render(stack, consumer, light, overlay, color);
			}
		}
		stack.popPose();
	}
}
