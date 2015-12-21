package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspectType;

public class AspectByariis implements IAspectType {
	@Override
	public String getName() {
		return "Byariis";
	}

	@Override
	public String getType() {
		return "Corruption";
	}

	@Override
	public String getDescription() {
		return "This effect can corrupt other effects, but even corrupt effects. So it could turn negative into positive, and positive into negative. so for example, if this effect gets combined with health it will do something negative to your health, but if this effect gets combined twice with health, it will corrupt itself and thus do something positive.";
	}
	
	@Override
	public int getIconIndex() {
		return 2;
	}
}
