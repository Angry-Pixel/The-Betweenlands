package thebetweenlands.common.entity;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.registries.ItemRegistry;

public class EntitySpyEye extends EntityProximitySpawner {

	private static final DataParameter<Boolean> IS_OPENING = EntityDataManager.createKey(EntitySpyEye.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> OPEN_COUNT = EntityDataManager.createKey(EntitySpyEye.class, DataSerializers.VARINT);
	public int MAX_OPEN = 10;
	public int MIN_OPEN = 0;

	public EntitySpyEye(World world) {
		super(world);
		setSize(1F, 1F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_OPENING, false);
		dataManager.register(OPEN_COUNT, 0);
	}

	@Override
	public void onUpdate() {
		// super.onUpdate();
		if (!getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime() % 5 == 0) {
				checkArea();
		}
		if (!getEntityWorld().isRemote) {
			if (!getOpening() && getOpenCount() > MIN_OPEN)
				setOpenCount(getOpenCount() - 1);

			if (getOpening() && getOpenCount() < MAX_OPEN)
				setOpenCount(getOpenCount() + 1);
		}
	}

	@Override
	@Nullable
	protected Entity checkArea() {
		Entity entity = null;
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);

				if (entity != null) {
					if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isSpectator() && !((EntityPlayer) entity).isCreative()) {
						if (canSneakPast() && entity.isSneaking())
							return null;
						else if (checkSight() && !canEntityBeSeen(entity))
							return null;
						else {
							if(!getOpening())
								setOpening(true);
						}
					}
				}
			}
			if (entity == null) {
				if (getOpening())
					setOpening(false);
			}
		}
		return entity;
	}


    public boolean isWearingSilkMask(EntityLivingBase entity) {
    	if(entity instanceof EntityPlayer) {
        	ItemStack helmet = ((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        	if(!helmet.isEmpty() && helmet.getItem() == ItemRegistry.SILK_MASK) {
        		return true;
        	}
        }
    	return false;
    }

	private void setOpening(boolean open) {
		dataManager.set(IS_OPENING, open);
	}

    public boolean getOpening() {
        return dataManager.get(IS_OPENING);
    }

	private void setOpenCount(int openCountIn) {
		dataManager.set(OPEN_COUNT, openCountIn);
	}

	public int getOpenCount() {
		return dataManager.get(OPEN_COUNT);
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public boolean canBeCollidedWith() {
        return false;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return true;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source == DamageSource.OUT_OF_WORLD) {
			return true;
		}
		if (source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if (sourceEntity instanceof EntityPlayer) {
				super.attackEntityFrom(source, damage);
			}
			return true;
		}
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return 6F;
	}

	@Override
	protected float getProximityVertical() {
		return 3F;
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
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 0;
	}
}