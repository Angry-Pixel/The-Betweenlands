package thebetweenlands.common.capability.playermounts;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

public interface IPlayerMountsEntityCapability {
	public List<NBTTagCompound> getQueuedPassengers();
}
