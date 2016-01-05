package thebetweenlands.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.GemCircleHelper;
import thebetweenlands.items.misc.ItemGem;

public class RecipesCircleGems implements IRecipe {
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack tool = null;
		ItemStack gem = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemGem) {
					if(gem != null) {
						return false;
					}
					gem = stack;
				} else {
					if(!GemCircleHelper.isApplicable(stack)) {
						return false;
					} else {
						if(tool != null) {
							return false;
						}
						tool = stack;
					}
				}
			}
		}
		return (tool != null && gem != null) && (GemCircleHelper.getGem(tool) != ((ItemGem)gem.getItem()).getCircleGem());
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack tool = null;
		ItemStack gem = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemGem) {
					gem = stack;
				} else {
					if(GemCircleHelper.isApplicable(stack)) {
						tool = stack;
					}
				}
			}
		}
		if(tool != null && gem != null) {
			ItemStack result = tool.copy();
			if(result.stackTagCompound == null) {
				result.stackTagCompound = new NBTTagCompound();
			}
			CircleGem appliedGem = ((ItemGem)gem.getItem()).getCircleGem();
			CircleGem toolGem = GemCircleHelper.getGem(tool);
			int gemRelation = appliedGem.getRelation(toolGem);
			if(gemRelation == -1) {
				GemCircleHelper.setGem(result, CircleGem.NONE);
			} else {
				GemCircleHelper.setGem(result, ((ItemGem)gem.getItem()).getCircleGem());
			}
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