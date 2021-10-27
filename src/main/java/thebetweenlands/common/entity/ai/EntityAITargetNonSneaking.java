package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.api.capability.IRotSmellCapability;
import thebetweenlands.common.registries.CapabilityRegistry;

public class EntityAITargetNonSneaking extends EntityAINearestAttackableTarget<EntityPlayer> {
	public EntityAITargetNonSneaking(EntityCreature entity) {
		super(entity, EntityPlayer.class, true);
		this.setMutexBits(0);
	}

	@Override
	protected boolean isSuitableTarget(EntityLivingBase target, boolean ignoreDisabledDamage) {
		if(isTargetSmelly(target))
			return super.isSuitableTarget(target, ignoreDisabledDamage);
		return super.isSuitableTarget(target, ignoreDisabledDamage) && !target.isSneaking();
	}

	private boolean isTargetSmelly(EntityLivingBase entity) {
		IRotSmellCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ROT_SMELL, null);
		if(cap != null)
			if(cap.isSmellingBad())
				return true;
		return false;
	}
}
