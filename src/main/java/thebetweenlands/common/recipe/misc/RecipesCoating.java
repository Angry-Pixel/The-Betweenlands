package thebetweenlands.common.recipe.misc;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;

public class RecipesCoating implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack tool = null;
		int coating = 0;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(EnumItemMisc.SCABYST.isItemOf(stack)) {
					coating++;
				} else if(stack.getItem() instanceof ICorrodible) {
					if(tool != null) {
						return false;
					}
					ICorrodible corrodible = (ICorrodible) stack.getItem();
					if(corrodible.getCoating(stack) >= corrodible.getMaxCoating(stack)) {
						return false;
					}
					tool = stack;
				} else {
					return false;
				}
			}
		}
		return coating > 0 && tool != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		int coating = 0;
		ItemStack tool = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ICorrodible) {
					tool = stack;
				} else if(EnumItemMisc.SCABYST.isItemOf(stack)) {
					coating++;
				}
			}
		}
		tool = tool.copy();
		ICorrodible corrodible = (ICorrodible) tool.getItem();
		corrodible.setCoating(tool, Math.min(corrodible.getMaxCoating(tool), corrodible.getCoating(tool) + coating * 75));
		return tool;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		int requiredCoating = 0;

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && stack.getItem() instanceof ICorrodible) {
				ICorrodible corrodible = (ICorrodible) stack.getItem();
				requiredCoating += MathHelper.ceil(((float)corrodible.getMaxCoating(stack) - (float)corrodible.getCoating(stack)) / 75.0F);
			}
		}

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && EnumItemMisc.SCABYST.isItemOf(stack)) {
				if(requiredCoating > 0) {
					requiredCoating--;
				} else {
					remaining.set(i, stack.copy());
					remaining.get(i).setCount(1);
					continue;
				}
			} else {
				remaining.set(i, ForgeHooks.getContainerItem(stack));
			}
		}

		return remaining;
	}
}