package thebetweenlands.common.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Copy of {@link EntityAINearestAttackableTarget} for non creature entities
 * @param <T>
 */
public class EntityAINearestAttackableTargetNonCreature<T extends EntityLivingBase> extends EntityAITargetNonCreature
{
	protected final Class<T> targetClass;
	private final int targetChance;
	/** Instance of EntityAINearestAttackableTargetSorter. */
	protected final EntityAINearestAttackableTargetNonCreature.Sorter theNearestAttackableTargetSorter;
	protected final Predicate <? super T > targetEntitySelector;
	protected T targetEntity;

	public EntityAINearestAttackableTargetNonCreature(EntityLiving creature, Class<T> classTarget, boolean checkSight)
	{
		this(creature, classTarget, checkSight, false);
	}

	public EntityAINearestAttackableTargetNonCreature(EntityLiving creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby)
	{
		this(creature, classTarget, 10, checkSight, onlyNearby, (Predicate <? super T >)null);
	}

	public EntityAINearestAttackableTargetNonCreature(EntityLiving creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate <? super T > targetSelector)
	{
		super(creature, checkSight, onlyNearby);
		this.targetClass = classTarget;
		this.targetChance = chance;
		this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTargetNonCreature.Sorter(creature);
		this.setMutexBits(1);
		this.targetEntitySelector = new Predicate<T>()
		{
			@Override
			public boolean apply(@Nullable T p_apply_1_)
			{
				return p_apply_1_ == null ? false : (targetSelector != null && !targetSelector.apply(p_apply_1_) ? false : (!EntitySelectors.NOT_SPECTATING.apply(p_apply_1_) ? false : EntityAINearestAttackableTargetNonCreature.this.isSuitableTarget(p_apply_1_, false)));
			}
		};
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean shouldExecute()
	{
		if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0)
		{
			return false;
		}
		else if (this.targetClass != EntityPlayer.class && this.targetClass != EntityPlayerMP.class)
		{
			List<T> list = this.taskOwner.getEntityWorld().<T>getEntitiesWithinAABB(this.targetClass, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);

			if (list.isEmpty())
			{
				return false;
			}
			else
			{
				Collections.sort(list, this.theNearestAttackableTargetSorter);
				this.targetEntity = list.get(0);
				return true;
			}
		}
		else
		{
			this.targetEntity = (T)this.taskOwner.getEntityWorld().getNearestAttackablePlayer(this.taskOwner.posX, this.taskOwner.posY + (double)this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), new Function<EntityPlayer, Double>()
			{
				@Override
				@Nullable
				public Double apply(@Nullable EntityPlayer p_apply_1_)
				{
					ItemStack itemstack = p_apply_1_.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

					if (itemstack != null && itemstack.getItem() == Items.SKULL)
					{
						int i = itemstack.getItemDamage();
						boolean flag = EntityAINearestAttackableTargetNonCreature.this.taskOwner instanceof EntitySkeleton && i == 0;
						boolean flag1 = EntityAINearestAttackableTargetNonCreature.this.taskOwner instanceof EntityZombie && i == 2;
						boolean flag2 = EntityAINearestAttackableTargetNonCreature.this.taskOwner instanceof EntityCreeper && i == 4;

						if (flag || flag1 || flag2)
						{
							return Double.valueOf(0.5D);
						}
					}

					return Double.valueOf(1.0D);
				}
			}, (Predicate<EntityPlayer>)this.targetEntitySelector);
			return this.targetEntity != null;
		}
	}

	protected AxisAlignedBB getTargetableArea(double targetDistance)
	{
		return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting()
	{
		this.taskOwner.setAttackTarget(this.targetEntity);
		super.startExecuting();
	}

	public static class Sorter implements Comparator<Entity>
	{
		private final Entity theEntity;

		public Sorter(Entity theEntityIn)
		{
			this.theEntity = theEntityIn;
		}

		@Override
		public int compare(Entity p_compare_1_, Entity p_compare_2_)
		{
			double d0 = this.theEntity.getDistanceSq(p_compare_1_);
			double d1 = this.theEntity.getDistanceSq(p_compare_2_);
			return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
		}
	}
}