package thebetweenlands.aspect.aspects;

import net.minecraft.nbt.NBTTagCompound;

public class AspectFergalaz implements IAspect {
	public String getName() {
		return "Fergalaz";
	}

	public String getType() {
		return "Earth";
	}

	public String getDescription() {
		return "Magical property which relates to earth. Any combination with this effect can be related to the element earth.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
