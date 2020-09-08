package thebetweenlands.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityFishingTackleBoxSeat extends Entity {
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
	protected void entityInit() {}

	@Override
	public void updatePassenger(Entity entity) {
		super.updatePassenger(entity);
		if (entity instanceof EntityLivingBase)
			entity.setPositionAndRotation(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
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
	protected void readEntityFromNBT(NBTTagCompound nbt) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {}

}
