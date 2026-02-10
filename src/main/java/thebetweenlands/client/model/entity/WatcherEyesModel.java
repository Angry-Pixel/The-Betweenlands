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
	public final ModelPart eye_main;
	public final ModelPart smoll_eye_top_l;
	public final ModelPart smoll_eye_top_r;
	public final ModelPart smoll_eye_mid_r;
	public final ModelPart smoll_eye_mid_l;
	public final ModelPart smoll_eye_bot_l;
	public final ModelPart smoll_eye_bot_r;
	public final ModelPart teeny_eye_top_mid;
	public final ModelPart teeny_eye_bot_mid;
	public final ModelPart eye_main_lens;
	public final ModelPart smoll_eye_top_l_lens;
	public final ModelPart smoll_eye_top_r_lens;
	public final ModelPart smoll_eye_mid_r_lens;
	public final ModelPart smoll_eye_mid_l_lens;
	public final ModelPart smoll_eye_bot_l_lens;
	public final ModelPart smoll_eye_bot_r_lens;
	public final ModelPart teeny_eye_top_mid_lens;
	public final ModelPart teeny_eye_bot_mid_lens;


	public WatcherEyesModel(ModelPart root) {
		super(root, RenderType::entityCutoutNoCull);
		this.eye_main = root.getChild("eye_main");
		this.smoll_eye_top_l = root.getChild("smoll_eye_top_l");
		this.smoll_eye_top_r = root.getChild("smoll_eye_top_r");
		this.smoll_eye_mid_r = root.getChild("smoll_eye_mid_r");
		this.smoll_eye_mid_l = root.getChild("smoll_eye_mid_l");
		this.smoll_eye_bot_l = root.getChild("smoll_eye_bot_l");
		this.smoll_eye_bot_r = root.getChild("smoll_eye_bot_r");
		this.teeny_eye_top_mid = root.getChild("teeny_eye_top_mid");
		this.teeny_eye_bot_mid = root.getChild("teeny_eye_bot_mid");
		this.eye_main_lens = root.getChild("eye_main_lens");
		this.smoll_eye_top_l_lens = root.getChild("smoll_eye_top_l_lens");
		this.smoll_eye_top_r_lens = root.getChild("smoll_eye_top_r_lens");
		this.smoll_eye_mid_r_lens = root.getChild("smoll_eye_mid_r_lens");
		this.smoll_eye_mid_l_lens = root.getChild("smoll_eye_mid_l_lens");
		this.smoll_eye_bot_l_lens = root.getChild("smoll_eye_bot_l_lens");
		this.smoll_eye_bot_r_lens = root.getChild("smoll_eye_bot_r_lens");
		this.teeny_eye_top_mid_lens = root.getChild("teeny_eye_top_mid_lens");
		this.teeny_eye_bot_mid_lens = root.getChild("teeny_eye_bot_mid_lens");

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition eye_main = partdefinition.addOrReplaceChild("eye_main", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, 0.0F, -5.0F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, -6.0F));

		PartDefinition smoll_eye_top_l = partdefinition.addOrReplaceChild("smoll_eye_top_l", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 9.0F, -7.0F));

		PartDefinition smoll_eye_top_r = partdefinition.addOrReplaceChild("smoll_eye_top_r", CubeListBuilder.create().texOffs(15, 18).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.5F, 10.0F, -7.5F));

		PartDefinition smoll_eye_mid_r = partdefinition.addOrReplaceChild("smoll_eye_mid_r", CubeListBuilder.create().texOffs(15, 23).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.5F, 14.0F, -7.5F));

		PartDefinition smoll_eye_mid_l = partdefinition.addOrReplaceChild("smoll_eye_mid_l", CubeListBuilder.create().texOffs(0, 24).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.5F, 15.0F, -7.5F));

		PartDefinition smoll_eye_bot_l = partdefinition.addOrReplaceChild("smoll_eye_bot_l", CubeListBuilder.create().texOffs(25, 0).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 19.0F, -7.5F));

		PartDefinition smoll_eye_bot_r = partdefinition.addOrReplaceChild("smoll_eye_bot_r", CubeListBuilder.create().texOffs(15, 11).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 19.0F, -7.0F));

		PartDefinition teeny_eye_top_mid = partdefinition.addOrReplaceChild("teeny_eye_top_mid", CubeListBuilder.create().texOffs(26, 22).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 9.0F, -7.5F));

		PartDefinition teeny_eye_bot_mid = partdefinition.addOrReplaceChild("teeny_eye_bot_mid", CubeListBuilder.create().texOffs(26, 25).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, -7.5F));

		PartDefinition eye_main_lens = partdefinition.addOrReplaceChild("eye_main_lens", CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, 0.01F, -5.01F, 7.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, -6.0F));

		PartDefinition smoll_eye_top_l_lens = partdefinition.addOrReplaceChild("smoll_eye_top_l_lens", CubeListBuilder.create().texOffs(25, 5).addBox(-2.0F, 0.0F, -3.01F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 9.0F, -7.0F));

		PartDefinition smoll_eye_top_r_lens = partdefinition.addOrReplaceChild("smoll_eye_top_r_lens", CubeListBuilder.create().texOffs(11, 28).addBox(-1.5F, 0.0F, -2.01F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.5F, 10.0F, -7.5F));

		PartDefinition smoll_eye_mid_r_lens = partdefinition.addOrReplaceChild("smoll_eye_mid_r_lens", CubeListBuilder.create().texOffs(18, 28).addBox(-1.5F, 0.0F, -2.04F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.5F, 14.0F, -7.5F));

		PartDefinition smoll_eye_mid_l_lens = partdefinition.addOrReplaceChild("smoll_eye_mid_l_lens", CubeListBuilder.create().texOffs(0, 29).addBox(-1.5F, 0.0F, -2.01F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(6.5F, 15.0F, -7.5F));

		PartDefinition smoll_eye_bot_l_lens = partdefinition.addOrReplaceChild("smoll_eye_bot_l_lens", CubeListBuilder.create().texOffs(25, 28).addBox(-1.5F, 0.0F, -2.01F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, 19.0F, -7.5F));

		PartDefinition smoll_eye_bot_r_lens = partdefinition.addOrReplaceChild("smoll_eye_bot_r_lens", CubeListBuilder.create().texOffs(26, 18).addBox(-2.0F, 0.0F, -3.01F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 19.0F, -7.0F));

		PartDefinition teeny_eye_top_mid_lens = partdefinition.addOrReplaceChild("teeny_eye_top_mid_lens", CubeListBuilder.create().texOffs(25, 9).addBox(-1.0F, 0.0F, -1.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 9.0F, -7.5F));

		PartDefinition teeny_eye_bot_mid_lens = partdefinition.addOrReplaceChild("teeny_eye_bot_mid_lens", CubeListBuilder.create().texOffs(30, 9).addBox(-1.0F, 0.0F, -1.01F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, -7.5F));

		return LayerDefinition.create(meshdefinition, 64, 64);

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

		eye_main.xRot = 1.5708F - open;
		smoll_eye_top_l.xRot = 1.5708F - open;
		smoll_eye_top_r.xRot = 1.5708F - open;
		smoll_eye_mid_r.xRot = 1.5708F - open;
		smoll_eye_mid_l.xRot = 1.5708F - open;
		smoll_eye_bot_l.xRot = 1.5708F - open;
		smoll_eye_bot_r.xRot = 1.5708F - open;
		teeny_eye_top_mid.xRot = 1.5708F - open;
		teeny_eye_bot_mid.xRot = 1.5708F - open;
		
		eye_main_lens.xRot = 1.5708F - open;
		smoll_eye_top_l_lens.xRot = 1.5708F - open;
		smoll_eye_top_r_lens.xRot = 1.5708F - open;
		smoll_eye_mid_r_lens.xRot = 1.5708F - open;
		smoll_eye_mid_l_lens.xRot = 1.5708F - open;
		smoll_eye_bot_l_lens.xRot = 1.5708F - open;
		smoll_eye_bot_r_lens.xRot = 1.5708F - open;
		teeny_eye_top_mid_lens.xRot = 1.5708F - open;
		teeny_eye_bot_mid_lens.xRot = 1.5708F - open;

		if (eyeGroupPick1 == 0) {
			eye_main_lens.yScale = 1F - scale1;
			eye_main_lens.y += scale1 * 2.5F;
		}
		if (eyeGroupPick1 == 1) {
			smoll_eye_top_l_lens.yScale = 1F - scale2;
			smoll_eye_top_l_lens.y += scale2 * 1.5F;
		}
		if (eyeGroupPick1 == 2) {
			smoll_eye_top_r_lens.yScale = 1F - scale3;
			smoll_eye_top_r_lens.y += scale3 * 1F;
		}
		if (eyeGroupPick2 == 0) {
			smoll_eye_mid_r_lens.yScale = 1F - scale1;
			smoll_eye_mid_r_lens.y += scale1 * 1F;
		}
		if (eyeGroupPick2 == 1) {
			smoll_eye_mid_l_lens.yScale = 1F - scale2;
			smoll_eye_mid_l_lens.y += scale2 * 1F;
		}
		if (eyeGroupPick2 == 2) {
			smoll_eye_bot_l_lens.yScale = 1F - scale3;
			smoll_eye_bot_l_lens.y += scale3 * 1F;
		}
		if (eyeGroupPick3 == 0) {
			smoll_eye_bot_r_lens.yScale = 1F - scale1;
			smoll_eye_bot_r_lens.y += scale1 * 1.5F;
		}
		if (eyeGroupPick3 == 1) {
			teeny_eye_top_mid_lens.yScale = 1F - scale2;
			teeny_eye_top_mid_lens.y += scale2 * 0.5F;
		}
		if (eyeGroupPick3 == 2) {
			teeny_eye_bot_mid_lens.yScale = 1F - scale3;
			teeny_eye_bot_mid_lens.y += scale3 * 0.5F;
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

	public void renderEyesToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		eye_main_lens.render(stack, consumer, light, overlay, color);
		smoll_eye_top_l_lens.render(stack, consumer, light, overlay, color);
		smoll_eye_top_r_lens.render(stack, consumer, light, overlay, color);
		smoll_eye_mid_r_lens.render(stack, consumer, light, overlay, color);
		smoll_eye_mid_l_lens.render(stack, consumer, light, overlay, color);
		smoll_eye_bot_l_lens.render(stack, consumer, light, overlay, color);
		smoll_eye_bot_r_lens.render(stack, consumer, light, overlay, color);
		teeny_eye_top_mid_lens.render(stack, consumer, light, overlay, color);
		teeny_eye_bot_mid_lens.render(stack, consumer, light, overlay, color);
	}
}