package thebetweenlands.common.entity.ai;

import net.minecraft.entity.ai.EntityAIAttackMelee;
import thebetweenlands.common.entity.mobs.EntityWight;

public class EntityAIWightAttack extends EntityAIAttackMelee {
	private final EntityWight wight;

	public EntityAIWightAttack(EntityWight wight, double speedIn, boolean useLongMemory) {
		super(wight, speedIn, useLongMemory);
		this.wight = wight;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && !this.wight.isHiding() && !this.wight.isVolatile();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return super.shouldContinueExecuting() && !this.wight.isHiding() && !this.wight.isVolatile();
	}
}
