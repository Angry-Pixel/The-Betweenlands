package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.registries.AttachmentRegistry;

public class NearestSmellyAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
	public NearestSmellyAttackableTargetGoal(Mob mob, Class<T> targetType, boolean mustSee) {
		super(mob, targetType, mustSee);
	}

	@Override
	protected boolean canAttack(@Nullable LivingEntity potentialTarget, TargetingConditions targetPredicate) {
		return super.canAttack(potentialTarget, targetPredicate) && this.isTargetSmelly(potentialTarget);
	}

	private boolean isTargetSmelly(@Nullable LivingEntity entity) {
		return entity != null && entity.hasData(AttachmentRegistry.ROT_SMELL) && entity.getData(AttachmentRegistry.ROT_SMELL).isSmellingBad(entity);
	}
}
