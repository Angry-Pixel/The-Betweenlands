package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspectType;

public class AspectOrdaniis implements IAspectType {
	@Override
	public String getName() {
		return "Ordaniis";
	}

	@Override
	public String getType() {
		return "Enhance";
	}

	@Override
	public String getDescription() {
		return "Needs new decription";
	}

	@Override
	public int getIconIndex() {
		return 10;
	}

	@Override
	public int getColor() {
		return 0xFF64EF99;
	}
}
