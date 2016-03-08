package thebetweenlands.entities.entityAI;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.AxisAlignedBB;

/**
 * Fixes entity attacking itself
 */
public class EntityAIHurtByTargetImproved extends EntityAITarget {
	boolean entityCallsForHelp;
	private int revengeTimer;

	public EntityAIHurtByTargetImproved(EntityCreature entity, boolean callsHelp) {
		super(entity, false);
		this.entityCallsForHelp = callsHelp;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		int i = this.taskOwner.func_142015_aE();
		return i != this.revengeTimer && this.isSuitableTarget(this.taskOwner.getAITarget(), false);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
		this.revengeTimer = this.taskOwner.func_142015_aE();

		if (this.entityCallsForHelp) {
			double dist = this.getTargetDistance();
			List list = this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), AxisAlignedBB.getBoundingBox(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D).expand(dist, 10.0D, dist));
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				EntityCreature creature = (EntityCreature) iterator.next();

				if (this.taskOwner != creature && creature.getAttackTarget() == null && !creature.isOnSameTeam(this.taskOwner.getAITarget()) && creature != this.taskOwner.getAITarget()) {
					creature.setAttackTarget(this.taskOwner.getAITarget());
				}
			}
		}

		super.startExecuting();
	}
}