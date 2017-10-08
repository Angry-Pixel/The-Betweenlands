package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

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
        int i = this.taskOwner.getRevengeTimer();
        return i != this.revengeTimer && this.isSuitableTarget(this.taskOwner.getAttackTarget(), false);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.taskOwner.getRevengeTarget());
        this.revengeTimer = this.taskOwner.getRevengeTimer();

        if (this.entityCallsForHelp) {
            double dist = this.getTargetDistance();
            List list = this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(), new AxisAlignedBB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D).grow(dist, 10.0D, dist));
            for (Object aList : list) {
                EntityCreature creature = (EntityCreature) aList;

                if (this.taskOwner != creature && creature.getAttackTarget() == null && !creature.isOnSameTeam(this.taskOwner.getAttackTarget()) && creature != this.taskOwner.getAttackTarget()) {
                    creature.setAttackTarget(this.taskOwner.getRevengeTarget());
                }
            }
        }

        super.startExecuting();
    }
}