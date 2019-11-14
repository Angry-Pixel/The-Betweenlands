package thebetweenlands.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;

public class EntityAINearestAttackableTargetDruid extends EntityAINearestAttackableTarget<EntityPlayer> {
	private EntityDarkDruid druid;

	public EntityAINearestAttackableTargetDruid(EntityDarkDruid druid) {
		super(druid, EntityPlayer.class, true);
		this.druid = druid;
	}

	@Override
	protected boolean isSuitableTarget(EntityLivingBase target, boolean ignoreDisabledDamage) {
		return super.isSuitableTarget(target, ignoreDisabledDamage) && (target.onGround || target.isRiding()) && druid.getAttackCounter() == 0;
	}

	@Override
	public boolean shouldContinueExecuting() {
		Entity target = druid.getAttackTarget();
		return target != null && target.isEntityAlive() && (druid.getAttackCounter() != 0 || target.onGround || target.isRiding());
	}

	@Override
	protected double getTargetDistance() {
		return 7;
	}
}
