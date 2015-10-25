package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspect;

public class AspectYihinren implements IAspect {
	public String getName() {
		return "Yihinren";
	}

	public String getType() {
		return "Form";
	}

	public String getDescription() {
		return "This effect has influence on the form of things, both physical and psychological. A very rare aspect that is mainly used in the special potions.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
