package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entities.BetweenlandsEntity;
import thebetweenlands.common.entities.EntityWight;

public class ModelWight<T extends BetweenlandsEntity> extends MowzieModelBase<T> implements HeadedModel {

	public static int textureWidth = 128;
	public static int textureHeight = 64;

	public boolean renderHeadOnly = false;

	// this is use to cercnaivate mojangs badness
	public float alpha = 1.0f;

	ModelPart body_base;
	ModelPart neck;
	ModelPart chest_left;
	ModelPart chest_right;
	ModelPart armright;
	ModelPart armleft;
	ModelPart legleft;
	ModelPart legright;
	ModelPart head1;
	ModelPart head3;
	ModelPart jaw;
	ModelPart head2;
	ModelPart headpieceleft1;
	ModelPart headpieceright1;
	ModelPart headpieceleft2;
	ModelPart headpieceright2;
	ModelPart cap;
	ModelPart jawpieceleft;
	ModelPart jawpieceright;
	ModelPart jawpiecemid;
	ModelPart ModelCore;

	// constructs the model part herarcy
	public ModelWight(ModelPart core) {

		ModelCore = core;
		body_base = core.getChild("body_base");
		neck = body_base.getChild("neck");

		armleft = body_base.getChild("armleft");
		legright = body_base.getChild("legright");
		chest_right = body_base.getChild("chest_right");
		legleft = body_base.getChild("legleft");
		chest_left = body_base.getChild("chest_left");
		armright = body_base.getChild("armright");

		head1 = neck.getChild("head1");
		head2 = neck.getChild("head2");
		head3 = neck.getChild("head3");
		cap = head1.getChild("cap");
		headpieceright2 = head1.getChild("headpieceright2");
		headpieceright1 = head1.getChild("headpieceright1");
		headpieceleft1 = head1.getChild("headpieceleft1");
		headpieceleft2 = head1.getChild("headpieceleft2");
		jaw = neck.getChild("jaw");
		jawpieceleft = jaw.getChild("jawpieceleft");
		jawpieceright = jaw.getChild("jawpieceright");
		jawpiecemid = jaw.getChild("jawpiecemid");
	}

	public static LayerDefinition createModelLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition body = meshdefinition.getRoot();

		// Body
		PartDefinition body_base = body.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -1.3F, -2.1F, 8, 8, 5), PartPose.offsetAndRotation(0.0F, -2.5F, 1.7F, 0.045553093477052F, 0.0F, 0.0F));
		body_base.addOrReplaceChild("chest_left", CubeListBuilder.create().texOffs(0, 14).addBox(-0.4F, -6.3F, -2.8F, 5, 6, 6), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2832669375986797F, -0.136659280431156F, -0.03665191429188092F));
		body_base.addOrReplaceChild("chest_right", CubeListBuilder.create().texOffs(23, 14).addBox(-4.6F, -6.3F, -2.8F, 5, 6, 6), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2832669375986797F, 0.136659280431156F, 0.03665191429188092F));

		// Kicks 'n guns
		body_base.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(9, 28).addBox(0.0F, -1.0F, -1.0F, 2, 22, 2), PartPose.offsetAndRotation(4.6F, -4.6F, -1.5F, -0.045553093477052F, -0.136659280431156F, 0.0F));
		body_base.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, -1.0F, -1.0F, 2, 22, 2), PartPose.offsetAndRotation(-4.6F, -4.6F, -1.5F, -0.045553093477052F, 0.136659280431156F, 0.0F));
		body_base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(18, 28).addBox(-1.0F, -0.2F, -1.0F, 2, 20, 2), PartPose.offsetAndRotation(2.3F, 6.7F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));
		body_base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(27, 28).addBox(-1.0F, -0.2F, -1.0F, 2, 20, 2), PartPose.offsetAndRotation(-2.3F, 6.7F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));

		// Neck & head
		PartDefinition neck = body_base.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(50, 0).addBox(-1.5F, -3.0F, -1.5F, 3, 4, 3), PartPose.offsetAndRotation(0.0F, -7.8F, -0.2F, 0.8196066167365371F, 0.0F, 0.0F));
		PartDefinition head1 = neck.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(50, 8).addBox(-4.0F, -3.7F, -4.8F, 8, 5, 8), PartPose.offsetAndRotation(0.0F, -5.5F, -0.8F, -0.36425021489121656F, 0.0F, 0.0F));
		PartDefinition jaw = neck.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(85, 0).addBox(-3.0F, -0.3F, -5.0F, 6, 1, 6), PartPose.offsetAndRotation(0.0F, -2.0F, -1.7F, 0.5009094953223726F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("jawpieceleft", CubeListBuilder.create().texOffs(110, 0).addBox(2.0F, -1.3F, -5.0F, 1, 1, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("jawpieceright", CubeListBuilder.create().texOffs(110, 5).addBox(-3.0F, -1.3F, -5.0F, 1, 1, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("jawpiecemid", CubeListBuilder.create().texOffs(110, 10).addBox(-1.0F, -1.3F, -5.0F, 2, 1, 1), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		neck.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(85, 14).addBox(-4.0F, 1.3F, -0.8F, 8, 1, 4), PartPose.offsetAndRotation(0.0F, -5.5F, -0.8F, -0.36425021489121656F, 0.0F, 0.0F));
		neck.addOrReplaceChild("head3", CubeListBuilder.create().texOffs(85, 8).addBox(-3.5F, -1.2F, -2.2F, 7, 2, 3), PartPose.offsetAndRotation(0.0F, -1.4F, 0.2F, -0.36425021489121656F, 0.0F, 0.0F));

		head1.addOrReplaceChild("headpieceleft1", CubeListBuilder.create().texOffs(50, 22).addBox(1.0F, 1.3F, -4.8F, 2, 1, 1), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("headpieceright1", CubeListBuilder.create().texOffs(57, 22).addBox(-3.0F, 1.3F, -4.8F, 2, 1, 1), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("headpieceleft2", CubeListBuilder.create().texOffs(64, 22).addBox(2.0F, 1.3F, -3.8F, 1, 1, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("headpieceright2", CubeListBuilder.create().texOffs(73, 22).addBox(-3.0F, 1.3F, -3.8F, 1, 1, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(50, 27).addBox(-4.5F, -5.0F, 0.0F, 9, 10, 9), PartPose.offsetAndRotation(0.0F, 0.75F, -5.3F, 0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, textureWidth, textureHeight);
	}

	@Override
	public ModelPart root() {
		return ModelCore;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAngle, float entityTickTime, float p_102622_,
						  float p_102623_) {
		if (entity instanceof EntityWight) {
			EntityWight wight = (EntityWight) entity;
			// looks alot more like hes having a sulk
			neck.xRot = 0.4F + wight.getHidingAnimation(entityTickTime - entity.tickCount);
			jaw.xRot = -0.4F + 1F - wight.getHidingAnimation(entityTickTime - entity.tickCount);
		} else {
			neck.xRot = 0.4F;
			jaw.xRot = -0.4F + 1;
		}
		armright.xRot = (float) Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
		armleft.xRot = (float) -Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
		legleft.xRot = (float) Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;
		legright.xRot = (float) -Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAngle;

		if (!this.renderHeadOnly) {
			neck.visible = true;
			body_base.visible = true;
		} else {
			neck.visible = false;
			body_base.visible = false;
			head1.visible = true;
			head3.visible = true;
			jaw.visible = true;
			head2.visible = true;
		}
	}

	public static MeshDefinition createBodyMesh(CubeDeformation none) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		return meshdefinition;
	}

	@Override
	public ModelPart getHead() {
		return head1;
	}

	public ModelPart getHead3() {
		return head3;
	}

	public ModelPart getHead2() {
		return head2;
	}

	public ModelPart getJaw() {
		return jaw;
	}

	public ModelWight<T> setRenderHeadOnly() {
		this.renderHeadOnly = true;
		return this;
	}
}
