package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;

public class RecipesCoating extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack tool = ItemStack.EMPTY;
		int coating = 0;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(EnumItemMisc.SCABYST.isItemOf(stack)) {
					coating++;
				} else if(stack.getItem() instanceof ICorrodible) {
					if(!tool.isEmpty()) {
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
		return coating > 0 && !tool.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		int coating = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
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
		NonNullList<ItemStack>  remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		int requiredCoating = 0;

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() instanceof ICorrodible) {
				ICorrodible corrodible = (ICorrodible) stack.getItem();
				requiredCoating += MathHelper.ceil(((float)corrodible.getMaxCoating(stack) - (float)corrodible.getCoating(stack)) / 75.0F);
			}
		}

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty() && EnumItemMisc.SCABYST.isItemOf(stack)) {
				if(requiredCoating > 0) {
					requiredCoating--;
				} else {
					remaining.set(i,stack.copy());
					remaining.get(i).setCount(1);
				}
			} else {
				remaining.set(i, ForgeHooks.getContainerItem(stack));
			}
		}

		return remaining;
	}
}