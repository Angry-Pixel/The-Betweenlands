package thebetweenlands.common.entity.movement;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.monster.AshSprite;

public class AshSpriteMoveControl extends MoveControl {

	private final AshSprite ashSprite;

	public AshSpriteMoveControl(AshSprite sprite) {
		super(sprite);
		this.ashSprite = sprite;
	}

	@Override
	public void tick() {
		if (this.operation == MoveControl.Operation.MOVE_TO) {
			Vec3 vec3 = new Vec3(this.wantedX - this.ashSprite.getX(), this.wantedY - this.ashSprite.getY(), this.wantedZ - this.ashSprite.getZ());
			double d0 = vec3.length();
			if (d0 < this.ashSprite.getBoundingBox().getSize()) {
				this.operation = MoveControl.Operation.WAIT;
				this.ashSprite.setDeltaMovement(this.ashSprite.getDeltaMovement().scale(0.5));
			} else {
				this.ashSprite.setDeltaMovement(this.ashSprite.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05 / d0)));
				if (this.ashSprite.getTarget() == null) {
					Vec3 vec31 = this.ashSprite.getDeltaMovement();
					this.ashSprite.setYRot(-((float)Mth.atan2(vec31.x, vec31.z)) * Mth.RAD_TO_DEG);
					this.ashSprite.yBodyRot = this.ashSprite.getYRot();
				} else {
					double d2 = this.ashSprite.getTarget().getX() - this.ashSprite.getX();
					double d1 = this.ashSprite.getTarget().getZ() - this.ashSprite.getZ();
					this.ashSprite.setYRot(-((float)Mth.atan2(d2, d1)) * Mth.RAD_TO_DEG);
					this.ashSprite.yBodyRot = this.ashSprite.getYRot();
				}
			}
		}
	}
}
