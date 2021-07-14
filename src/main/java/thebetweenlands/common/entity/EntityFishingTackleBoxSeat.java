package thebetweenlands.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityFishingTackleBoxSeat extends Entity {
	
	private static final DataParameter<Float> OFFSET = EntityDataManager.<Float>createKey(EntityFishingTackleBoxSeat.class, DataSerializers.FLOAT);
	public boolean tempSeat = false;

	public EntityFishingTackleBoxSeat (World world) {
		super(world);
		setSize(0.0F, 0.0F);
	}

	public EntityFishingTackleBoxSeat(World entityWorld, boolean tempSeat) {
		super(entityWorld);
		this.tempSeat = tempSeat;
	}

	@Override
	protected void entityInit() {
        dataManager.register(OFFSET, 0.0F);
	}
	
	public void setSeatOffset(float amount) {
        dataManager.set(OFFSET, amount);
    }

    public float getSeatOffset() {
        return dataManager.get(OFFSET);
    }

	@Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (OFFSET.equals(key)) {
            setSeatOffset(getSeatOffset());
        }
        super.notifyDataManagerChange(key);
    }

	@Override
	public void updatePassenger(Entity entity) {
		super.updatePassenger(entity);
		if (entity instanceof EntityLivingBase)
			entity.setPositionAndRotation(posX, posY + getSeatOffset(), posZ, entity.rotationYaw, entity.rotationPitch);
	}

	@Override
	 public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			if (!isBeingRidden() || world.isAirBlock(getPosition()) || (tempSeat && ticksExisted >= 60))
				setDead();
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setSeatOffset(nbt.getFloat("offset"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setFloat("offset", getSeatOffset());
	}

}
