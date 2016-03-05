package thebetweenlands.entities.entityAI.recruit;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIRecruitAttackOnCollide extends EntityAIBase implements IRecruitAI {
	private static final float MINIMUM_DAMAGE = 2.0F;

	private World worldObj;
	private EntityCreature attacker;
	/** An amount of decrementing ticks that allows the entity to attack once the tick reaches 0. */
	private int attackTick;
	/** The speed with which the mob will approach the target */
	private double speedTowardsTarget;
	/** When true, the mob will continue chasing its target, even if it can't find a path to them right now. */
	private boolean longMemory;
	/** The PathEntity of our entity. */
	private PathEntity entityPathEntity;
	private Class classTarget;
	private int updateTick;
	private double targetX;
	private double targetY;
	private double targetZ;
	private int failedPathFindingPenalty;

	public EntityAIRecruitAttackOnCollide(EntityCreature attacker, double speed, boolean longMemory) {
		this.attacker = attacker;
		this.speedTowardsTarget = speed;
		this.longMemory = longMemory;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

		if (entitylivingbase == null) {
			return false;
		} else if (!entitylivingbase.isEntityAlive()) {
			return false;
		} else if (this.classTarget != null && !this.classTarget.isAssignableFrom(entitylivingbase.getClass())) {
			return false;
		} else {
			if (-- this.updateTick <= 0) {
				this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
				this.updateTick = 4 + this.attacker.getRNG().nextInt(7);
				return this.entityPathEntity != null;
			} else {
				return true;
			}
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
		return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : (!this.longMemory ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistance(MathHelper.floor_double(entitylivingbase.posX), MathHelper.floor_double(entitylivingbase.posY), MathHelper.floor_double(entitylivingbase.posZ))));
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
		this.updateTick = 0;
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		this.attacker.getNavigator().clearPathEntity();
	}

	/**
	 * Updates the task
	 */
	public void updateTask() {
		EntityLivingBase target = this.attacker.getAttackTarget();
		this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
		double targetDistance = this.attacker.getDistanceSq(target.posX, target.boundingBox.minY, target.posZ);
		double reach = (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + target.width);
		--this.updateTick;

		if ((this.longMemory || this.attacker.getEntitySenses().canSee(target)) && this.updateTick <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || target.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F)) {
			this.targetX = target.posX;
			this.targetY = target.boundingBox.minY;
			this.targetZ = target.posZ;
			this.updateTick = failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

			if (this.attacker.getNavigator().getPath() != null) {
				PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
				if (finalPathPoint != null && target.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1) {
					failedPathFindingPenalty = 0;
				} else {
					failedPathFindingPenalty += 10;
				}
			} else {
				failedPathFindingPenalty += 10;
			}

			if (targetDistance > 1024.0D) {
				this.updateTick += 10;
			} else if (targetDistance > 256.0D) {
				this.updateTick += 5;
			}

			if (!this.attacker.getNavigator().tryMoveToEntityLiving(target, this.speedTowardsTarget)) {
				this.updateTick += 15;
			}
		}

		this.attackTick = Math.max(this.attackTick - 1, 0);

		if (targetDistance <= reach && this.attackTick <= 0) {
			this.attackTick = 20;

			if (this.attacker.getHeldItem() != null) {
				this.attacker.swingItem();
			}

			float prevHealth = target.getHealth();
			this.attacker.attackEntityAsMob(target);
			float healthDiff = prevHealth - target.getHealth();
			if(healthDiff < MINIMUM_DAMAGE) {
				target.attackEntityFrom(DamageSource.causeMobDamage(this.attacker), MINIMUM_DAMAGE - healthDiff);
			}
		}
	}
}
