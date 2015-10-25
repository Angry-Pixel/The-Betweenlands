package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspect;

public class AspectArmaniis implements IAspect {
	public String getName() {
		return "Armaniis";
	}

	public String getType() {
		return "Desire";
	}

	public String getDescription() {
		return "Has effect on the desires of a mob or the player. Could be useful for food, but also things like trading or corrupting the desire.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
