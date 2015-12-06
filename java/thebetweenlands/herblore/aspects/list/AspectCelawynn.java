package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspect;

public class AspectCelawynn implements IAspect {
	@Override
	public String getName() {
		return "Celawynn";
	}

	@Override
	public String getType() {
		return "Stomach";
	}

	@Override
	public String getDescription() {
		return "Has effect on the stomach. So this could have effect on the hunger bar for example.";
	}

	@Override
	public int getIconIndex() {
		return 4;
	}
}
