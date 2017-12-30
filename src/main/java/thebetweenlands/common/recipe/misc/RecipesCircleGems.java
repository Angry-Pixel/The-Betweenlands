package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.misc.ItemGem;

public class RecipesCircleGems extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack tool = ItemStack.EMPTY;
		ItemStack gem = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof ItemGem) {
					if(!gem.isEmpty()) {
						return false;
					}
					gem = stack;
				} else {
					if(!CircleGemHelper.isApplicable(stack.getItem())) {
						return false;
					} else {
						if(!tool.isEmpty()) {
							return false;
						}
						tool = stack;
					}
				}
			}
		}
		return (!tool.isEmpty() && !gem.isEmpty()) && (CircleGemHelper.getGem(tool) != ((ItemGem)gem.getItem()).type);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack tool = ItemStack.EMPTY;
		ItemStack gem = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof ItemGem) {
					gem = stack;
				} else {
					if(CircleGemHelper.isApplicable(stack.getItem())) {
						tool = stack;
					}
				}
			}
		}
		if(!tool.isEmpty() && !gem.isEmpty()) {
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
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
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