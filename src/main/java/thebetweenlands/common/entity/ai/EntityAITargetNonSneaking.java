package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAITargetNonSneaking extends EntityAINearestAttackableTarget<EntityPlayer> {
	public EntityAITargetNonSneaking(EntityCreature entity) {
		super(entity, EntityPlayer.class, true);
		this.setMutexBits(0);
	}

	@Override
	protected boolean isSuitableTarget(EntityLivingBase target, boolean ignoreDisabledDamage) {
		return super.isSuitableTarget(target, ignoreDisabledDamage) && !target.isSneaking();
	}
}
