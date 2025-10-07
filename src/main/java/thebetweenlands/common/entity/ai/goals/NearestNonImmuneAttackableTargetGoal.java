package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.component.entity.InfestationIgnoreData;
import thebetweenlands.common.registries.AttachmentRegistry;

import java.util.function.Predicate;

public class NearestNonImmuneAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

	public NearestNonImmuneAttackableTargetGoal(Mob mob, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach) {
		super(mob, targetType, randomInterval, mustSee, mustReach, null);
	}

	@Override
	protected boolean canAttack(@Nullable LivingEntity target, TargetingConditions conditions) {
		return super.canAttack(target, conditions) && !this.isTargetImmune(target);
	}

	private boolean isTargetImmune(LivingEntity entity) {
		if (entity instanceof Player player) {
			return entity.getData(AttachmentRegistry.INFESTATION_IGNORE).isImmune(player);
		}
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity target = this.mob.getTarget();
		return target != null && target.isAlive() && !this.isTargetImmune(target);
	}
}
