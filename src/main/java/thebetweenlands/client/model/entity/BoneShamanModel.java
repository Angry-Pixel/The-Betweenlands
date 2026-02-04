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
import thebetweenlands.common.entity.monster.BoneShaman;

public class BoneShamanModel<T extends BoneShaman> extends MowzieModelBase<T> {

	private final ModelPart body;

	public BoneShamanModel(ModelPart root) {
		super(root, RenderType::entityCutoutNoCull);
		this.body = root.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 0.0F, -4.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 0.0F));

		PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 6.0F, 1.0F));

		PartDefinition left_foot = left_leg.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(41, 21).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));

		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, 0.0F, -1.5F, 1.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(27, 7).addBox(-4.0F, -7.0F, -3.5F, 8.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition head_top = head.addOrReplaceChild("head_top", CubeListBuilder.create().texOffs(8, 18).addBox(-3.0F, -8.0F, -2.5F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition beak_bottom = head.addOrReplaceChild("beak_bottom", CubeListBuilder.create().texOffs(55, 0).addBox(-0.5F, -0.5F, -1.6F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -3.5F, 0.2618F, 0.0F, 0.0F));

		PartDefinition left_eye = head.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(50, 10).addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, -4.0F, 0.0F));

		PartDefinition beak_top = head.addOrReplaceChild("beak_top", CubeListBuilder.create().texOffs(43, 0).addBox(-1.0F, -0.5F, -1.9F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -3.5F, 0.2618F, 0.0F, 0.0F));

		PartDefinition right_eye = head.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(8, 14).addBox(-1.5F, -1.5F, -4.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, -4.0F, 0.0F));

		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(51, 0).addBox(0.0F, 0.0F, -1.5F, 1.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 1.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(34, 0).addBox(-1.5F, -0.5F, 0.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 3.5F, -0.5236F, 0.0F, 0.0F));

		PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 6.0F, 1.0F));

		PartDefinition right_foot = right_leg.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(27, 21).addBox(-2.0F, 0.0F, -4.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		body.render(stack, consumer, light, overlay, color);
	}
}