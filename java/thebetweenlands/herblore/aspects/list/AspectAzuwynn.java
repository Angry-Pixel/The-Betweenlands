package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspect;

public class AspectAzuwynn implements IAspect {
	@Override
	public String getName() {
		return "Azuwynn";
	}

	@Override
	public String getType() {
		return "Muscle";
	}

	@Override
	public String getDescription() {
		return "Has effect on the muscles, could either result in more damage, speed or maybe rapid fire and all stuff in that regard.";
	}
	
	@Override
	public int getIconIndex() {
		return 1;
	}
}
