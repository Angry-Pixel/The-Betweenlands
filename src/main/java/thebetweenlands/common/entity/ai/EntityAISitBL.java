package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.passive.EntityTameable;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;

/**
 * Same as {@link EntityAISit} but setting mutex to not look at players
 */
public class EntityAISitBL extends EntityAISit {
	private final EntityTameable tameable;
	private boolean isSitting;

	public EntityAISitBL(EntityTameable entity) {
		super(entity);
		tameable = entity;
		setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		if (!tameable.isTamed())
			return false;
		else if (tameable.isInWater())
			return false;
		else if (!tameable.onGround && !(tameable instanceof EntityChiromawTame))
			return false;
		else {
			EntityLivingBase entitylivingbase = tameable.getOwner();
			if (entitylivingbase == null)
				return true;
			else
				return tameable.getDistanceSq(entitylivingbase) < 144.0D && entitylivingbase.getRevengeTarget() != null ? false : isSitting;
		}
	}

	@Override
	public void startExecuting() {
		tameable.getNavigator().clearPath();
		tameable.setSitting(true);
	}

	@Override
	public void resetTask() {
		tameable.setSitting(false);
	}

	@Override
	public void setSitting(boolean sitting) {
		isSitting = sitting;
	}
}