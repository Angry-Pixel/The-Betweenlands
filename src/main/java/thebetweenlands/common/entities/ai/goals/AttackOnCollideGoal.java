package thebetweenlands.common.entities.ai.goals;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class AttackOnCollideGoal extends Goal {

	protected Mob attacker;
	protected int attackTick;

	public AttackOnCollideGoal(Mob entity) {
		this.attacker = entity;
	}

	@Override
	public boolean canUse() {
		return this.attacker.getTarget() != null && this.attacker.getTarget().isAlive();
	}

	@Override
	public void tick() {
		LivingEntity target = this.attacker.getTarget();
		if(target != null) {
			this.attacker.lookAt(target, 30.0F, 30.0F);
			double distSq = this.attacker.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
			this.attackTick = Math.max(this.attackTick - 1, 0);
			this.attackEntity(target, distSq);
		}
	}

	protected void attackEntity(LivingEntity target, double distSq) {
		if (this.attacker.isWithinMeleeAttackRange(target) && this.attackTick <= 0) {
			this.attackTick = 20;
			this.attacker.swing(InteractionHand.MAIN_HAND);
			this.attacker.doHurtTarget(target);
		}
	}
}
