package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.misc.ItemGem;

public class RecipesCircleGems implements IRecipe {
	@Override
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
					if(!CircleGemHelper.isApplicable(stack.getItem())) {
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
		return (tool != null && gem != null) && (CircleGemHelper.getGem(tool) != ((ItemGem)gem.getItem()).type);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack tool = null;
		ItemStack gem = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemGem) {
					gem = stack;
				} else {
					if(CircleGemHelper.isApplicable(stack.getItem())) {
						tool = stack;
					}
				}
			}
		}
		if(tool != null && gem != null) {
			ItemStack result = tool.copy();
			CircleGemType appliedGem = ((ItemGem)gem.getItem()).type;
			CircleGemType toolGem = CircleGemHelper.getGem(tool);
			int gemRelation = appliedGem.getRelation(toolGem);
			if(gemRelation == -1) {
				CircleGemHelper.setGem(result, CircleGemType.NONE);
			} else {
				CircleGemHelper.setGem(result, appliedGem);
			}
			return result;
		}
		return null;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack[] remaining = new ItemStack[inv.getSizeInventory()];

		for (int i = 0; i < remaining.length; ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			remaining[i] = ForgeHooks.getContainerItem(itemstack);
		}

		return remaining;
	}
}