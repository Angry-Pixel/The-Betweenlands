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
		return super.isSuitableTarget(target, ignoreDisabledDamage) && target.onGround && druid.getAttackCounter() == 0;
	}

	@Override
	public boolean continueExecuting() {
		return super.continueExecuting() && (druid.getAttackCounter() != 0 || druid.getAttackTarget().onGround);
	}
}
