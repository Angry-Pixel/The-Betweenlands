package thebetweenlands.herblore.aspects.list;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.IAspect;

public class AspectDayuniis implements IAspect {
	@Override
	public String getName() {
		return "Dayuniis";
	}

	@Override
	public String getType() {
		return "Mind";
	}

	@Override
	public String getDescription() {
		return "Has effect on the player's mind and on how senses work. Could be positive, or negative (think nausea/schizophrenia).";
	}

	@Override
	public int getIconIndex() {
		return 5;
	}
}
