package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.item.misc.ItemBLNameTag;

public class RecipeRenameItem extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean hasNametag = false;
        boolean hasItem = false;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);

            if(stack.isEmpty())
                continue;

            if(stack.getItem() instanceof ItemBLNameTag) {
                if(stack.hasDisplayName()) {
                    hasNametag = true;
                } else {
                    return false;
                }
            } else {
                if(hasItem) {
                    return false;
                }

                hasItem = true;
            }
        }

        return hasNametag && hasItem;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack item = ItemStack.EMPTY;
        ItemStack nameTag = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);

            if(stack.isEmpty())
                continue;

            if(stack.getItem() instanceof ItemBLNameTag && stack.hasDisplayName()) {
                nameTag = stack;
            } else {
                item = stack;
            }

            if(nameTag != ItemStack.EMPTY && item != ItemStack.EMPTY) {
                break;
            }
        }

        ItemStack newStack = item.copy();
        newStack.setCount(1);
        newStack.setStackDisplayName(nameTag.getDisplayName());

        return newStack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            remaining.set(i, ForgeHooks.getContainerItem(itemstack));
        }

        return remaining;
    }
}
