package thebetweenlands.client.extensions.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class BigSwingExtension implements IClientItemExtensions {

	public static BigSwingExtension INSTANCE = new BigSwingExtension();

	@Override
	public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm, ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
		float drive = swingProcess;

		float driveScale = 0.05f;
		float drivePow = 2f;

		drive = (float) (1 - Math.pow((1 - drive) * driveScale, drivePow) / Math.pow(driveScale, drivePow));

		float xOff = -0.65f;
		float yOff = 1f;
		float zOff = 0.85f;

		float leftMove = Mth.sin(drive * Mth.PI);

		float roll = Mth.sin(Math.min(drive * Mth.PI * 2, Mth.HALF_PI));
		float roll2 = (drive > 0.75F ? (float) Math.pow(Mth.sin((drive - 0.75F) * Mth.PI * 2), 3) : 0);
		float yaw = Mth.sin(drive * Mth.PI);

		pose.translate(leftMove * -1.2f, leftMove * 0.7f - equipProcess * 0.2f, 0.0D);

		pose.translate(-xOff, -yOff, -zOff);
		pose.mulPose(Axis.ZP.rotationDegrees(roll * -90));
		pose.mulPose(Axis.XP.rotationDegrees(yaw * -190));
		pose.mulPose(Axis.ZP.rotationDegrees(roll2 * 90));
		pose.translate(xOff, yOff, zOff);

		//apply normal hand transform
		int i = arm == HumanoidArm.RIGHT ? 1 : -1;
		pose.translate((float)i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);

		return true;
	}
}
