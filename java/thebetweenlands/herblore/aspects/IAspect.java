package thebetweenlands.herblore.aspects;

import net.minecraft.nbt.NBTTagCompound;

public interface IAspect {
	String getName();

	String getType();

	String getDescription();
	
	int getIconIndex();
}
