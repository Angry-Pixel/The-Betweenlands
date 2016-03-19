package thebetweenlands.recipes.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.equipment.ItemAmulet;
import thebetweenlands.items.misc.ItemGem;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;

public class RecipesAmulets implements IRecipe {
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack amulet = null;
		ItemStack gem = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemGem) {
					if(gem != null)
						return false;
					gem = stack;
				} else if(stack.getItem() == BLItemRegistry.amulet && CircleGem.getGem(stack) == CircleGem.NONE) {
					if(amulet != null)
						return false;
					amulet = stack;
				}
			}
		}
		return gem != null && amulet != null;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack gem = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemGem) {
					gem = stack;
				}
			}
		}
		if(gem != null) {
			CircleGem circleGem = ((ItemGem)gem.getItem()).getCircleGem();
			ItemStack result = ItemAmulet.createStack(circleGem);
			return result;
		}
		return null;
	}

	/**
	 * Returns the size of the recipe area
	 */
	public int getRecipeSize() {
		return 2;
	}

	public ItemStack getRecipeOutput() {
		return null;
	}
}