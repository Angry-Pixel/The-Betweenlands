package thebetweenlands.common.entity.mobs;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.EntityProximitySpawner;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityRockSnot extends EntityProximitySpawner implements IEntityBL {
	private static final DataParameter<Integer> TENDRIL_COUNT = EntityDataManager.createKey(EntityRockSnot.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> JAW_ANGLE = EntityDataManager.createKey(EntityRockSnot.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> EATING_TIMER = EntityDataManager.createKey(EntityRockSnot.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> PEARL_TIMER = EntityDataManager.createKey(EntityRockSnot.class, DataSerializers.VARINT);
	public boolean placed_by_player;
	public int spawnDelayCounter = 20;
	public static final int PEARL_CREATION_TIME = 120; // 6 seconds

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
		dataManager.register(JAW_ANGLE, 0);
		dataManager.register(EATING_TIMER, 0);
		dataManager.register(PEARL_TIMER, -1);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1D);
	}

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.ROCK_SNOT;
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
		if (getEntityWorld().isRemote) {
			if (isBeingRidden() && getJawAngle() == 16)
				if (getEntityWorld().getTotalWorldTime() % 20 == 0)
					spawnEatingParticles();
			if (getPearlTimer() == 1)
				spawnSpittingParticles();
			if (getPearlTimer() >= 30)
				if (getEntityWorld().getTotalWorldTime() % 10 == 0)
					spawnMakingParticles();
		}

		if (!getEntityWorld().isRemote) {
			if (getPearlTimer() < 0) {
				if (getEntityWorld().getTotalWorldTime() % 5 == 0)
					checkAreaHere();

				if (getAttackTarget() != null) {
					if (!isBeingRidden() && getJawAngle() < 80) {
						setJawAngle(getJawAngle() + 8);
						if (getEatingHeight() > 0)
							setEatingHeight(0);
					}
					if (isBeingRidden() && getJawAngle() > 16)
						setJawAngle(getJawAngle() - 8);
				}

				if (getAttackTarget() == null) {
					if (!isBeingRidden()) {
						if (getJawAngle() > 0 && getTendrilCount() <= 0)
							setJawAngle(getJawAngle() - 8);
					}
				}

				if (isBeingRidden() && getJawAngle() == 16 && getPlacedByPlayer()) {
					if (getEntityWorld().getTotalWorldTime() % 20 == 0) {
						setEatingHeight(getEatingHeight() + 1);
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.ROCK_SNOT_EAT, SoundCategory.HOSTILE, 1F, 1F);
						getPassengers().get(0).attackEntityFrom(DamageSource.GENERIC, 0F);
						if (getEatingHeight() == 10 && getPassengers().get(0) != null) {
							getPassengers().get(0).setDead();
							setPearlTimer(PEARL_CREATION_TIME);
							setEatingHeight(0);
							setJawAngle(0);
						}
					}
				}

				if (isBeingRidden() && getJawAngle() == 16 && !getPlacedByPlayer())
					if (getEntityWorld().getTotalWorldTime() % 20 == 0) {
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.ROCK_SNOT_EAT, SoundCategory.HOSTILE, 1F, 1F);
						getPassengers().get(0).attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
					}

			} else {
				if (getPearlTimer() >= 0) {
					setPearlTimer(getPearlTimer() - 1);
					if (getEntityWorld().getTotalWorldTime() % 20 == 0 && getPearlTimer() >= 30)
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.ROCK_SNOT_DIGEST, SoundCategory.HOSTILE, 1F, 0.8F + rand.nextFloat() * 0.5F);
				}

				if (getPearlTimer() == 0) {
					ItemStack pearl = new ItemStack(ItemRegistry.ROCK_SNOT_PEARL);
					EntityItem item = new EntityItem(world, posX, posY + 0.5D, posZ, pearl);
					item.motionX = item.motionZ = 0D;
					item.motionY = 0.4D;
					world.spawnEntity(item);
					getEntityWorld().playSound(null, getPosition(), SoundRegistry.ROCK_SNOT_SPIT, SoundCategory.HOSTILE, 1F, 1F);
				}
			}
		}
		super.onUpdate();
	}

	@Override
    public float getEyeHeight(){
        return this.height;
    }

	@Nullable
	@Override
	protected Entity checkArea() {
		return null;
	}

	public void checkAreaHere() {
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, proximityBox());
			for (Iterator<EntityLivingBase> iterator = list.iterator(); iterator.hasNext();) {
				EntityLivingBase entity  = iterator.next();
				if (entity != null && (entity instanceof EntityPlayer && getPlacedByPlayer() || entity instanceof EntityRockSnot))
					iterator.remove();
			}
			if (list.isEmpty()) {
				setAttackTarget(null);
				return;
			}
			if (!list.isEmpty()) {
				EntityLivingBase entity = list.get(0);

					//if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isSpectator() && !((EntityPlayer) entity).isCreative()) {

					if (canSneakPast() && entity.isSneaking())
						return;
					else if (checkSight() && !canEntityBeSeen(entity))
							return;
					else 
						if(getCanShootTendril())
							setAttackTarget((EntityLivingBase) entity);
					if (!isDead && isSingleUse())
						setDead();
					//}
			}
		}
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
		if(source == DamageSource.DROWN)
			return false;
		if(source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if(!(sourceEntity instanceof EntityPlayer))
				if(isBeingRidden() && getPassengers().get(0) == sourceEntity)
					return false;
			if(sourceEntity instanceof EntityPlayer && ((EntityPlayer) sourceEntity).isCreative()) {
				this.setDead();
			}
		}
		return damageSnot(source, damage);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		if(entity == EntityPlayer.class && getPlacedByPlayer() || entity == EntityRockSnot.class)
			return false;
		return super.canAttackClass(entity);
	}

	@Override
	public void setAttackTarget(EntityLivingBase entity) {
		if (getPlacedByPlayer()) {
			if (entity instanceof EntityPlayer)
				super.setAttackTarget(null);
		} else
			super.setAttackTarget(entity);
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}

	@Override
	public void updatePassenger(Entity entity) {
		entity.setPosition(posX, posY + height * 0.5F - (entity.height / 10F * ((float) getEatingHeight())), posZ);
	}

	@SideOnly(Side.CLIENT)
	public void spawnMakingParticles() {
		for (int count = 0; count < 5; ++count)
			BLParticles.GEM_PROC.spawn(world, posX + (world.rand.nextDouble() - 0.5D), posY + 0.5D + world.rand.nextDouble(), posZ + (world.rand.nextDouble() - 0.5D));
	}

	@SideOnly(Side.CLIENT)
	public void spawnEatingParticles() {
		for (int count = 0; count < 5; ++count)
			BLParticles.ITEM_BREAKING.spawn(world, posX + (world.rand.nextDouble() - 0.5D), posY + world.rand.nextDouble(), posZ + (world.rand.nextDouble() - 0.5D), ParticleArgs.get().withData(EnumItemMisc.SNOT.create(1)));
	}

	@SideOnly(Side.CLIENT)
	public void spawnSpittingParticles() {
		for (int count = 0; count < 5; ++count)
			for (int more = 0; more < 5; ++more) {
				if(inWater)
					world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX, posY + count * 0.5D, posZ, 0.0D, 0.1D, 0.0D, new int[0]);
				else
					BLParticles.RAIN.spawn(world, posX, posY + count * 0.5D, posZ).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			}
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

	private int getEatingHeight() {
		return dataManager.get(EATING_TIMER);
	}

	public void setEatingHeight(int count) {
		dataManager.set(EATING_TIMER, count);
	}

	public int getPearlTimer() {
		return dataManager.get(PEARL_TIMER);
	}

	public void setPearlTimer(int count) {
		dataManager.set(PEARL_TIMER, count);
	}

	public boolean getCanShootTendril() {
		return getTendrilCount() < 4 && getPearlTimer() <= 0;
	}

	public int getTendrilCount() {
		return dataManager.get(TENDRIL_COUNT);
	}

	public void setTendrilCount(int count) {
		dataManager.set(TENDRIL_COUNT, count);
	}

	public void setJawAngle(int angle) {
		dataManager.set(JAW_ANGLE, angle);
	}

	public int getJawAngle() {
		return dataManager.get(JAW_ANGLE);
	}

	public void setPlacedByPlayer(boolean placed) {
		placed_by_player = placed;
	}

	public boolean getPlacedByPlayer() {
		return placed_by_player;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setPlacedByPlayer(compound.getBoolean("placed_by_player"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("placed_by_player", getPlacedByPlayer());
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
		return 2;
	}

	@Override
	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).grow(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal()).offset(0D, + getProximityVertical () + 1D , 0D);
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

			if (target == null || parentEntity.isBeingRidden() || !parentEntity.getCanShootTendril())
				return false;
			else if (parentEntity.spawnDelayCounter == 0)
				return true;
			else
				return false;

		}

		@Override
		public boolean shouldContinueExecuting() {
			return target != null && parentEntity.recentlyHit <= 40 && parentEntity.getCanShootTendril() && parentEntity.spawnDelayCounter == 0 && !parentEntity.isBeingRidden();
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
				grabber.setPositionAndUpdate(parentEntity.posX, parentEntity.posY + parentEntity.height * 0.5D, parentEntity.posZ);
				grabber.moveToTarget(targetX, targetY, targetZ, 0.3F);
				parentEntity.getEntityWorld().spawnEntity(grabber);
				parentEntity.getEntityWorld().playSound(null, parentEntity.getPosition(), SoundRegistry.ROCK_SNOT_ATTACK, SoundCategory.HOSTILE, 1F, 1F);
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
