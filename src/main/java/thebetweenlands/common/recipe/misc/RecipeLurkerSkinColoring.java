package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeLurkerSkinColoring extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        int size = inventoryCrafting.getSizeInventory();

        if (size < 2) {
            return false;
        }

        boolean hasDye = false;
        boolean hasPouch = false;

        ItemStack is;

        for (int i = 0; i < size; i++) {
            is = inventoryCrafting.getStackInSlot(i);

            if (inventoryCrafting.getStackInSlot(i).isEmpty()) {
                continue;
            }

            if(hasPouch && hasDye) {
                return false;
            }

            if(is.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
                hasPouch = true;
            } else if(is.getItem() == ItemRegistry.DYE) {
                hasDye = true;
            }
        }

        return hasDye && hasPouch;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        ItemStack itemStackDye = ItemStack.EMPTY;
        ItemStack itemStackPouch = ItemStack.EMPTY;

        for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
            ItemStack tempStack = inventoryCrafting.getStackInSlot(i);

            if(tempStack.isEmpty())
                continue;

            if(tempStack.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
                itemStackPouch = tempStack;
            } else if(tempStack.getItem() == ItemRegistry.DYE) {
                itemStackDye = tempStack;
            }
        }

        if (itemStackDye.isEmpty() || itemStackPouch.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack newPouch = itemStackPouch.copy();

        if(!newPouch.hasTagCompound()) {
            newPouch.setTagCompound(new NBTTagCompound());
        }

        newPouch.getTagCompound().setInteger("type", itemStackDye.getMetadata());

        return newPouch;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ItemRegistry.LURKER_SKIN_POUCH);
    }

    @Override
    public boolean isDynamic() {
        return true;
    }


    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty() && (stack.getItem() == ItemRegistry.DYE)) {
                remaining.set(i, ItemMisc.EnumItemMisc.WEEDWOOD_BOWL.create(1));
            }
        }

        return remaining;
    }
}
