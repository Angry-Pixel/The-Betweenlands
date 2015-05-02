package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.network.packets.PacketSnailHatchParticle;
import thebetweenlands.utils.AnimationMathHelper;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityMireSnailEgg extends EntityAnimal {
	public float pulseFloat;
	AnimationMathHelper pulse = new AnimationMathHelper();
	public EntityMireSnailEgg(World world) {
		super(world);
		setSize(0.35F, 0.35F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(31, new Integer(0));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if(getGrowingAge() < 0 || getGrowingAge() > 0) // stupid hack to stop entity scaling 
			setGrowingAge(0);
		if(!worldObj.isRemote) {
			if(getHatchTime() < 100)
				setHatchTime(getHatchTime() + 1);
			if(getHatchTime() >= 24000) //this should be 24000 = 1 day (20 mins)
				hatch();
		}
		pulseFloat = pulse.swing(0.3F, 0.2F, false);
		renderYawOffset = prevRenderYawOffset;
	}

	private void hatch() {
		EntityMireSnail snail = new EntityMireSnail(worldObj);
		snail.setPosition(posX, posY, posZ);
		if(snail.getCanSpawnHere()) {
			setDead();
			hatchParticlePacketTarget();
			snail.setTame(Byte.valueOf((byte) 1));
			worldObj.spawnEntityInWorld(snail);
		}
	}

	private void hatchParticlePacketTarget() {
		World world = worldObj;
		int dim = 0;
		if (world instanceof WorldServer) {
			dim = ((WorldServer) world).provider.dimensionId;
			TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketSnailHatchParticle(this.posX, this.posY, this.posZ)), new TargetPoint(dim, posX + 0.5D, posY + 0.2D, posZ + 0.5D, 16D));
		}
	}

	@Override
	public boolean isMovementCeased() {
		return true;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityAgeable) {
		return null;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5.0D);
	}

	public void setHatchTime(int hatchTime) {
		dataWatcher.updateObject(31, Integer.valueOf(hatchTime));
	}

	public int getHatchTime() {
		return dataWatcher.getWatchableObjectInt(31);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("hatchTicks", getHatchTime());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setHatchTime(nbt.getInteger("hatchTicks"));
	}
}
