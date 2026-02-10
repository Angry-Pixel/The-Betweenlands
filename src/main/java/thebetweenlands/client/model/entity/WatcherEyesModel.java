package thebetweenlands.client.model.entity;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.WatcherEyes;

public class WatcherEyesModel extends MowzieModelBase<WatcherEyes> {
	private final ModelPart eye_main;
	private final ModelPart eye_main_lens;
	private final ModelPart smoll_eye_top_l;
	private final ModelPart smoll_eye_top_l_lens;
	private final ModelPart smoll_eye_top_r;
	private final ModelPart smoll_eye_top_r_lens;
	private final ModelPart smoll_eye_mid_r;
	private final ModelPart smoll_eye_mid_r_lens;
	private final ModelPart smoll_eye_mid_l;
	private final ModelPart smoll_eye_mid_l_lens;
	private final ModelPart smoll_eye_bot_l;
	private final ModelPart smoll_eye_bot_l_lens;
	private final ModelPart smoll_eye_bot_r;
	private final ModelPart smoll_eye_bot_r_lens;
	private final ModelPart teeny_eye_top_mid;
	private final ModelPart teeny_eye_top_mid_lens;
	private final ModelPart teeny_eye_bot_mid;
	private final ModelPart teeny_eye_bot_mid_lens;


	public WatcherEyesModel(ModelPart root) {
		super(root, RenderType::entityCutoutNoCull);
		this.eye_main = root.getChild("eye_main");
		this.eye_main_lens = this.eye_main.getChild("eye_main_lens");
		this.smoll_eye_top_l = root.getChild("smoll_eye_top_l");
		this.smoll_eye_top_l_lens = this.smoll_eye_top_l.getChild("smoll_eye_top_l_lens");
		this.smoll_eye_top_r = root.getChild("smoll_eye_top_r");
		this.smoll_eye_top_r_lens = this.smoll_eye_top_r.getChild("smoll_eye_top_r_lens");
		this.smoll_eye_mid_r = root.getChild("smoll_eye_mid_r");
		this.smoll_eye_mid_r_lens = this.smoll_eye_mid_r.getChild("smoll_eye_mid_r_lens");
		this.smoll_eye_mid_l = root.getChild("smoll_eye_mid_l");
		this.smoll_eye_mid_l_lens = this.smoll_eye_mid_l.getChild("smoll_eye_mid_l_lens");
		this.smoll_eye_bot_l = root.getChild("smoll_eye_bot_l");
		this.smoll_eye_bot_l_lens = this.smoll_eye_bot_l.getChild("smoll_eye_bot_l_lens");
		this.smoll_eye_bot_r = root.getChild("smoll_eye_bot_r");
		this.smoll_eye_bot_r_lens = this.smoll_eye_bot_r.getChild("smoll_eye_bot_r_lens");
		this.teeny_eye_top_mid = root.getChild("teeny_eye_top_mid");
		this.teeny_eye_top_mid_lens = this.teeny_eye_top_mid.getChild("teeny_eye_top_mid_lens");
		this.teeny_eye_bot_mid = root.getChild("teeny_eye_bot_mid");
		this.teeny_eye_bot_mid_lens = this.teeny_eye_bot_mid.getChild("teeny_eye_bot_mid_lens");

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition eye_main = partdefinition.addOrReplaceChild("eye_main", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, -6.0F));

		PartDefinition eye_main_lens = eye_main.addOrReplaceChild("eye_main_lens", CubeListBuilder.create().texOffs(0, 11).addBox(-2.5F, 5.01F, 0.0F, 5.0F, 0.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition smoll_eye_top_l = partdefinition.addOrReplaceChild("smoll_eye_top_l", CubeListBuilder.create().texOffs(13, 17).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, 9.0F, -7.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition smoll_eye_top_l_lens = smoll_eye_top_l.addOrReplaceChild("smoll_eye_top_l_lens", CubeListBuilder.create().texOffs(7, 24).addBox(-1.5F, 0.0F, -3.01F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition smoll_eye_top_r = partdefinition.addOrReplaceChild("smoll_eye_top_r", CubeListBuilder.create().texOffs(23, 14).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 10.0F, -7.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition smoll_eye_top_r_lens = smoll_eye_top_r.addOrReplaceChild("smoll_eye_top_r_lens", CubeListBuilder.create().texOffs(23, 10).addBox(-1.0F, 0.0F, -2.01F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition smoll_eye_mid_r = partdefinition.addOrReplaceChild("smoll_eye_mid_r", CubeListBuilder.create().texOffs(21, 5).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 14.0F, -7.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition smoll_eye_mid_r_lens = smoll_eye_mid_r.addOrReplaceChild("smoll_eye_mid_r_lens", CubeListBuilder.create().texOffs(10, 17).addBox(-1.0F, 0.0F, -2.04F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition smoll_eye_mid_l = partdefinition.addOrReplaceChild("smoll_eye_mid_l", CubeListBuilder.create().texOffs(16, 11).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 15.0F, -7.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition smoll_eye_mid_l_lens = smoll_eye_mid_l.addOrReplaceChild("smoll_eye_mid_l_lens", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -2.01F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition smoll_eye_bot_l = partdefinition.addOrReplaceChild("smoll_eye_bot_l", CubeListBuilder.create().texOffs(16, 0).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 20.0F, -7.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition smoll_eye_bot_l_lens = smoll_eye_bot_l.addOrReplaceChild("smoll_eye_bot_l_lens", CubeListBuilder.create().texOffs(0, 11).addBox(-1.0F, 0.0F, -2.01F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition smoll_eye_bot_r = partdefinition.addOrReplaceChild("smoll_eye_bot_r", CubeListBuilder.create().texOffs(0, 17).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 18.0F, -7.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition smoll_eye_bot_r_lens = smoll_eye_bot_r.addOrReplaceChild("smoll_eye_bot_r_lens", CubeListBuilder.create().texOffs(0, 24).addBox(-1.5F, 0.0F, -3.01F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition teeny_eye_top_mid = partdefinition.addOrReplaceChild("teeny_eye_top_mid", CubeListBuilder.create().texOffs(19, 24).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 9.0F, -7.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition teeny_eye_top_mid_lens = teeny_eye_top_mid.addOrReplaceChild("teeny_eye_top_mid_lens", CubeListBuilder.create().texOffs(0, 14).addBox(-0.5F, 0.0F, -1.01F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition teeny_eye_bot_mid = partdefinition.addOrReplaceChild("teeny_eye_bot_mid", CubeListBuilder.create().texOffs(14, 24).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 21.0F, -7.5F, 1.5708F, 0.0F, 0.0F));

		PartDefinition teeny_eye_bot_mid_lens = teeny_eye_bot_mid.addOrReplaceChild("teeny_eye_bot_mid_lens", CubeListBuilder.create().texOffs(0, 3).addBox(-0.5F, 0.0F, -1.01F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);


	}

	@Override
	public void setupAnim(WatcherEyes eyes, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		float open = eyes.getOpenCount() * 0.15708F;
		float scale1 = eyes.blinkTick1 * 0.1F;
		float scale2 = eyes.blinkTick2 * 0.1F;
		float scale3 = eyes.blinkTick3 * 0.1F;
		int eyeGroupPick1 = eyes.eyeGroupPick1;
		int eyeGroupPick2 = eyes.eyeGroupPick2;
		int eyeGroupPick3 = eyes.eyeGroupPick3;

		eye_main.xRot = 0F - open;
		smoll_eye_top_l.xRot = 1.5708F - open;
		smoll_eye_top_r.xRot = 1.5708F - open;
		smoll_eye_mid_r.xRot = 1.5708F - open;
		smoll_eye_mid_l.xRot = 1.5708F - open;
		smoll_eye_bot_l.xRot = 1.5708F - open;
		smoll_eye_bot_r.xRot = 1.5708F - open;
		teeny_eye_top_mid.xRot = 1.5708F - open;
		teeny_eye_bot_mid.xRot = 1.5708F - open;

		eye_main.yRot = 0F;
		smoll_eye_top_l.yRot = 0F;
		smoll_eye_top_r.yRot = 0F;
		smoll_eye_mid_r.yRot = 0F;
		smoll_eye_mid_l.yRot = 0F;
		smoll_eye_bot_l.yRot = 0F;
		smoll_eye_bot_r.yRot = 0F;
		teeny_eye_top_mid.yRot = 0F;
		teeny_eye_bot_mid.yRot = 0F;

		eye_main.zRot = 0F;
		smoll_eye_top_l.zRot = 0F;
		smoll_eye_top_r.zRot = 0F;
		smoll_eye_mid_r.zRot = 0F;
		smoll_eye_mid_l.zRot = 0F;
		smoll_eye_bot_l.zRot = 0F;
		smoll_eye_bot_r.zRot = 0F;
		teeny_eye_top_mid.zRot = 0F;
		teeny_eye_bot_mid.zRot = 0F;

		if (eyeGroupPick1 == 0) {
			eye_main_lens.zScale = 1F - scale1;
			eye_main_lens.z = 0F + scale1 * 2.5F;
		}
		if (eyeGroupPick1 == 1) {
			smoll_eye_top_l_lens.yScale = 1F - scale2;
			smoll_eye_top_l_lens.y = 0F + scale2 * 1.5F;
		}
		if (eyeGroupPick1 == 2) {
			smoll_eye_top_r_lens.yScale = 1F - scale3;
			smoll_eye_top_r_lens.y = 0F + scale3 * 1F;
		}
		if (eyeGroupPick2 == 0) {
			smoll_eye_mid_r_lens.yScale = 1F - scale1;
			smoll_eye_mid_r_lens.y = 0F + scale1 * 1F;
		}
		if (eyeGroupPick2 == 1) {
			smoll_eye_mid_l_lens.yScale = 1F - scale2;
			smoll_eye_mid_l_lens.y = 0F + scale2 * 1F;
		}
		if (eyeGroupPick2 == 2) {
			smoll_eye_bot_l_lens.yScale = 1F - scale3;
			smoll_eye_bot_l_lens.y = 0F + scale3 * 1F;
		}
		if (eyeGroupPick3 == 0) {
			smoll_eye_bot_r_lens.yScale = 1F - scale1;
			smoll_eye_bot_r_lens.y = 0F + scale1 * 1.5F;
		}
		if (eyeGroupPick3 == 1) {
			teeny_eye_top_mid_lens.yScale = 1F - scale2;
			teeny_eye_top_mid_lens.y = 0F + scale2 * 0.5F;
		}
		if (eyeGroupPick3 == 2) {
			teeny_eye_bot_mid_lens.yScale = 1F - scale3;
			teeny_eye_bot_mid_lens.y = 0F + scale3 * 0.5F;
		}
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		eye_main.render(stack, consumer, light, overlay, color);
		smoll_eye_top_l.render(stack, consumer, light, overlay, color);
		smoll_eye_top_r.render(stack, consumer, light, overlay, color);
		smoll_eye_mid_r.render(stack, consumer, light, overlay, color);
		smoll_eye_mid_l.render(stack, consumer, light, overlay, color);
		smoll_eye_bot_l.render(stack, consumer, light, overlay, color);
		smoll_eye_bot_r.render(stack, consumer, light, overlay, color);
		teeny_eye_top_mid.render(stack, consumer, light, overlay, color);
		teeny_eye_bot_mid.render(stack, consumer, light, overlay, color);
	}
}