package thebetweenlands.common.entity.movement;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import thebetweenlands.common.entity.creature.Jellyfish;

public class JellyfishMoveControl extends MoveControl {

	private final Jellyfish jellyfish;

	public JellyfishMoveControl(Jellyfish jellyfish) {
		super(jellyfish);
		this.jellyfish = jellyfish;
	}

	@Override
	public void tick() {
		if (this.operation == MoveControl.Operation.MOVE_TO && !this.jellyfish.getNavigation().isDone()) {
			double targetX = this.getWantedX() - this.jellyfish.getX();
			double targetY = this.getWantedY() - this.jellyfish.getY();
			double targetZ = this.getWantedZ() - this.jellyfish.getZ();
			double targetDistance = targetX * targetX + targetY * targetY + targetZ * targetZ;
			targetDistance = Mth.sqrt((float) targetDistance);
			targetY = targetY / targetDistance;
			float targetAngle = (float) (Mth.atan2(targetZ, targetX) * (180D / Math.PI)) - 90.0F;
			this.jellyfish.setYRot(this.rotlerp(this.jellyfish.getYRot(), targetAngle, 180.0F));
			this.jellyfish.yBodyRot = this.jellyfish.getYRot();
			float travelSpeed = (float) (this.getSpeedModifier() * this.jellyfish.getAttributeValue(Attributes.MOVEMENT_SPEED));
			this.jellyfish.setSpeed(this.jellyfish.getSpeed() + (travelSpeed - this.jellyfish.getSpeed()) * 0.125F);
			this.jellyfish.setDeltaMovement(this.jellyfish.getDeltaMovement().add(0.0D, this.jellyfish.getSpeed() * targetY * 0.2D, 0.0D));
			LookControl control = this.jellyfish.getLookControl();
			double targetDirectionX = this.jellyfish.getX() + targetX / targetDistance * 2.0D;
			double targetDirectionY = (double) this.jellyfish.getEyeHeight() + this.jellyfish.getY() + targetY / targetDistance;
			double targetDirectionZ = this.jellyfish.getZ() + targetZ / targetDistance * 2.0D;
			double lookX = control.getWantedX();
			double lookY = control.getWantedY();
			double lookZ = control.getWantedZ();

			if (!control.isLookingAtTarget()) {
				lookX = targetDirectionX;
				lookY = targetDirectionY;
				lookZ = targetDirectionZ;
			}

			this.jellyfish.getLookControl().setLookAt(lookX + (targetDirectionX - lookX) * 0.125D, lookY + (targetDirectionY - lookY) * 0.125D, lookZ + (targetDirectionZ - lookZ) * 0.125D, 10.0F, 40.0F);

		} else {
			this.jellyfish.setSpeed(0.0F);
		}
	}
}
