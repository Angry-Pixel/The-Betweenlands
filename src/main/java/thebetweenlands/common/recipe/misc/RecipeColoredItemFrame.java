package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeColoredItemFrame extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int size = inv.getSizeInventory();

        if (size < 2) {
            return false;
        }

        boolean hasDye = false;
        boolean hasFrame = false;

        ItemStack is;

        for (int i = 0; i < size; i++) {
            is = inv.getStackInSlot(i);

            if (inv.getStackInSlot(i).isEmpty()) {
                continue;
            }

            if(hasFrame && hasDye) {
                return false;
            }

            if(is.getItem() == ItemRegistry.ITEM_FRAME && is.getMetadata() == EnumBLDyeColor.PEWTER_GREY.getMetadata()) {
                hasFrame = true;
            } else if(is.getItem() == ItemRegistry.DYE) {
                hasDye = true;
            }
        }

        return hasDye && hasFrame;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemStackDye = ItemStack.EMPTY;
        ItemStack itemStackFrame = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack tempStack = inv.getStackInSlot(i);

            if(tempStack.isEmpty())
                continue;

            if(tempStack.getItem() == ItemRegistry.ITEM_FRAME && tempStack.getMetadata() == EnumBLDyeColor.PEWTER_GREY.getMetadata()) {
                itemStackFrame = tempStack;
            } else if(tempStack.getItem() == ItemRegistry.DYE) {
                itemStackDye = tempStack;
            }
        }

        if (itemStackDye.isEmpty() || itemStackFrame.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(ItemRegistry.ITEM_FRAME, 1, itemStackDye.getMetadata());
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
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty() && (stack.getItem() == ItemRegistry.DYE)) {
                remaining.set(i, ItemMisc.EnumItemMisc.WEEDWOOD_BOWL.create(1));
            }
        }

        return remaining;
    }
}