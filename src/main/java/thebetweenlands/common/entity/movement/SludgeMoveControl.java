package thebetweenlands.common.entity.movement;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import thebetweenlands.common.entity.monster.Sludge;

//copy of Slime.SlimeMoveControl, modified to use sludges
public class SludgeMoveControl extends MoveControl {
	private float yRot;
	private int jumpDelay;
	private final Sludge sludge;
	private boolean isAggressive;

	public SludgeMoveControl(Sludge sludge) {
		super(sludge);
		this.sludge = sludge;
		this.yRot = 180.0F * sludge.getYRot() / Mth.PI;
	}

	public void setDirection(float yRot, boolean aggressive) {
		this.yRot = yRot;
		this.isAggressive = aggressive;
	}

	public void setWantedMovement(double speed) {
		this.speedModifier = speed;
		this.operation = MoveControl.Operation.MOVE_TO;
	}

	@Override
	public void tick() {
		this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
		this.mob.yHeadRot = this.mob.getYRot();
		this.mob.yBodyRot = this.mob.getYRot();
		if (this.operation != MoveControl.Operation.MOVE_TO) {
			this.mob.setZza(0.0F);
		} else {
			this.operation = MoveControl.Operation.WAIT;
			if (this.mob.onGround()) {
				this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
				if (this.jumpDelay-- <= 0) {
					this.jumpDelay = this.sludge.getJumpDelay();
					if (this.isAggressive) {
						this.jumpDelay /= 3;
					}

					this.sludge.getJumpControl().jump();
					if (this.sludge.makesSoundOnJump()) {
						this.sludge.playSound(this.sludge.getJumpSound(), 1.0F, ((this.sludge.getRandom().nextFloat() - this.sludge.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F);
					}
				} else {
					this.sludge.xxa = 0.0F;
					this.sludge.zza = 0.0F;
					this.mob.setSpeed(0.0F);
				}
			} else {
				this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
			}
		}
	}
}
