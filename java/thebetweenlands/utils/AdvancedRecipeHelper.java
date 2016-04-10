package thebetweenlands.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AdvancedRecipeHelper {
	public static ItemStack setContainerItem(ItemStack stack, ItemStack container, String recipe) {
		if(stack.stackTagCompound == null)
			stack.stackTagCompound = new NBTTagCompound();
		stack.stackTagCompound.setTag("containerItem." + recipe, container.writeToNBT(new NBTTagCompound()));
		return stack;
	}

	public static ItemStack getContainerItem(ItemStack stack, ItemStack defaultContainer, String recipe) {
		NBTTagCompound nbt = stack.stackTagCompound != null ? stack.stackTagCompound : new NBTTagCompound();
		boolean hasContainerItem = nbt.hasKey("containerItem." + recipe);
		ItemStack containerItem = hasContainerItem ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("containerItem." + recipe)) : null;
		if(hasContainerItem)
			nbt.removeTag("containerItem." + recipe);
		return hasContainerItem ? containerItem : defaultContainer;
	}
}
