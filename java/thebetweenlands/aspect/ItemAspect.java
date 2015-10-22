package thebetweenlands.aspect;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thebetweenlands.aspect.aspects.IAspect;

public class ItemAspect {
	public final IAspect aspect;
	public final float amount;

	public ItemAspect(IAspect aspect, float amount) {
		this.aspect = aspect;
		this.amount = amount;
	}
	
	public void readFromNBT(NBTTagCompound tagCompound) {
		NBTTagCompound aspectData = new NBTTagCompound();
		NBTTagList aspectList = aspectData.getTagList("aspectList", 0);
	}

	public void writeToNBT(NBTTagCompound tagCompound) {
		
	}
}
