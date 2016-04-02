package thebetweenlands.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper {
	public static NBTTagCompound getStackNBTSafe(ItemStack stack) {
		if(stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		return stack.getTagCompound();
	}
}
