package thebetweenlands.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.utils.AnimationMathHelper;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntitySwordEnergy extends Entity implements IEntityAdditionalSpawnData {
	public float pulseFloat;
	AnimationMathHelper pulse = new AnimationMathHelper();
	public EntitySwordEnergy(World world) {
		super(world);
		setSize(8F, 0.5F);
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(20, 3.5F);
		dataWatcher.addObject(21, 3.5F);
		dataWatcher.addObject(22, 3.5F);
		dataWatcher.addObject(23, 3.5F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		pulseFloat = pulse.swing(0.3F, 0.75F, false);
		motionY = 0;
		if (!worldObj.isRemote) {
			if (getSwordPart1Pos() > 0 && getSwordPart1Pos() < 3.5F)
				setSwordPart1Pos(getSwordPart1Pos() - 0.05F);

			if (getSwordPart2Pos() > 0 && getSwordPart2Pos() < 3.5F)
				setSwordPart2Pos(getSwordPart2Pos() - 0.05F);

			if (getSwordPart3Pos() > 0 && getSwordPart3Pos() < 3.5F)
				setSwordPart3Pos(getSwordPart3Pos() - 0.05F);

			if (getSwordPart4Pos() > 0 && getSwordPart4Pos() < 3.5F)
				setSwordPart4Pos(getSwordPart4Pos() - 0.05F);

			if (getSwordPart1Pos() <= 0 && getSwordPart2Pos() <= 0 && getSwordPart3Pos() <= 0 && getSwordPart4Pos() <= 0) {
				EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(BLItemRegistry.shockwaveSword));
				entityItem.motionX = 0;
				entityItem.motionY = 0;
				entityItem.motionZ = 0;
				entityItem.delayBeforeCanPickup = 10;
				worldObj.spawnEntityInWorld(entityItem);
				setDead();
			}
		}
	}

	public float getSwordPart1Pos() {
		return dataWatcher.getWatchableObjectFloat(20);
	}

	public void setSwordPart1Pos(float pos) {
		dataWatcher.updateObject(20, pos);
	}
	
	public float getSwordPart2Pos() {
		return dataWatcher.getWatchableObjectFloat(21);
	}

	public void setSwordPart2Pos(float pos) {
		dataWatcher.updateObject(21, pos);
	}
	
	public float getSwordPart3Pos() {
		return dataWatcher.getWatchableObjectFloat(22);
	}

	public void setSwordPart3Pos(float pos) {
		dataWatcher.updateObject(22, pos);
	}
	
	public float getSwordPart4Pos() {
		return dataWatcher.getWatchableObjectFloat(23);
	}

	public void setSwordPart4Pos(float pos) {
		dataWatcher.updateObject(23, pos);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setFloat("partPos1", getSwordPart1Pos());
		nbt.setFloat("partPos2", getSwordPart2Pos());
		nbt.setFloat("partPos3", getSwordPart3Pos());
		nbt.setFloat("partPos4", getSwordPart4Pos());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		setSwordPart1Pos(nbt.getFloat("partPos1"));
		setSwordPart2Pos(nbt.getFloat("partPos2"));
		setSwordPart3Pos(nbt.getFloat("partPos3"));
		setSwordPart4Pos(nbt.getFloat("partPos4"));
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeFloat(dataWatcher.getWatchableObjectFloat(20));
		buffer.writeFloat(dataWatcher.getWatchableObjectFloat(21));
		buffer.writeFloat(dataWatcher.getWatchableObjectFloat(22));
		buffer.writeFloat(dataWatcher.getWatchableObjectFloat(23));
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		dataWatcher.updateObject(20, additionalData.readFloat());
		dataWatcher.updateObject(21, additionalData.readFloat());
		dataWatcher.updateObject(22, additionalData.readFloat());
		dataWatcher.updateObject(23, additionalData.readFloat());
	}

}

