package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.monster.DarkDruid;

public class DruidNearestAttackableTargetGoal extends NearestAttackableTargetGoal<Player> {

	private final DarkDruid druid;

	public DruidNearestAttackableTargetGoal(DarkDruid druid) {
		super(druid, Player.class, true);
		this.druid = druid;
	}

	@Override
	protected boolean canAttack(@Nullable LivingEntity target, TargetingConditions conditions) {
		return super.canAttack(target, conditions) && (target.onGround() || target.isPassenger()) && this.druid.getAttackCounter() == 0;
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity target = druid.getTarget();
		return target != null && target.isAlive() && (this.druid.getAttackCounter() != 0 || target.onGround() || target.isPassenger());
	}

	@Override
	protected double getFollowDistance() {
		return 7.0D;
	}
}
