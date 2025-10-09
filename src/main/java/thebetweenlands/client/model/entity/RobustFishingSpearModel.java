package thebetweenlands.client.model.entity;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import thebetweenlands.common.entity.projectile.spear.RobustFishingSpear;

public class RobustFishingSpearModel extends FishingSpearModel<RobustFishingSpear> {

	private final ModelPart leftFin;
	private final ModelPart rightFin;
	private final ModelPart shaftTail1;
	private final ModelPart shaftTail2;
	private final ModelPart tail;

	public RobustFishingSpearModel(ModelPart root) {
		super(root);
		var shaft = root.getChild("shaft");
		this.leftFin = shaft.getChild("left_fin");
		this.rightFin = shaft.getChild("right_fin");
		this.shaftTail1 = shaft.getChild("shaft_tail_1");
		this.shaftTail2 = this.shaftTail1.getChild("shaft_tail_2");
		this.tail = this.shaftTail2.getChild("tail");
	}

	public static LayerDefinition create() {
		MeshDefinition definition = createBasicSpear();
		PartDefinition partDefinition = definition.getRoot();

		partDefinition.addOrReplaceChild("tip", CubeListBuilder.create()
				.texOffs(14, 4).addBox(0.0F, -16.0F, -2.0F, 0, 8, 4),
			PartPose.offset(0.0F, 8.0F, 0.0F));

		addFins(partDefinition);

		return LayerDefinition.create(definition, 32, 32);
	}

	@Override
	public void setupAnim(RobustFishingSpear entity, float limbSwing, float limbSwingAmount, float ageInTicks, float partialTick, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, partialTick, netHeadYaw, headPitch);
		float flap = Mth.sin(ageInTicks * 0.5F) * 0.6F;

		if (entity.isAnimated() && entity.clientReturnTick > 0) {
			this.rightFin.yRot = -0.7853981633974483F + flap * 0.5F;
			this.leftFin.yRot = 0.7853981633974483F - flap * 0.5F;
			this.rightFin.zRot = -flap * 0.5F;
			this.leftFin.zRot = flap * 0.5F;
			this.shaftTail1.zRot = flap * 0.25F;
			this.shaftTail2.zRot = flap * 0.25F;
			this.tail.zRot = flap * 0.25F;
		}
	}
}
