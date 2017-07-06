package thebetweenlands.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Copy of {@link EntityAITarget} for non creature entities
 * @param <T>
 */
public abstract class EntityAITargetNonCreature extends EntityAIBase
{
	/** The entity that this task belongs to */
	protected final EntityLiving taskOwner;
	/** If true, EntityAI targets must be able to be seen (cannot be blocked by walls) to be suitable targets. */
	protected boolean shouldCheckSight;
	/** When true, only entities that can be reached with minimal effort will be targetted. */
	private final boolean nearbyOnly;
	/** When nearbyOnly is true: 0 -> No target, but OK to search; 1 -> Nearby target found; 2 -> Target too far. */
	private int targetSearchStatus;
	/** When nearbyOnly is true, this throttles target searching to avoid excessive pathfinding. */
	private int targetSearchDelay;
	/**
	 * If  @shouldCheckSight is true, the number of ticks before the interuption of this AITastk when the entity does't
	 * see the target
	 */
	private int targetUnseenTicks;
	protected EntityLivingBase target;
	protected int unseenMemoryTicks;

	public EntityAITargetNonCreature(EntityLiving creature, boolean checkSight)
	{
		this(creature, checkSight, false);
	}

	public EntityAITargetNonCreature(EntityLiving creature, boolean checkSight, boolean onlyNearby)
	{
		this.unseenMemoryTicks = 60;
		this.taskOwner = creature;
		this.shouldCheckSight = checkSight;
		this.nearbyOnly = onlyNearby;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting()
	{
		EntityLivingBase entitylivingbase = this.taskOwner.getAttackTarget();

		if (entitylivingbase == null)
		{
			entitylivingbase = this.target;
		}

		if (entitylivingbase == null)
		{
			return false;
		}
		else if (!entitylivingbase.isEntityAlive())
		{
			return false;
		}
		else
		{
			Team team = this.taskOwner.getTeam();
			Team team1 = entitylivingbase.getTeam();

			if (team != null && team1 == team)
			{
				return false;
			}
			else
			{
				double d0 = this.getTargetDistance();

				if (this.taskOwner.getDistanceSqToEntity(entitylivingbase) > d0 * d0)
				{
					return false;
				}
				else
				{
					if (this.shouldCheckSight)
					{
						if (this.taskOwner.getEntitySenses().canSee(entitylivingbase))
						{
							this.targetUnseenTicks = 0;
						}
						else if (++this.targetUnseenTicks > this.unseenMemoryTicks)
						{
							return false;
						}
					}

					if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).capabilities.disableDamage)
					{
						return false;
					}
					else
					{
						this.taskOwner.setAttackTarget(entitylivingbase);
						return true;
					}
				}
			}
		}
	}

	protected double getTargetDistance()
	{
		IAttributeInstance iattributeinstance = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
		return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		this.targetSearchStatus = 0;
		this.targetSearchDelay = 0;
		this.targetUnseenTicks = 0;
	}

	/**
	 * Resets the task
	 */
	public void resetTask()
	{
		this.taskOwner.setAttackTarget((EntityLivingBase)null);
		this.target = null;
	}

	/**
	 * A static method used to see if an entity is a suitable target through a number of checks.
	 */
	public static boolean isSuitableTarget(EntityLiving attacker, EntityLivingBase target, boolean includeInvincibles, boolean checkSight)
	{
		if (target == null)
		{
			return false;
		}
		else if (target == attacker)
		{
			return false;
		}
		else if (!target.isEntityAlive())
		{
			return false;
		}
		else if (!attacker.canAttackClass(target.getClass()))
		{
			return false;
		}
		else if (attacker.isOnSameTeam(target))
		{
			return false;
		}
		else
		{
			if (attacker instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId() != null)
			{
				if (target instanceof IEntityOwnable && ((IEntityOwnable)attacker).getOwnerId().equals(target.getUniqueID()))
				{
					return false;
				}

				if (target == ((IEntityOwnable)attacker).getOwner())
				{
					return false;
				}
			}
			else if (target instanceof EntityPlayer && !includeInvincibles && ((EntityPlayer)target).capabilities.disableDamage)
			{
				return false;
			}

			return !checkSight || attacker.getEntitySenses().canSee(target);
		}
	}

	/**
	 * A method used to see if an entity is a suitable target through a number of checks. Args : entity,
	 * canTargetInvinciblePlayer
	 */
	protected boolean isSuitableTarget(EntityLivingBase target, boolean includeInvincibles)
	{
		if (!isSuitableTarget(this.taskOwner, target, includeInvincibles, this.shouldCheckSight))
		{
			return false;
		}
		else if (this.taskOwner instanceof EntityCreature && !((EntityCreature)this.taskOwner).isWithinHomeDistanceFromPosition(new BlockPos(target)))
		{
			return false;
		}
		else
		{
			if (this.nearbyOnly)
			{
				if (--this.targetSearchDelay <= 0)
				{
					this.targetSearchStatus = 0;
				}

				if (this.targetSearchStatus == 0)
				{
					this.targetSearchStatus = this.canEasilyReach(target) ? 1 : 2;
				}

				if (this.targetSearchStatus == 2)
				{
					return false;
				}
			}

			return true;
		}
	}

	/**
	 * Checks to see if this entity can find a short path to the given target.
	 */
	private boolean canEasilyReach(EntityLivingBase target)
	{
		this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
		Path path = this.taskOwner.getNavigator().getPathToEntityLiving(target);

		if (path == null)
		{
			return false;
		}
		else
		{
			PathPoint pathpoint = path.getFinalPathPoint();

			if (pathpoint == null)
			{
				return false;
			}
			else
			{
				int i = pathpoint.xCoord - MathHelper.floor(target.posX);
				int j = pathpoint.zCoord - MathHelper.floor(target.posZ);
				return (double)(i * i + j * j) <= 2.25D;
			}
		}
	}
}