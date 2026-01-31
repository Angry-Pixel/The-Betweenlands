package thebetweenlands.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import thebetweenlands.common.entity.projectile.ThrownBone;

public class ThrownBoneModel<T extends ThrownBone> extends HierarchicalModel<T> {
	public ModelPart root;
	public ModelPart weapon;

	public ThrownBoneModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.root = root;
		this.weapon = root.getChild("weapon");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition weapon = partdefinition.addOrReplaceChild("weapon", CubeListBuilder.create().texOffs(22, 14).addBox(-0.5119F, -0.6F, -4.1082F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 20.8F, 1.1F, -3.1416F, 0.7418F, 1.5708F));

		PartDefinition slime1_r1 = weapon.addOrReplaceChild("slime1_r1", CubeListBuilder.create().texOffs(21, 0).addBox(-1.0F, 0.4F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6623F, -1.0F, 2.1881F, 0.6545F, 0.4363F, 0.0F));

		PartDefinition slime1_r2 = weapon.addOrReplaceChild("slime1_r2", CubeListBuilder.create().texOffs(21, 0).addBox(-1.0F, -0.6F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7842F, 0.0F, 4.0481F, -0.1309F, -0.3054F, 0.0F));

		PartDefinition bone_r1 = weapon.addOrReplaceChild("bone_r1", CubeListBuilder.create().texOffs(19, 6).addBox(-2.0F, -0.6F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0849F, 0.0F, 3.0944F, 0.0F, -0.3054F, 0.0F));

		PartDefinition bone_r2 = weapon.addOrReplaceChild("bone_r2", CubeListBuilder.create().texOffs(16, 21).addBox(0.0F, -0.6F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-0.5119F, 0.0F, -0.1082F, 0.0F, 0.4363F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		weapon.render(stack, consumer, light, overlay, color);
	}

	@Override
	public ModelPart root() {
		return root;
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}