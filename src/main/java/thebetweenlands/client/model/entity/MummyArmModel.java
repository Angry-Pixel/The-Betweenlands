package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.monster.MummyArm;

public class MummyArmModel extends MowzieModelBase<MummyArm> {

	private final ModelPart root;
	private final ModelPart arm;
	private final ModelPart arm2;

	public MummyArmModel(ModelPart root) {
		this.root = root;
		this.arm = root.getChild("arm_1");
		this.arm2 = this.arm.getChild("arm_2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		var arm = partDefinition.addOrReplaceChild("arm_1", CubeListBuilder.create()
				.texOffs(19, 32)
				.addBox(0.0F, -1.0F, -1.5F, 2, 10, 2),
			PartPose.rotation(0.13735741213195374F, 0.7311184236604247F, -0.4316199240181977F));

		arm.addOrReplaceChild("arm_2", CubeListBuilder.create()
				.texOffs(28, 32)
				.addBox(0.0F, 0.0F, -1.5F, 2, 10, 2),
			PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, -0.5918411493512771F, 0.0F, 0.0F));

		return LayerDefinition.create(definition, 128, 64);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(MummyArm entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void prepareMobModel(MummyArm entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();

		this.arm.yRot = (float) Math.toRadians(limbSwing);
		this.arm.zRot = -0.1F;
		float offset = 0.0F;
		if (entity.attackSwing > 0) {
			this.walk(this.arm, 1.0F, -0.4F, false, offset, 0.0F, (Mth.abs(entity.attackSwing - 10.0F) / 2.0F + Math.signum(entity.attackSwing - 10.0F) * -1.0F * partialTick / 2.0F), 1.0F);
			this.walk(this.arm2, 1.0F, -0.6F, false, offset, 0.0F, (Mth.abs(entity.attackSwing - 10.0F) / 2.0F + Math.signum(entity.attackSwing - 10.0F) * -1.0F * partialTick / 2.0F), 1.0F);
		}
		if (entity.hurtTime > 0) {
			this.bob(this.arm, 0.5F, -10.0F, true, (entity.hurtTime - partialTick) / 5.0F, 1.0F);
		}
		this.swing(this.arm, 1.0F, 0.4F, false, offset, 0.0F, (entity.tickCount + partialTick) / 10.0F, 1.0F);
		this.walk(this.arm2, 1.0F, 0.4F, false, offset, 0.0F, (entity.tickCount + partialTick) / 8.0F, 1.0F);
		this.swing(this.arm2, 1.0F, 0.1F, false, offset, 0.0F, (entity.tickCount + partialTick) / 4.0F, 1.0F);
		this.walk(this.arm, 1.0F, 0.1F, false, offset, 0.0F, (entity.tickCount + partialTick) / 10.0F, 1.0F);
		this.walk(this.arm, 2.0F, 0.3F, false, offset, 0.0F, (entity.tickCount + partialTick) / 15.0F, 1.0F);
		this.bob(this.arm, 2.0F, 1.8F, false, (entity.tickCount + partialTick) / 15.0F, 1.0F);
	}
}
