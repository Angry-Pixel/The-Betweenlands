package thebetweenlands.aspect;

import net.minecraft.nbt.NBTTagCompound;

public class AspectFreiwynn implements IAspect {
	public String getName() {
		return "Freiwynn";
	}

	public String getType() {
		return "Vision";
	}

	public String getDescription() {
		return "Alters the player's vision. (In combination with other properties. So for example when you combine health with vision, you would be able to spot mobs their health.)";
	}

	public void readFromNBT(NBTTagCompound tagCompound) {

	}

	public void writeToNBT(NBTTagCompound tagCompound) {

	}
}
