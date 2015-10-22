package thebetweenlands.aspect.aspects;

import net.minecraft.nbt.NBTTagCompound;

public class AspectOrdaniis implements IAspect {
	public String getName() {
		return "Ordaniis";
	}

	public String getType() {
		return "Enhance";
	}

	public String getDescription() {
		return "Randomly enhances up to two effects (both positive and negative) in a potion, making them either stronger, last longer or changes an effect.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
