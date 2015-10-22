package thebetweenlands.aspect.aspects;

import net.minecraft.nbt.NBTTagCompound;

public class AspectByariis implements IAspect {
	public String getName() {
		return "Byariis";
	}

	public String getType() {
		return "Corruption";
	}

	public String getDescription() {
		return "This effect can corrupt other effects, but even corrupt effects. So it could turn negative into positive, and positive into negative. so for example, if this effect gets combined with health it will do something negative to your health, but if this effect gets combined twice with health, it will corrupt itself and thus do something positive.";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
