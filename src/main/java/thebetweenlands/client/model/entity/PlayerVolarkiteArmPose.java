package thebetweenlands.client.model.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

public class PlayerVolarkiteArmPose {

	public static final EnumProxy<HumanoidModel.ArmPose> THEBETWEENLANDS_VOLARKITE_Y_POSE = new EnumProxy<>(HumanoidModel.ArmPose.class, true, (IArmPoseTransformer) PlayerVolarkiteArmPose::applyYPose);

	private static void applyYPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
		model.leftArm.yRot = (float) Math.PI;
		model.leftArm.xRot = 0.0F;
		model.leftArm.zRot = -2.7F;
		model.leftArm.y = 1.0F;
		model.leftArm.x = 4.0F;

		model.rightArm.yRot = (float) Math.PI;
		model.rightArm.xRot = 0.0F;
		model.rightArm.zRot = 2.7F;
		model.rightArm.y = 1.0F;
		model.rightArm.x = -4.0F;
	}
}
