package thebetweenlands.herblore.aspects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemAspect {
	public final IAspect aspect;
	public final float amount;

	public ItemAspect(IAspect aspect, float amount) {
		this.aspect = aspect;
		this.amount = amount;
	}
}
