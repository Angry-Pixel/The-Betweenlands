package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entities.Wight;

public class ModelWight<T extends Entity> extends MowzieModelBase<T> implements HeadedModel {

	public boolean renderHeadOnly = false;

	private final ModelPart body_base;
	private final ModelPart neck;
	private final ModelPart armright;
	private final ModelPart armleft;
	private final ModelPart legleft;
	private final ModelPart legright;
	private final ModelPart head1;
	private final ModelPart head3;
	private final ModelPart jaw;
	private final ModelPart head2;
	private final ModelPart root;

	public ModelWight(ModelPart root) {
		super(location -> RenderType.entityTranslucent(location, true));
		this.root = root;
		this.body_base = root.getChild("body_base");
		this.neck = this.body_base.getChild("neck");

		this.armleft = this.body_base.getChild("armleft");
		this.legright = this.body_base.getChild("legright");
		this.legleft = this.body_base.getChild("legleft");
		this.armright = this.body_base.getChild("armright");

		head1 = neck.getChild("head1");
		head2 = neck.getChild("head2");
		head3 = neck.getChild("head3");
		jaw = neck.getChild("jaw");
	}

	public static LayerDefinition createModelLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition body = meshdefinition.getRoot();

		PartDefinition body_base = body.addOrReplaceChild("body_base", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -1.3F, -2.1F, 8, 8, 5), PartPose.offsetAndRotation(0.0F, -2.5F, 1.7F, 0.045553093477052F, 0.0F, 0.0F));
		body_base.addOrReplaceChild("chest_left", CubeListBuilder.create().texOffs(0, 14).addBox(-0.4F, -6.3F, -2.8F, 5, 6, 6), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2832669375986797F, -0.136659280431156F, -0.03665191429188092F));
		body_base.addOrReplaceChild("chest_right", CubeListBuilder.create().texOffs(23, 14).addBox(-4.6F, -6.3F, -2.8F, 5, 6, 6), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2832669375986797F, 0.136659280431156F, 0.03665191429188092F));

		body_base.addOrReplaceChild("armleft", CubeListBuilder.create().texOffs(9, 28).addBox(0.0F, -1.0F, -1.0F, 2, 22, 2), PartPose.offsetAndRotation(4.6F, -4.6F, -1.5F, -0.045553093477052F, -0.136659280431156F, 0.0F));
		body_base.addOrReplaceChild("armright", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, -1.0F, -1.0F, 2, 22, 2), PartPose.offsetAndRotation(-4.6F, -4.6F, -1.5F, -0.045553093477052F, 0.136659280431156F, 0.0F));
		body_base.addOrReplaceChild("legleft", CubeListBuilder.create().texOffs(18, 28).addBox(-1.0F, -0.2F, -1.0F, 2, 20, 2), PartPose.offsetAndRotation(2.3F, 6.7F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));
		body_base.addOrReplaceChild("legright", CubeListBuilder.create().texOffs(27, 28).addBox(-1.0F, -0.2F, -1.0F, 2, 20, 2), PartPose.offsetAndRotation(-2.3F, 6.7F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));

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

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		if (this.renderHeadOnly) {
			this.head1.render(stack, consumer, light, overlay, color);
			this.head2.render(stack, consumer, light, overlay, color);
			this.head3.render(stack, consumer, light, overlay, color);
			this.jaw.render(stack, consumer, light, overlay, color);
		} else {
			super.renderToBuffer(stack, consumer, light, overlay, color);
		}
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if (entity instanceof Wight wight) {
			this.neck.xRot = 0.4F + wight.getHidingAnimation(ageInTicks - entity.tickCount);
			this.jaw.xRot = -0.4F + 1F - wight.getHidingAnimation(ageInTicks - entity.tickCount);
		} else {
			this.neck.xRot = 0.4F;
			this.jaw.xRot = -0.4F + 1;
		}
		this.armright.xRot = (float) Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.armleft.xRot = (float) -Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.legleft.xRot = (float) Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.legright.xRot = (float) -Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
	}

	@Override
	public ModelPart getHead() {
		return this.head1;
	}

	public ModelPart getHead3() {
		return this.head3;
	}

	public ModelPart getHead2() {
		return this.head2;
	}

	public ModelPart getJaw() {
		return this.jaw;
	}

	public ModelWight<?> setRenderHeadOnly(boolean headOnly) {
		this.renderHeadOnly = headOnly;
		return this;
	}
}
