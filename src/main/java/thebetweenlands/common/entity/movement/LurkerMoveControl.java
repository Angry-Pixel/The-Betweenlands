package thebetweenlands.common.entity.movement;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import thebetweenlands.common.entity.creature.Lurker;

public class LurkerMoveControl extends MoveControl {
	private final Lurker lurker;

	public LurkerMoveControl(Lurker lurker) {
		super(lurker);
		this.lurker = lurker;
	}

	@Override
	public void tick() {
		if (this.operation == MoveControl.Operation.MOVE_TO && !this.lurker.getNavigation().isDone()) {
			double d0 = this.getWantedX() - this.lurker.getX();
			double d1 = this.getWantedY() - this.lurker.getY();
			double d2 = this.getWantedZ() - this.lurker.getZ();
			double d3 = d0 * d0 + d1 * d1 + d2 * d2;
			d3 = Mth.sqrt((float) d3);
			d1 = d1 / d3;
			float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
			this.lurker.setYRot(this.rotlerp(lurker.getYRot(), f, 90.0F));
			this.lurker.yBodyRot = this.lurker.getYRot();
			float f1 = (float) (this.getSpeedModifier() * this.lurker.getAttributeValue(Attributes.MOVEMENT_SPEED));
			this.lurker.setSpeed(this.lurker.getSpeed() + (f1 - this.lurker.getSpeed()) * 0.125F);
			double d4 = Math.sin((double) (this.lurker.tickCount + this.lurker.getId()) * 0.5D) * 0.05D;
			double d5 = Math.cos(this.lurker.getYRot() * 0.017453292F);
			double d6 = Math.sin(this.lurker.getYRot() * 0.017453292F);
			this.lurker.setDeltaMovement(this.lurker.getDeltaMovement().add(d4 * d5, 0.0D, d4 * d6));
			d4 = Math.sin((double) (this.lurker.tickCount + this.lurker.getId()) * 0.75D) * 0.05D;
			this.lurker.setDeltaMovement(this.lurker.getDeltaMovement().add(0.0D, d4 * (d6 + d5) * 0.25D, 0.0D));
			if (Math.abs(this.lurker.getDeltaMovement().y()) < 0.35) {
				this.lurker.setDeltaMovement(this.lurker.getDeltaMovement().add(0.0D, this.lurker.getSpeed() * d1 * 0.1D * (2 + (d1 > 0 ? 0.4 : 0) + (this.lurker.horizontalCollision ? 20 : 0)), 0.0D));
			}
			LookControl control = this.lurker.getLookControl();
			double d7 = this.lurker.getX() + d0 / d3 * 2.0D;
			double d8 = (double) this.lurker.getEyeHeight() + this.lurker.getY() + d1 / d3;
			double d9 = this.lurker.getZ() + d2 / d3 * 2.0D;
			double d10 = control.getWantedX();
			double d11 = control.getWantedY();
			double d12 = control.getWantedZ();

			if (!control.isLookingAtTarget()) {
				d10 = d7;
				d11 = d8;
				d12 = d9;
			}

			this.lurker.getLookControl().setLookAt(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
		} else {
			this.lurker.setSpeed(0.0F);
		}
	}
}
