package thebetweenlands.aspect;

import net.minecraft.nbt.NBTTagCompound;

public class AspectCelawynn implements IAspect {
	public String getName() {
		return "Celawynn";
	}

	public String getType() {
		return "Stomach";
	}

	public String getDescription() {
		return "Has effect on the stomach. So this could have effect on the hunger bar for example.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
