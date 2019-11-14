package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;

public class EntityAIHurtByTargetDruid extends EntityAIHurtByTarget {
	private EntityDarkDruid druid;

	public EntityAIHurtByTargetDruid(EntityDarkDruid druid) {
		super(druid, true);
		this.druid = druid;
	}

	@Override
	protected boolean isSuitableTarget(EntityLivingBase target, boolean ignoreDisabledDamage) {
		return super.isSuitableTarget(target, ignoreDisabledDamage) && (target.onGround || target.isRiding()) && druid.getAttackCounter() == 0;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return super.shouldContinueExecuting() && (druid.getAttackCounter() != 0 || (druid.getAttackTarget() != null && (druid.getAttackTarget().onGround || druid.getAttackTarget().isRiding())));
	}
}
