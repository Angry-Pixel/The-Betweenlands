package thebetweenlands.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AdvancedRecipeHelper {
    public static ItemStack setContainerItem(ItemStack stack, ItemStack container, String recipe) {
        if(stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound tagCompound = stack.getTagCompound();
        tagCompound.setTag("containerItem." + recipe, container.writeToNBT(new NBTTagCompound()));
        stack.setTagCompound(tagCompound);
        return stack;
    }

    public static ItemStack getContainerItem(ItemStack stack, ItemStack defaultContainer, String recipe) {
        NBTTagCompound nbt = stack.getTagCompound() != null ? stack.getTagCompound() : new NBTTagCompound();
        boolean hasContainerItem = nbt.hasKey("containerItem." + recipe);
        ItemStack containerItem = hasContainerItem ? new ItemStack(nbt.getCompoundTag("containerItem." + recipe)) : ItemStack.EMPTY;
        if(hasContainerItem)
            nbt.removeTag("containerItem." + recipe);
        return hasContainerItem ? containerItem : defaultContainer;
    }
}
