package thebetweenlands.herblore.aspects;

import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.herblore.aspects.AspectManager.AspectEntry;

public class Aspect {
	public final IAspectType aspect;
	public final float amount;

	public Aspect(IAspectType aspect, float amount) {
		if(aspect == null) throw new RuntimeException("Aspect can't be null");
		this.aspect = aspect;
		this.amount = amount;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("aspect", this.aspect.getName());
		nbt.setFloat("amount", this.amount);
		return nbt;
	}

	public static Aspect readFromNBT(NBTTagCompound nbt) {
		String aspectName = nbt.getString("aspect");
		float amount = nbt.getFloat("amount");
		AspectEntry aspect = AspectManager.getAspectEntryFromName(aspectName);
		if(aspect != null) {
			return new Aspect(aspect.aspect, amount);
		}
		return null;
	}
}
