package thebetweenlands.aspect;

import net.minecraft.nbt.NBTTagCompound;

public class AspectYunugaz implements IAspect {
	public String getName() {
		return "Yunugaz";
	}

	public String getType() {
		return "Wind";
	}

	public String getDescription() {
		return "Magical property which relates to wind. Any combination with this effect can be related to the element wind.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
