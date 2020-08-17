package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeWeedwoodFishingRod extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private ItemStack resultItem = ItemStack.EMPTY;

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        this.resultItem = ItemStack.EMPTY;
        int hasRodItem = 0;
        int hasBaitItem = 0;
       // int hasReelItem = 0; NYI, but may be
      //  int hasFloatHookItem = 0;

		for (int inputSlot = 0; inputSlot < inv.getSizeInventory(); ++inputSlot) {
			ItemStack itemstack = inv.getStackInSlot(inputSlot);

			if (!itemstack.isEmpty()) {
				if (itemstack.getItem() == ItemRegistry.WEEDWOOD_FISHING_ROD)
					hasRodItem++;
				else if (itemstack.getItem() == ItemRegistry.TINY_SLUDGE_WORM || itemstack.getItem() == ItemRegistry.TINY_SLUDGE_WORM_HELPER)
					hasBaitItem++;
				else
					return false;
			}
		}

		if (hasBaitItem == 1) {
			for (int inputSlot2 = 0; inputSlot2 < inv.getSizeInventory(); ++inputSlot2) {
				ItemStack stack = inv.getStackInSlot(inputSlot2);

				if (stack.getItem() == ItemRegistry.WEEDWOOD_FISHING_ROD && stack.hasTagCompound()) {

					this.resultItem = stack.copy();

					if(stack.getTagCompound().getBoolean("baited"))
						return false;
					
					resultItem.getTagCompound().setBoolean("baited", true);
				}
			}
			return true;
		}
		return false;
	}

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv){
        return this.resultItem.copy();
    }

    @Override
    public ItemStack getRecipeOutput(){
        return this.resultItem;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv){
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i){
            ItemStack itemstack = inv.getStackInSlot(i);
            nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
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
