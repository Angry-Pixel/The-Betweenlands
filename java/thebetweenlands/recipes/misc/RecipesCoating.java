package thebetweenlands.recipes.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.utils.CorrodibleItemHelper;

public class RecipesCoating implements IRecipe {
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack tool = null;
		int coating = 0;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemGeneric && stack.getItemDamage() == EnumItemGeneric.SCABYST.id) {
					coating++;
				} else if(stack.getItem() instanceof ICorrodible) {
					if(tool != null)
						return false;
					tool = stack;
				} else {
					return false;
				}
			}
		}
		return coating == 8 && tool != null;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack tool = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null && stack.getItem() instanceof ICorrodible) {
				tool = stack;
				break;
			}
		}
		tool = tool.copy();
		CorrodibleItemHelper.setCoating(tool, 600);
		return tool;
	}

	/**
	 * Returns the size of the recipe area
	 */
	public int getRecipeSize() {
		return 9;
	}

	public ItemStack getRecipeOutput() {
		return null;
	}
}