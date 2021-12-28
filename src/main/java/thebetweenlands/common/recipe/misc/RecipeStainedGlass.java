package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.block.misc.BlockFilteredSiltGlass;
import thebetweenlands.common.item.misc.ItemBLDye;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeStainedGlass extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(i == 4) {
                    if(!(stack.getItem() instanceof ItemBLDye))
                        return false;
                } else {
                    if(stack.getItem() != Item.getItemFromBlock(BlockRegistry.FILTERED_SILT_GLASS))
                        return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack dye = ItemStack.EMPTY;

        if(inv.getSizeInventory() == 9) {
            dye = inv.getStackInSlot(4);

            if(dye != ItemStack.EMPTY) {
                return new ItemStack(BlockRegistry.FILTERED_SILT_GLASS_STAINED, 8, dye.getMetadata());
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 9;
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
