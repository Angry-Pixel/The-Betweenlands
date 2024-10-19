package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.monster.DarkDruid;

public class DruidHurtByTargetGoal extends HurtByTargetGoal {

	private final DarkDruid druid;

	public DruidHurtByTargetGoal(DarkDruid druid, Class<?>... toIgnoreDamage) {
		super(druid, toIgnoreDamage);
		this.druid = druid;
	}

	@Override
	protected boolean canAttack(@Nullable LivingEntity target, TargetingConditions conditions) {
		return super.canAttack(target, conditions) && (target.onGround() || target.isPassenger()) && this.druid.getAttackCounter() == 0;
	}

	@Override
	public boolean canContinueToUse() {
		return super.canContinueToUse() && (this.druid.getAttackCounter() != 0 || (this.druid.getTarget() != null && (this.druid.getTarget().onGround() || this.druid.getTarget().isPassenger())));
	}
}
