package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspectType;

public class AspectFirnalaz implements IAspectType {
	@Override
	public String getName() {
		return "Firnalaz";
	}

	@Override
	public String getType() {
		return "Fire";
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to fire. Any combination with this effect can be related to fire.";
	}

	@Override
	public int getIconIndex() {
		return 7;
	}

	@Override
	public int getColor() {
		return 0xFFFF7F00;
	}
}
