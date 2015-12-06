package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspect;

public class AspectFreiwynn implements IAspect {
	@Override
	public String getName() {
		return "Freiwynn";
	}

	@Override
	public String getType() {
		return "Vision";
	}

	@Override
	public String getDescription() {
		return "Alters the player's vision. (In combination with other properties. So for example when you combine health with vision, you would be able to spot mobs their health.)";
	}

	@Override
	public int getIconIndex() {
		return 8;
	}
}
