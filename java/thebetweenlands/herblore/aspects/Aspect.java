package thebetweenlands.herblore.aspects;

import net.minecraft.nbt.NBTTagCompound;

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
		IAspectType aspectType = AspectManager.getAspectTypeFromName(aspectName);
		if(aspectType != null) {
			return new Aspect(aspectType, amount);
		}
		return null;
	}
}
