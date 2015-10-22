package thebetweenlands.aspect.aspects;

import net.minecraft.nbt.NBTTagCompound;

public class AspectFirnalaz implements IAspect {
	public String getName() {
		return "Firnalaz";
	}

	public String getType() {
		return "Fire";
	}

	public String getDescription() {
		return "Magical property which relates to fire. Any combination with this effect can be related to fire.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
