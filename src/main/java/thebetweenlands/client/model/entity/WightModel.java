package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.Wight;

public class WightModel<T extends Entity> extends MowzieModelBase<T> {

	public boolean renderHeadOnly = false;

	private final ModelPart root;
	private final ModelPart neck;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart jaw;

	private final ModelPart[] headPieces;

	public WightModel(ModelPart root) {
		super(RenderType::entityTranslucent);
		this.root = root;
		ModelPart body = root.getChild("body");
		this.neck = root.getChild("neck");

		this.leftArm = body.getChild("left_arm");
		this.rightLeg = body.getChild("right_leg");
		this.leftLeg = body.getChild("left_leg");
		this.rightArm = body.getChild("right_arm");

		this.jaw = this.neck.getChild("jaw");

		this.headPieces = new ModelPart[]{
			this.neck.getChild("head1"),
			this.neck.getChild("head2"),
			this.neck.getChild("head3"),
			this.jaw
		};
	}

	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partDefinition = meshdefinition.getRoot();

		PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0)
				.addBox(-4.0F, -1.3F, -2.1F, 8, 8, 5),
			PartPose.offsetAndRotation(0.0F, -2.5F, 1.7F, 0.045553093477052F, 0.0F, 0.0F));
		body.addOrReplaceChild("left_chest", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-0.4F, -6.3F, -2.8F, 5, 6, 6),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2832669375986797F, -0.136659280431156F, -0.03665191429188092F));
		body.addOrReplaceChild("right_chest", CubeListBuilder.create()
				.texOffs(23, 14).addBox(-4.6F, -6.3F, -2.8F, 5, 6, 6),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2832669375986797F, 0.136659280431156F, 0.03665191429188092F));

		body.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(9, 28).addBox(0.0F, -1.0F, -1.0F, 2, 22, 2),
			PartPose.offsetAndRotation(4.6F, -4.6F, -1.5F, -0.045553093477052F, -0.136659280431156F, 0.0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(0, 28).addBox(-2.0F, -1.0F, -1.0F, 2, 22, 2),
			PartPose.offsetAndRotation(-4.6F, -4.6F, -1.5F, -0.045553093477052F, 0.136659280431156F, 0.0F));
		body.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(18, 28).addBox(-1.0F, -0.2F, -1.0F, 2, 20, 2),
			PartPose.offsetAndRotation(2.3F, 6.7F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));
		body.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(27, 28).addBox(-1.0F, -0.2F, -1.0F, 2, 20, 2),
			PartPose.offsetAndRotation(-2.3F, 6.7F, 0.0F, -0.045553093477052F, 0.0F, 0.0F));

		PartDefinition neck = partDefinition.addOrReplaceChild("neck", CubeListBuilder.create()
				.texOffs(50, 0).addBox(-1.5F, -3.0F, -1.5F, 3, 4, 3),
			PartPose.offsetAndRotation(0.0F, -7.8F, -0.2F, 0.8196066167365371F, 0.0F, 0.0F));
		PartDefinition head1 = neck.addOrReplaceChild("head1", CubeListBuilder.create()
				.texOffs(50, 8).addBox(-4.0F, -3.7F, -4.8F, 8, 5, 8),
			PartPose.offsetAndRotation(0.0F, -5.5F, -0.8F, -0.36425021489121656F, 0.0F, 0.0F));
		PartDefinition jaw = neck.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(85, 0).addBox(-3.0F, -0.3F, -5.0F, 6, 1, 6),
			PartPose.offsetAndRotation(0.0F, -2.0F, -1.7F, 0.5009094953223726F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("left_jaw", CubeListBuilder.create()
				.texOffs(110, 0).addBox(2.0F, -1.3F, -5.0F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("right_jaw", CubeListBuilder.create()
				.texOffs(110, 5).addBox(-3.0F, -1.3F, -5.0F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		jaw.addOrReplaceChild("jaw", CubeListBuilder.create()
				.texOffs(110, 10).addBox(-1.0F, -1.3F, -5.0F, 2, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		neck.addOrReplaceChild("head2", CubeListBuilder.create()
				.texOffs(85, 14).addBox(-4.0F, 1.3F, -0.8F, 8, 1, 4),
			PartPose.offsetAndRotation(0.0F, -5.5F, -0.8F, -0.36425021489121656F, 0.0F, 0.0F));
		neck.addOrReplaceChild("head3", CubeListBuilder.create()
				.texOffs(85, 8).addBox(-3.5F, -1.2F, -2.2F, 7, 2, 3),
			PartPose.offsetAndRotation(0.0F, -1.4F, 0.2F, -0.36425021489121656F, 0.0F, 0.0F));

		head1.addOrReplaceChild("left_head_1", CubeListBuilder.create()
				.texOffs(50, 22).addBox(1.0F, 1.3F, -4.8F, 2, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("right_head_1", CubeListBuilder.create()
				.texOffs(57, 22).addBox(-3.0F, 1.3F, -4.8F, 2, 1, 1),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("left_head_2", CubeListBuilder.create()
				.texOffs(64, 22).addBox(2.0F, 1.3F, -3.8F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("right_head_2", CubeListBuilder.create()
				.texOffs(73, 22).addBox(-3.0F, 1.3F, -3.8F, 1, 1, 3),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
		head1.addOrReplaceChild("cap", CubeListBuilder.create()
				.texOffs(50, 27).addBox(-4.5F, -5.0F, 0.0F, 9, 10, 9),
			PartPose.offsetAndRotation(0.0F, 0.75F, -5.3F, 0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		if (this.renderHeadOnly) {
			for (ModelPart part : this.headPieces) {
				part.render(stack, consumer, light, overlay, color);
			}
		} else {
			super.renderToBuffer(stack, consumer, light, overlay, color);
		}
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftArm.xRot = -Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.rightLeg.xRot = -Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
	}

	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		if (entity instanceof Wight wight) {
			this.neck.xRot = 0.4F + wight.getHidingAnimation(partialTick);
			this.jaw.xRot = -0.4F + 1.0F - wight.getHidingAnimation(partialTick);
		} else {
			this.neck.xRot = 0.4F;
			this.jaw.xRot = -0.4F + 1;
		}
	}

	public WightModel<T> setRenderHeadOnly(boolean headOnly) {
		this.renderHeadOnly = headOnly;
		return this;
	}
}
