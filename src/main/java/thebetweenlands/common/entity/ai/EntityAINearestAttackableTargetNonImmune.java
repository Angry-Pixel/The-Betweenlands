package thebetweenlands.common.entity.ai;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import thebetweenlands.api.capability.IInfestationIgnoreCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityAINearestAttackableTargetNonImmune<T extends EntityLivingBase> extends EntityAINearestAttackableTarget {
	private EntityCreature taskOwner;

	public EntityAINearestAttackableTargetNonImmune(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate <? super T > targetSelector) {
		super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
		this.taskOwner = creature;
	}

	@Override
	protected boolean isSuitableTarget(EntityLivingBase target, boolean ignoreDisabledDamage) {
		return super.isSuitableTarget(target, ignoreDisabledDamage) && !isTargetImmune(target);
	}

	private boolean isTargetImmune(EntityLivingBase entity) {
		IInfestationIgnoreCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_INFESTATION_IGNORE, null);
		if(cap != null)
			if(cap.isImmune())
				return true;
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		EntityLivingBase target = taskOwner.getAttackTarget();
		return target != null && target.isEntityAlive() && !isTargetImmune(target);
	}
}
