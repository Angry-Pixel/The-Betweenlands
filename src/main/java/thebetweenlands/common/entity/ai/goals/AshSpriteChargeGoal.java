package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.monster.AshSprite;

import java.util.EnumSet;

public class AshSpriteChargeGoal extends Goal {

	private final AshSprite ashSprite;

	public AshSpriteChargeGoal(AshSprite sprite) {
		this.setFlags(EnumSet.of(Flag.MOVE));
		this.ashSprite = sprite;
	}

	@Override
	public boolean canUse() {
		if (this.ashSprite.getTarget() != null && !this.ashSprite.getMoveControl().hasWanted() && this.ashSprite.getRandom().nextInt(reducedTickDelay(7)) == 0)
			return this.ashSprite.distanceToSqr(this.ashSprite.getTarget()) > 4.0D;
		else
			return false;
	}

	@Override
	public boolean canContinueToUse() {
		return this.ashSprite.getMoveControl().hasWanted() && this.ashSprite.isCharging() && this.ashSprite.getTarget() != null && this.ashSprite.getTarget().isAlive();
	}

	@Override
	public void start() {
		LivingEntity target = this.ashSprite.getTarget();
		if (target != null) {
			Vec3 vec3d = target.getEyePosition();
			this.ashSprite.getMoveControl().setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0D);
			this.ashSprite.setCharging(true);
		}
	}

	@Override
	public void stop() {
		this.ashSprite.setCharging(false);
	}

	@Override
	public void tick() {
		LivingEntity target = this.ashSprite.getTarget();

		if (target != null) {
			if (this.ashSprite.getBoundingBox().intersects(target.getBoundingBox())) {
				this.ashSprite.doHurtTarget(target);
				this.ashSprite.setCharging(false);
			} else {
				double d0 = this.ashSprite.distanceToSqr(target);
				if (d0 < 9.0D) {
					Vec3 vec3d = target.getEyePosition();
					this.ashSprite.getMoveControl().setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0D);
				}
			}
		}
	}
}
