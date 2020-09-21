package thebetweenlands.common.entity.mobs;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.EntityProximitySpawner;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.util.PlayerUtil;

public class EntityRockSnot extends EntityProximitySpawner implements IEntityBL {
	
	private static final DataParameter<Integer> TENDRIL_COUNT = EntityDataManager.createKey(EntityRockSnot.class, DataSerializers.VARINT);
	public int spawnDelayCounter = 20;

	public EntityRockSnot(World world) {
		super(world);
		setSize(1F, 0.5F);
		setNoGravity(true);
	}

	public World getWorld() {
		return getEntityWorld();
	}

	@Override
	protected void initEntityAI() {
		targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true));
		tasks.addTask(2, new EntityRockSnot.EntityAIShootTendril(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(TENDRIL_COUNT, 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
	}

	@Override
	public void onLivingUpdate() {
		if (!getEntityWorld().isRemote) {
			if (spawnDelayCounter > 0)
				spawnDelayCounter--;
		}
		super.onLivingUpdate();
	}
	
	@Override
	public void onUpdate() {
		if (!getEntityWorld().isRemote) {
			if(getEntityWorld().getTotalWorldTime()%5 == 0)
				checkArea();
		}
		super.onUpdate();
	}

	@Override
    public float getEyeHeight(){
        return this.height - height;
    }

	@Nullable
	@Override
	protected Entity checkArea() {
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				Entity entity = list.get(entityCount);
				if (entity != null) {
					//if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isSpectator() && !((EntityPlayer) entity).isCreative()) {
						if (canSneakPast() && entity.isSneaking())
							return null;
						else if (checkSight() && !canEntityBeSeen(entity))
							return null;
						else 
							if(getCanShootTendril())
								setAttackTarget((EntityLivingBase) entity);

							if (!isDead && isSingleUse())
								setDead();
						}
					//}
			}
			if(list.isEmpty())
				setAttackTarget(null);
		}
		return null;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		return canEntityBeSeen(entity) ? super.attackEntityAsMob(entity) : false;
	}

	protected boolean damageSnot(DamageSource source, float ammount) {
		return super.attackEntityFrom(source, ammount);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(sourceEntity instanceof EntityPlayer && ((EntityPlayer) sourceEntity).isCreative()) {
				this.setDead();
			}
		}
		return false;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

	@Override
	public void updatePassenger(Entity entity) {
		PlayerUtil.resetFloating(entity);
		if (entity instanceof EntityLivingBase)
			entity.setPosition(posX, getEntityBoundingBox().minY - entity.height, posZ);
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	public boolean getCanShootTendril() {
		return getTendrilCount() < 4;
	}

	public int getTendrilCount() {
		return dataManager.get(TENDRIL_COUNT);
	}

	public void setTendrilCount(int count) {
		dataManager.set(TENDRIL_COUNT, count);
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean isNotColliding() {
		return true;
	}

	@Override
    public EnumPushReaction getPushReaction() {
        return EnumPushReaction.IGNORE;
    }

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
	protected float getProximityHorizontal() {
		return 4;
	}

	@Override
	protected float getProximityVertical() {
		return 4;
	}

	@Override
	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).grow(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal()).offset(0D, - getProximityVertical () * 2 , 0D);
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		return null;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 0;
	}

	@Override
	protected boolean isSingleUse() {
		return false;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}

    static class EntityAIShootTendril extends EntityAIBase {
    	EntityRockSnot parentEntity;
		EntityLivingBase target;

		public EntityAIShootTendril(EntityRockSnot parent) {
			this.parentEntity = parent;
			this.setMutexBits(5);
		}

		@Override
		public boolean shouldExecute() {
			target = parentEntity.getAttackTarget();

			if (target == null)
				return false;
			else if (parentEntity.spawnDelayCounter == 0)
				return true;
			else
				return false;

		}

		@Override
		public boolean shouldContinueExecuting() {
			return target != null && parentEntity.recentlyHit <= 40 && parentEntity.getCanShootTendril() && parentEntity.spawnDelayCounter == 0;
		}

		@Override
		public void startExecuting() {

		}

		@Override
		public void updateTask() {
			if (!parentEntity.getEntityWorld().isRemote && target != null) {
				double targetX = target.posX - parentEntity.posX;
				double targetY = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F) - (parentEntity.posY + (double) (parentEntity.height / 2.0F));
				double targetZ = target.posZ - parentEntity.posZ;
				EntityRockSnotTendril grabber = new EntityRockSnotTendril(parentEntity);
				grabber.setPositionAndUpdate(parentEntity.posX, parentEntity.posY, parentEntity.posZ);
				grabber.moveToTarget(targetX, targetY, targetZ, 0.3F);
				parentEntity.getEntityWorld().spawnEntity(grabber);
				if (!grabber.getExtending())
					grabber.setExtending(true);
				parentEntity.setTendrilCount(parentEntity.getTendrilCount() + 1);
				parentEntity.spawnDelayCounter = 10;
			}
		}

		@Override
	    public void resetTask() {
	        target = null;
	    }
	}

}
