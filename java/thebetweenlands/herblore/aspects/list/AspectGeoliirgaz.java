package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspectType;

public class AspectGeoliirgaz implements IAspectType {
	@Override
	public String getName() {
		return "Geoliirgaz";
	}

	@Override
	public String getType() {
		return "Void";
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to the void or ether. Any combination with this effect can be related to void or darkness.";
	}

	@Override
	public int getIconIndex() {
		return 9;
	}
}
