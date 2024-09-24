package thebetweenlands.common.entity.fishing.anadia;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;

public class AnadiaMoveControl extends MoveControl {
	private final Anadia anadia;

	public AnadiaMoveControl(Anadia anadia) {
		super(anadia);
		this.anadia = anadia;
	}

	@Override
	public void tick() {
		if (this.anadia.getStaminaTicks() <= 0 || this.anadia.getNettableTimer() > 0) {
			this.operation = MoveControl.Operation.WAIT;
			this.anadia.setSpeed(0);
			return;
		}

		if (this.operation == MoveControl.Operation.MOVE_TO && !this.anadia.getNavigation().isDone()) {
			double targetX = this.wantedX - this.anadia.getX();
			double targetY = this.wantedY - this.anadia.getY();
			double targetZ = this.wantedZ - this.anadia.getZ();
			double targetDistance = targetX * targetX + targetY * targetY + targetZ * targetZ;
			targetDistance = Mth.sqrt((float) targetDistance);
			targetY = targetY / targetDistance;
			float targetAngle = (float) (Mth.atan2(targetZ, targetX) * (180D / Math.PI)) - 90.0F;
			this.anadia.setYRot(this.rotlerp(this.anadia.getYRot(), targetAngle, 90.0F));
			this.anadia.setYBodyRot(this.anadia.getYRot());
			float travelSpeed = (float) (this.speedModifier * this.anadia.getAttributeValue(Attributes.MOVEMENT_SPEED));
			this.anadia.setSpeed(this.anadia.getSpeed() + (travelSpeed - this.anadia.getSpeed()) * 0.125F);
			double wiggleSpeed = Math.sin((double) (this.anadia.tickCount + this.anadia.getId()) * 0.5D) * this.anadia.getFishSize() * 0.05D;
			double wiggleOffsetX = Math.cos(this.anadia.getYRot() * this.anadia.getFishSize() * 0.01F);
			double wiggleOffsetZ = Math.sin(this.anadia.getYRot() * this.anadia.getFishSize() * 0.01F);
			this.anadia.setDeltaMovement(this.anadia.getDeltaMovement().add(wiggleSpeed * wiggleOffsetX, 0.0D, wiggleSpeed * wiggleOffsetZ));
			wiggleSpeed = Math.sin((double) (this.anadia.tickCount + this.anadia.getId()) * 0.75D) * 0.05D;
			this.anadia.setDeltaMovement(this.anadia.getDeltaMovement().add(0.0D, wiggleSpeed * (wiggleOffsetZ + wiggleOffsetX) * 0.25D, 0.0D));
			this.anadia.setDeltaMovement(this.anadia.getDeltaMovement().add(0.0D, this.anadia.getSpeed() * targetY * 0.1D, 0.0D));
			LookControl entitylookhelper = this.anadia.getLookControl();
			double targetDirectionX = this.anadia.getX() + targetX / targetDistance * 2.0D;
			double targetDirectionY = (double) this.anadia.getEyeHeight() + this.anadia.getY() + targetY / targetDistance;
			double targetDirectionZ = this.anadia.getZ() + targetZ / targetDistance * 2.0D;
			double lookX = entitylookhelper.getWantedX();
			double lookY = entitylookhelper.getWantedY();
			double lookZ = entitylookhelper.getWantedZ();

			if (!entitylookhelper.isLookingAtTarget()) {
				lookX = targetDirectionX;
				lookY = targetDirectionY;
				lookZ = targetDirectionZ;
			}

			this.anadia.getLookControl().setLookAt(lookX + (targetDirectionX - lookX) * 0.125D, lookY + (targetDirectionY - lookY) * 0.125D, lookZ + (targetDirectionZ - lookZ) * 0.125D, 10.0F, 40.0F);
		} else {
			this.anadia.setSpeed(0.0F);
		}
	}
}
