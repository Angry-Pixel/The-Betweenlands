package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspectType;

public class AspectFergalaz implements IAspectType {
	@Override
	public String getName() {
		return "Fergalaz";
	}

	@Override
	public String getType() {
		return "Earth";
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to earth. Any combination with this effect can be related to the element earth.";
	}

	@Override
	public int getIconIndex() {
		return 6;
	}

	@Override
	public int getColor() {
		return 0xFF29B539;
	}
}
