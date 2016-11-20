package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityAIAttackOnCollide extends EntityAIBase {
	protected World worldObj;
	protected EntityCreature attacker;
	/** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
	protected int attackTick;

	public EntityAIAttackOnCollide(EntityCreature creature) {
		this.attacker = creature;
		this.worldObj = creature.worldObj;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		return this.attacker.getAttackTarget() != null && this.attacker.getAttackTarget().isEntityAlive();
	}

	@Override
	public boolean continueExecuting() {
		return this.attacker.getAttackTarget() != null && this.attacker.getAttackTarget().isEntityAlive();
	}

	@Override
	public void updateTask() {
		EntityLivingBase target = this.attacker.getAttackTarget();
		this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
		double distSq = this.attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
		this.attackTick = Math.max(this.attackTick - 1, 0);
		this.attackEntity(target, distSq);
	}

	protected void attackEntity(EntityLivingBase target, double distSq) {
		double d0 = this.getAttackReachSqr(target);

		if (distSq <= d0 && this.attackTick <= 0) {
			this.attackTick = 20;
			this.attacker.swingArm(EnumHand.MAIN_HAND);
			this.attacker.attackEntityAsMob(target);
		}
	}

	protected double getAttackReachSqr(EntityLivingBase attackTarget) {
		return (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width);
	}
}