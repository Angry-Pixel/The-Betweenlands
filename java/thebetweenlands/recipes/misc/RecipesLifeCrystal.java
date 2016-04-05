package thebetweenlands.recipes.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;

public class RecipesLifeCrystal implements IRecipe {
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting crafter, World world) {
		int hearts = 0;
		ItemStack crystal = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == BLItemRegistry.lifeCrystal) {
					if(crystal != null)
						return false;
					crystal = stack;
				} else if(stack.getItem() == BLItemRegistry.wightsHeart) {
					hearts++;
				} else {
					return false;
				}
			}
		}
		return hearts > 0 && crystal != null;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		int hearts = 0;
		ItemStack crystal = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == BLItemRegistry.lifeCrystal) {
					crystal = stack;
				} else if(stack.getItem() == BLItemRegistry.wightsHeart) {
					hearts++;
				}
			}
		}
		crystal = crystal.copy();
		crystal.setItemDamage(crystal.getItemDamage() - Math.max(0, MathHelper.ceiling_double_int(hearts * crystal.getMaxDamage() / 8.0F)));
		return crystal;
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