package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspectType;

public class AspectYeowynn implements IAspectType {
	@Override
	public String getName() {
		return "Yeowynn";
	}

	@Override
	public String getType() {
		return "Health";
	}

	@Override
	public String getDescription() {
		return "Has effect on the health bar, could be both negative or positive, depending on the combination.";
	}

	@Override
	public int getIconIndex() {
		return 11;
	}
}
