package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeOlmletteMixture extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private ItemStack resultItem = ItemStack.EMPTY;

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        resultItem = ItemStack.EMPTY;
        int hasFlathead = 0;
        int hasBlackHat = 0;
        int hasEggs = 0;
        int hasStick = 0;
        int hasBowl= 0;

		for (int inputSlot = 0; inputSlot < inv.getSizeInventory(); ++inputSlot) {
			ItemStack itemstack = inv.getStackInSlot(inputSlot);

			if (!itemstack.isEmpty()) {
				if (itemstack.getItem() == ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM)
					hasFlathead++;
				if(itemstack.getItem() == ItemRegistry.BLACK_HAT_MUSHROOM_ITEM)
					hasBlackHat++;
				if(itemstack.getItem() == ItemRegistry.OLM_EGG_RAW)
					hasEggs++;
				if(EnumItemMisc.WEEDWOOD_STICK.isItemOf(itemstack))
					hasStick++;
				if(EnumItemMisc.WEEDWOOD_BOWL.isItemOf(itemstack))
					hasBowl++;
			}
		}

		if (hasFlathead >= 1 && hasBlackHat >= 1 && hasEggs >= 2 && hasStick >= 1 && hasBowl >= 1) {
			resultItem = EnumItemMisc.OLMLETTE_MIXTURE.create(1);
			return true;
		}
		return false;
	}

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv){
        return resultItem.copy();
    }

    @Override
    public ItemStack getRecipeOutput(){
        return resultItem;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv){
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i){
            ItemStack itemstack = inv.getStackInSlot(i);
			if (!itemstack.isEmpty()) {
				if(EnumItemMisc.WEEDWOOD_STICK.isItemOf(itemstack))
					itemstack.setCount(itemstack.getCount() + 1);

				if(EnumItemMisc.WEEDWOOD_BOWL.isItemOf(itemstack))
					itemstack.setCount(itemstack.getCount() + 1);
			}
        }
        return nonnulllist;
    }

    @Override
    public boolean isDynamic(){
        return true;
    }

    @Override
    public boolean canFit(int width, int height){
        return width * height >= 1;
    }
}
