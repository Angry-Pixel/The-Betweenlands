package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.DarkDruid;

public class DarkDruidModel extends MowzieModelBase<DarkDruid> {

	private final ModelPart root;
	private final ModelPart leftArm;
	private final ModelPart rightArm;

	public DarkDruidModel(ModelPart root) {
		this.root = root;
		this.leftArm = root.getChild("left_arm");
		this.rightArm = root.getChild("right_arm");
	}

	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
			.texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 9.0F)
			.texOffs(0, 0).addBox(-1.5F, -1.0F, -4.5F, 3.0F, 2.0F, 1.0F)
			.texOffs(9, 4).addBox(-2.0F, -9.0F, -3.5F, 4.0F, 1.0F, 1.0F),
			PartPose.offset(0.0F, 2.0F, 0.0F));

		head.addOrReplaceChild("left_hood", CubeListBuilder.create()
				.texOffs(44, 40).addBox(-5.0F, -8.0F, -4.5F, 1.0F, 11.0F, 9.0F),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1222F));

		head.addOrReplaceChild("right_hood", CubeListBuilder.create().mirror()
				.texOffs(44, 40).addBox(4.0F, -8.0F, -4.5F, 1.0F, 11.0F, 9.0F),
			PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1222F));

		head.addOrReplaceChild("left_hood_top", CubeListBuilder.create().mirror()
				.texOffs(74, 54).addBox(-4.5F, -15.0F, -4.5F, 4.0F, 1.0F, 9.0F),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, 0.2967F));

		head.addOrReplaceChild("right_hood_top", CubeListBuilder.create()
				.texOffs(74, 54).addBox(0.5F, -15.0F, -4.5F, 4.0F, 1.0F, 9.0F),
			PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, 0.0F, 0.0F, -0.2967F));

		PartDefinition liripipe = head.addOrReplaceChild("liripipe", CubeListBuilder.create()
				.texOffs(114, 0).addBox(-3.0F, -0.5F, -0.5F, 6.0F, 8.0F, 1.0F),
			PartPose.offsetAndRotation(0.0F, -8.0F, 4.5F, 0.1745F, 0.0F, 0.0F));

		liripipe.addOrReplaceChild("liripipe2", CubeListBuilder.create()
				.texOffs(118, 9).addBox(-2.0F, -0.5F, -0.5F, 4.0F, 12.0F, 1.0F)
				.texOffs(122, 22).addBox(-1.0F, 11.5F, -0.5F, 2.0F, 6.0F, 1.0F),
			PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, -0.1745F, 0.0F, 0.0F));

		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(36, 0).addBox(-5.0F, -2.0F, -2.0F, 5.0F, 12.0F, 5.0F)
				.texOffs(104, 56).addBox(-5.5F, 10.0F, -2.5F, 6.0F, 2.0F, 6.0F),
			PartPose.offset(-3.0F, 4.0F, 0.0F));

		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().mirror()
				.texOffs(36, 0).addBox(0.0F, -2.0F, -2.0F, 5.0F, 12.0F, 5.0F)
				.texOffs(104, 56).addBox(-0.5F, 10.0F, -2.5F, 6.0F, 2.0F, 6.0F),
			PartPose.offset(3.0F, 4.0F, 0.0F));

		partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 13.0F, 8.0F)
				.texOffs(70, 0).addBox(-4.0F, 13.0F, -3.0F, 8.0F, 5.0F, 8.0F)
				.texOffs(70, 13).addBox(-4.5F, 18.0F, -3.5F, 9.0F, 2.0F, 10.0F)
				.texOffs(78, 31).addBox(-5.0F, 20.0F, -4.0F, 10.0F, 1.0F, 15.0F)
				.texOffs(0, 45).addBox(-5.5F, 21.0F, -4.5F, 11.0F, 1.0F, 18.0F)
				.texOffs(70, 13).addBox(-3.0F, 19.0F, 6.5F, 6.0F, 1.0F, 2.0F)
				.texOffs(70, 25).addBox(-3.0F, 20.0F, 11.0F, 6.0F, 1.0F, 1.0F)
				.texOffs(0, 40).addBox(-4.0F, 21.0F, 13.5F, 8.0F, 1.0F, 2.0F),
			PartPose.offset(0.0F, 2.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(DarkDruid entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void prepareMobModel(DarkDruid entity, float limbSwing, float limbSwingAmount, float partialTick) {
		float attackAnimationTime = entity.getAttackAnimationTime(partialTick);
		float pitch = -Mth.HALF_PI + Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) * Mth.DEG_TO_RAD;
		float ticksExisted = entity.tickCount + partialTick;
		this.rightArm.xRot = pitch * attackAnimationTime + Mth.cos(limbSwing * 0.6662F + Mth.PI) * 2 * limbSwingAmount * 0.5F * (1 - attackAnimationTime);
		this.leftArm.xRot = pitch * attackAnimationTime + Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F * (1 - attackAnimationTime);
		this.rightArm.xRot += Mth.sin(ticksExisted * 0.4F) * 0.1F * attackAnimationTime;
		this.leftArm.xRot += Mth.cos(ticksExisted * 0.4F) * 0.1F * attackAnimationTime;
		this.rightArm.zRot = -Mth.sin(ticksExisted * 0.2F) * 0.1F * attackAnimationTime;
		this.leftArm.zRot = Mth.cos(ticksExisted * 0.2F) * 0.1F * attackAnimationTime;
	}
}