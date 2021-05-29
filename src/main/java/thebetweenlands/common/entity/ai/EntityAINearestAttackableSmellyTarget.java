package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import thebetweenlands.api.capability.IRotSmellCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityAINearestAttackableSmellyTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget {
	private EntityCreature taskOwner;

	public EntityAINearestAttackableSmellyTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
		super(creature, classTarget, true);
		this.taskOwner = creature;
	}

	@Override
	protected boolean isSuitableTarget(EntityLivingBase target, boolean ignoreDisabledDamage) {
		return super.isSuitableTarget(target, ignoreDisabledDamage) && isTargetSmelly(target);
	}

	private boolean isTargetSmelly(EntityLivingBase entity) {
		IRotSmellCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ROT_SMELL, null);
		if(cap != null)
			if(cap.isSmellingBad())
				return true;
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		EntityLivingBase target = taskOwner.getAttackTarget();
		return target != null && target.isEntityAlive() && isTargetSmelly(target);
	}
}
