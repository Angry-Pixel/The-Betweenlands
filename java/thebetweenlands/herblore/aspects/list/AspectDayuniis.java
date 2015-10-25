package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspect;

public class AspectDayuniis implements IAspect {
	public String getName() {
		return "Dayuniis";
	}

	public String getType() {
		return "Mind";
	}

	public String getDescription() {
		return "Has effect on the player's mind and on how senses work. Could be positive, or negative (think nausea/schizophrenia).";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
