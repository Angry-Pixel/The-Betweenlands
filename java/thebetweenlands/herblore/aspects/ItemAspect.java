package thebetweenlands.herblore.aspects;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.AspectRegistry.AspectEntry;

public class ItemAspect {
	public final IAspect aspect;
	public final float amount;

	public ItemAspect(IAspect aspect, float amount) {
		if(aspect == null) throw new RuntimeException("Aspect can't be null");
		this.aspect = aspect;
		this.amount = amount;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("aspect", this.aspect.getName());
		nbt.setFloat("amount", this.amount);
		return nbt;
	}

	public static ItemAspect readFromNBT(NBTTagCompound nbt) {
		String aspectName = nbt.getString("aspect");
		float amount = nbt.getFloat("amount");
		AspectEntry aspect = AspectRecipes.REGISTRY.getAspectEntryFromName(aspectName);
		if(aspect != null) {
			return new ItemAspect(aspect.aspect, amount);
		}
		return null;
	}
}
