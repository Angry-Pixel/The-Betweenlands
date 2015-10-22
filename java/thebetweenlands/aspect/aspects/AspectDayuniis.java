package thebetweenlands.aspect.aspects;

import net.minecraft.nbt.NBTTagCompound;

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
