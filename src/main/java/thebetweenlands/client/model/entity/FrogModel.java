package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.client.model.MowzieModelBase;
import thebetweenlands.common.entity.creature.frog.Frog;

public class FrogModel extends MowzieModelBase<Frog> {

	private final ModelPart root;
	private final ModelPart torso;
	private final ModelPart head;
	private final ModelPart legfrontleft1;
	private final ModelPart legfrontright1;
	private final ModelPart legfrontleft2;
	private final ModelPart legfrontright2;
	private final ModelPart legbackleft1;
	private final ModelPart legbackright1;
	private final ModelPart legbackleft2;
	private final ModelPart legbackright2;

	public FrogModel(ModelPart root) {
		this.root = root;
		this.torso = root.getChild("torso");
		this.head = root.getChild("head");
		this.legfrontleft1 = root.getChild("left_front_leg_1");
		this.legfrontleft2 = root.getChild("left_front_leg_2");
		this.legfrontright1 = root.getChild("right_front_leg_1");
		this.legfrontright2 = root.getChild("right_front_leg_2");
		this.legbackleft1 = root.getChild("left_back_leg_1");
		this.legbackleft2 = this.legbackleft1.getChild("left_back_leg_2");
		this.legbackright1 = root.getChild("right_back_leg_1");
		this.legbackright2 = this.legbackright1.getChild("right_back_leg_2");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = new MeshDefinition();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 10).addBox(-1.5F, -1.0F, -3.0F, 3, 2, 3),
			PartPose.offsetAndRotation(0.0F, 19.0F, 0.0F, -0.07435719668865202F, 0.0F, 0.0F));
		partDefinition.addOrReplaceChild("torso", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.5F, -1.0F, 0.0F, 5, 3, 6),
			PartPose.offsetAndRotation(0.0F, 19.0F, 0.0F, -0.5948578119277954F, 0.0F, 0.0F));

		partDefinition.addOrReplaceChild("left_front_leg_1", CubeListBuilder.create()
				.texOffs(25, 0).addBox(0.0F, -1.0F, -0.5F, 1, 3, 2),
			PartPose.offsetAndRotation(2.0F, 20.0F, 0.0F, -0.07435719668865202F, 0.18589310348033902F, -0.3717860877513886F));
		partDefinition.addOrReplaceChild("left_front_leg_2", CubeListBuilder.create()
				.texOffs(25, 6).addBox(1.0F, 0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(2.0F, 20.0F, 0.0F, -0.40896469354629517F, 0.0F, 0.5948578119277954F));

		partDefinition.addOrReplaceChild("right_front_leg_1", CubeListBuilder.create()
				.texOffs(32, 0).addBox(-1.0F, -1.0F, -0.5F, 1, 3, 2),
			PartPose.offsetAndRotation(-2.0F, 20.0F, 0.0F, -0.0743509978055954F, -0.18589499592781067F, 0.37178999185562134F));
		partDefinition.addOrReplaceChild("right_front_leg_2", CubeListBuilder.create()
				.texOffs(30, 6).addBox(-2.0F, 0.5F, -0.5F, 1, 3, 1),
			PartPose.offsetAndRotation(-2.0F, 20.0F, 0.0F, -0.40896469354629517F, 0.0F, -0.5948606133460999F));

		var legbackleft1 = partDefinition.addOrReplaceChild("left_back_leg_1", CubeListBuilder.create()
				.texOffs(25, 11).addBox(-0.5F, -1.5F, -0.5F, 2, 2, 3),
			PartPose.offsetAndRotation(2.0F, 22.0F, 3.0F, -0.296705972839036F, -0.15707963267948966F, -0.19198621771937624F));
		legbackleft1.addOrReplaceChild("left_back_leg_2", CubeListBuilder.create()
				.texOffs(25, 17).addBox(-0.5F, 0.1F, -3.9F, 1, 1, 4),
			PartPose.offsetAndRotation(1.0F, 0.3F, 2.0F, 0.45378560551852565F, 0.0F, 0.19198621771937624F));

		var legbackright1 = partDefinition.addOrReplaceChild("right_back_leg_1", CubeListBuilder.create()
				.texOffs(36, 11).addBox(-1.5F, -1.5F, -0.5F, 2, 2, 3),
			PartPose.offsetAndRotation(-2.0F, 22.0F, 3.0F, -0.296705972839036F, 0.15707963267948966F, 0.19198621771937624F));
		legbackright1.addOrReplaceChild("right_back_leg_2", CubeListBuilder.create()
				.texOffs(36, 17).addBox(-0.5F, 0.1F, -3.9F, 1, 1, 4),
			PartPose.offsetAndRotation(-1.0F, 0.3F, 2.0F, 0.45378560551852565F, 0.0F, -0.19198621771937624F));

		return LayerDefinition.create(definition, 64, 32);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void setupAnim(Frog entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw / Mth.RAD_TO_DEG;
		this.head.xRot = headPitch / Mth.RAD_TO_DEG - 0.07435719668865202F + Mth.sin(ageInTicks / 8.0F) / 15.0F;
	}

	@Override
	public void prepareMobModel(Frog entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.setInitPose();
		float leapProgress = entity.prevJumpAnimationTicks + (entity.jumpAnimationTicks - entity.prevJumpAnimationTicks) * partialTick;
		float frame = entity.tickCount + partialTick;

		if (!entity.isInWater()) {
			//Idle animation
			this.torso.xRot = -0.55F - (float) (Math.sin(frame / 10.0F) + 1) / 35.0F;
			this.torso.y = 19 + (float) Math.sin(frame / 8.0F) / 15.0F;

			this.legbackleft1.y = 22;
			this.legbackright1.y = 22;

			this.head.y = 19;
			this.legfrontleft1.y = 20;
			this.legfrontleft2.y = 20;
			this.legfrontright1.y = 20;
			this.legfrontright2.y = 20;

			this.legbackleft2.xRot = 0.5F;
			this.legbackright2.xRot = 0.5F;

			if (entity.onGround()) {
				this.legbackleft1.xRot = -0.296705972839036F;
				this.legbackright1.xRot = -0.296705972839036F;
				this.legbackleft2.xRot = 0.45378560551852565F;
				this.legbackright2.xRot = 0.45378560551852565F;

				this.legfrontleft1.zRot = -0.3717860877513886F;
				this.legfrontright1.zRot = 0.37178999185562134F;
				this.legfrontleft2.zRot = 0.5948578119277954F;
				this.legfrontright2.zRot = -0.5948606133460999F;
			}
		} else {
			//Idle animation
			this.torso.xRot = -0.1F - (float) (Math.sin(frame / 10.0F) + 1) / 35.0F;

			//Water bobbing animation
			this.torso.y = 19 + (float) Math.sin(frame / 8.0F) / 2.0F;
			this.legbackleft1.y = 21 + (float) Math.sin(frame / 8.0F) / 2.0F;
			this.legbackright1.y = 21 + (float) Math.sin(frame / 8.0F) / 2.0F;
			this.head.y = 19 + (float) Math.sin(frame / 8.0F) / 2.0F;
			this.legfrontleft1.y = 20 + (float) Math.sin(frame / 8.0F) / 2.0F;
			this.legfrontleft2.y = 20 + (float) Math.sin(frame / 8.0F) / 2.0F;
			this.legfrontright1.y = 20 + (float) Math.sin(frame / 8.0F) / 2.0F;
			this.legfrontright2.y = 20 + (float) Math.sin(frame / 8.0F) / 2.0F;

			this.legbackleft2.xRot = 0.5F + 0.6F;
			this.legbackright2.xRot = 0.5F + 0.6F;
		}

		if (!entity.onGround() || entity.jumpAnimationTicks > 0) {
			if (entity.jumpAnimationTicks > 0 && entity.jumpAnimationTicks <= 7) {
				this.legbackleft1.xRot = -0.296705972839036F + 0.15F * leapProgress;
				this.legbackright1.xRot = -0.296705972839036F + 0.15F * leapProgress;
				this.legbackleft2.xRot = 0.45378560551852565F + 0.2F * leapProgress;
				this.legbackright2.xRot = 0.45378560551852565F + 0.2F * leapProgress;

				this.legfrontleft1.zRot = -0.3717860877513886F - 0.15F * leapProgress;
				this.legfrontright1.zRot = 0.37178999185562134F + 0.15F * leapProgress;
				this.legfrontleft2.zRot = 0.5948578119277954F - 0.15F * leapProgress;
				this.legfrontright2.zRot = -0.5948606133460999F + 0.15F * leapProgress;
			} else if (entity.jumpAnimationTicks > 7 && entity.jumpAnimationTicks <= 14) {
				this.legbackleft1.xRot = -0.296705972839036F + 1.05F - 0.075F * leapProgress;
				this.legbackright1.xRot = -0.296705972839036F + 1.05F - 0.075F * leapProgress;
				this.legbackleft2.xRot = 0.45378560551852565F + 1.4F - 0.1F * leapProgress;
				this.legbackright2.xRot = 0.45378560551852565F + 1.4F - 0.1F * leapProgress;

				this.legfrontleft1.zRot = -0.3717860877513886F - 1.05F + 0.075F * leapProgress;
				this.legfrontright1.zRot = 0.37178999185562134F + 1.05F - 0.075F * leapProgress;
				this.legfrontleft2.zRot = 0.5948578119277954F - 1.05F + 0.075F * leapProgress;
				this.legfrontright2.zRot = -0.5948606133460999F + 1.05F - 0.075F * leapProgress;
			}
		}
	}
}
