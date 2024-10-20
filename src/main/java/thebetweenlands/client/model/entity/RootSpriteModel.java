package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.RootSprite;

public class RootSpriteModel extends MowzieModelBase<RootSprite> {

	private final ModelPart root;
	private final ModelPart bodyBase;
	private final ModelPart leftFoot;
	private final ModelPart rightFoot;
	private final ModelPart head;

	public RootSpriteModel(ModelPart root) {
		this.root = root;
		this.bodyBase = root.getChild("body_base");
		this.leftFoot = this.bodyBase.getChild("left_foot");
		this.rightFoot = this.bodyBase.getChild("right_foot");
		this.head = this.bodyBase.getChild("body_top").getChild("head");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var wobbly_body_base = partDefinition.addOrReplaceChild("body_base", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-1.5F, -2.0F, -1.0F, 3, 2, 2),
			PartPose.offsetAndRotation(0.0F, 23.2F, 0.0F, -0.091106186954104F, 0.0F, 0.0F));

		var wobbly_body_top = wobbly_body_base.addOrReplaceChild("body_top", CubeListBuilder.create()
				.texOffs(0, 5).addBox(-1.5F, -2.0F, -2.0F, 3, 2, 2),
			PartPose.offsetAndRotation(0.0F, -2.0F, 1.0F, 0.136659280431156F, 0.0F, 0.0F));

		var head = wobbly_body_top.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 10).addBox(-2.0F, -3.0F, -2.0F, 4, 3, 4),
			PartPose.offsetAndRotation(0.0F, -1.7F, -1.0F, -0.18203784098300857F, 0.0F, 0.091106186954104F));

		head.addOrReplaceChild("head_roots", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-2.0F, -2.0F, 1.0F, 4, 2, 4),
			PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, -0.136659280431156F, 0.0F, 0.0F));

		wobbly_body_base.addOrReplaceChild("left_foot", CubeListBuilder.create()
				.texOffs(14, 0).addBox(-0.5F, 0.0F, -2.0F, 2, 1, 3),
			PartPose.offsetAndRotation(0.5F, -0.2F, 0.0F, 0.091106186954104F, -0.091106186954104F, 0.0F));

		wobbly_body_base.addOrReplaceChild("right_foot", CubeListBuilder.create()
				.texOffs(14, 5).addBox(-1.5F, 0.0F, -2.0F, 2, 1, 3),
			PartPose.offsetAndRotation(-0.5F, -0.2F, 0.0F, 0.091106186954104F, 0.091106186954104F, 0.0F));

		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(RootSprite entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yRot = (float) Math.toRadians(netHeadYaw);

		float wobbleX = Mth.cos(ageInTicks / 11.0F) * 0.08F;
		float wobbleZ = Mth.cos(ageInTicks / 12.0F) * 0.08F;

		this.leftFoot.xRot = Mth.cos(limbSwing * 2F) * 1.4F * limbSwingAmount * 0.5F + 0.1F - wobbleX;
		this.rightFoot.xRot = Mth.cos(limbSwing * 2F + Mth.PI) * 1.4F * limbSwingAmount * 0.5F + 0.1F - wobbleX;

		this.leftFoot.zRot = -wobbleZ;
		this.rightFoot.zRot = -wobbleZ;

		this.head.xRot = -0.18203784098300857F + wobbleX * 2;
		this.head.zRot = 0.091106186954104F + wobbleZ * 2;

		this.bodyBase.xRot = -0.091106186954104F + wobbleX;
		this.bodyBase.zRot = wobbleZ;
	}
}
