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
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.projectile.BoneShamanProjectile;

public class BoneShamanProjectileModel<T extends BoneShamanProjectile> extends MowzieModelBase<T> {
	private final ModelPart head_base;
	private final ModelPart head_top;
	private final ModelPart jaw;

	public BoneShamanProjectileModel(ModelPart root) {
		super(root);
		this.head_base = root.getChild("head_base");
		this.head_top = this.head_base.getChild("head_top");
		this.jaw = this.head_base.getChild("jaw");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head_base = partdefinition.addOrReplaceChild("head_base", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, -3.0F, -2.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 22.0F, 3.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition head_top = head_base.addOrReplaceChild("head_top", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -7.0F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(11, 11).addBox(1.0F, 0.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(2.0F, 0.0F, -6.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(17, 11).addBox(-3.0F, 0.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 3).addBox(-3.0F, 0.0F, -6.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.5F));

		PartDefinition jaw = head_base.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(19, 0).addBox(-2.0F, 0.0F, -5.0F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(23, 11).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -1.5F, 0.2618F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public void setupAnim(BoneShamanProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, int color) {
		head_base.render(stack, consumer, light, overlay, color);
	}
}