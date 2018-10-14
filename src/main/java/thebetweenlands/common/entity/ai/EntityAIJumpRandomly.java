package thebetweenlands.common.entity.ai;

import java.util.function.Supplier;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIJumpRandomly extends EntityAIBase {
	private final EntityLiving taskOwner;
	private int chance;
	private Supplier<Boolean> condition;

	public EntityAIJumpRandomly(EntityLiving taskOwner, int chance, Supplier<Boolean> condition) {
		this.taskOwner = taskOwner;
		this.chance = chance;
		this.condition = condition;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return this.taskOwner.isEntityAlive() && this.taskOwner.getRNG().nextInt(this.chance) == 0 && this.condition.get();
	}

	@Override
	public void startExecuting() {
		this.taskOwner.getJumpHelper().setJumping();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return false;
	}
}
