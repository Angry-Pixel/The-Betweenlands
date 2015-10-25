package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspect;

public class AspectYeowynn implements IAspect {
	public String getName() {
		return "Yeowynn";
	}

	public String getType() {
		return "Health";
	}

	public String getDescription() {
		return "Has effect on the health bar, could be both negative or positive, depending on the combination.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
