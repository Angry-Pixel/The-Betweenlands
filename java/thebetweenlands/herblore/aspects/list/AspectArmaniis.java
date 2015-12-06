package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspect;

public class AspectArmaniis implements IAspect {
	@Override
	public String getName() {
		return "Armaniis";
	}

	@Override
	public String getType() {
		return "Desire";
	}

	@Override
	public String getDescription() {
		return "Has effect on the desires of a mob or the player. Could be useful for food, but also things like trading or corrupting the desire.";
	}

	@Override
	public int getIconIndex() {
		return 0;
	}
}
