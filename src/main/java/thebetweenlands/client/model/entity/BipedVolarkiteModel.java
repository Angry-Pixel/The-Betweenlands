package thebetweenlands.client.model.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public class BipedVolarkiteModel<T extends LivingEntity> extends HumanoidModel<T> {

	public BipedVolarkiteModel(ModelPart root) {
		super(root);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		this.leftArm.yRot = (float) Math.PI;
		this.leftArm.xRot = -0.01F;
		this.leftArm.zRot = -2.7F;
		this.leftArm.y = 1.0F;
		this.leftArm.x = 4.0F;

		this.rightArm.yRot = (float) Math.PI;
		this.rightArm.xRot = -0.01F;
		this.rightArm.zRot = 2.7F;
		this.rightArm.y = 1.0F;
		this.rightArm.x = -4.0F;
	}
}
