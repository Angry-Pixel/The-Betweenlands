package thebetweenlands.common.entity.movement;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import thebetweenlands.common.entity.creature.Emberling;

public class EmberlingMoveControl extends MoveControl {
	private final Emberling emberling;

	public EmberlingMoveControl(Emberling emberling) {
		super(emberling);
		this.emberling = emberling;
	}

	@Override
	public void tick() {
		if (this.operation == MoveControl.Operation.MOVE_TO && !this.emberling.getNavigation().isDone()) {
			double d0 = this.getWantedX() - this.emberling.getX();
			double d1 = this.getWantedY() - this.emberling.getY();
			double d2 = this.getWantedZ() - this.emberling.getZ();
			double d3 = d0 * d0 + d1 * d1 + d2 * d2;
			d3 = Mth.sqrt((float) d3);
			d1 = d1 / d3;
			float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
			this.emberling.setYRot(this.rotlerp(this.emberling.getYRot(), f, 90.0F));
			this.emberling.yBodyRot = this.emberling.getYRot();
			float f1 = (float) (this.getSpeedModifier() * this.emberling.getAttributeValue(Attributes.MOVEMENT_SPEED));
			this.emberling.setSpeed(this.emberling.getSpeed() + (f1 - this.emberling.getSpeed()) * 0.125F);
			double d4 = Math.sin((double) (this.emberling.tickCount + emberling.getId()) * 0.5D) * 0.05D;
			double d5 = Math.cos(this.emberling.getYRot() * 0.017453292F);
			double d6 = Math.sin(this.emberling.getYRot() * 0.017453292F);
			this.emberling.setDeltaMovement(this.emberling.getDeltaMovement().add(d4 * d5, 0.0D, d4 * d6));
			d4 = Math.sin((double) (this.emberling.tickCount + this.emberling.getId()) * 0.75D) * 0.05D;
			this.emberling.setDeltaMovement(this.emberling.getDeltaMovement().add(0.0D, d4 * (d6 + d5) * 0.25D, 0.0D));
			if (Math.abs(this.emberling.getDeltaMovement().y()) < 0.35) {
				this.emberling.setDeltaMovement(this.emberling.getDeltaMovement().add(0.0D, this.emberling.getSpeed() * d1 * 0.1D * (2 + (d1 > 0 ? 0.4 : 0) + (this.emberling.horizontalCollision ? 20 : 0)), 0.0D));
			}
			LookControl control = this.emberling.getLookControl();
			double d7 = this.emberling.getX() + d0 / d3 * 2.0D;
			double d8 = (double) this.emberling.getEyeHeight() + this.emberling.getY() + d1 / d3;
			double d9 = this.emberling.getZ() + d2 / d3 * 2.0D;
			double d10 = control.getWantedX();
			double d11 = control.getWantedY();
			double d12 = control.getWantedZ();

			if (!control.isLookingAtTarget()) {
				d10 = d7;
				d11 = d8;
				d12 = d9;
			}

			this.emberling.getLookControl().setLookAt(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
		} else {
			this.emberling.setSpeed(0.0F);
		}
	}
}
