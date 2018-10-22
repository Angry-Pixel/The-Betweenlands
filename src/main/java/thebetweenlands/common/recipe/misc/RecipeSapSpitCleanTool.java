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
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeSapSpitCleanTool extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		int sap = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof ICorrodible) {
					if(!tool.isEmpty())
						return false;
					if(((ICorrodible) stack.getItem()).getCorrosion(stack) == 0)
						return false;
					tool = stack;
				} else if(stack.getItem() == ItemRegistry.SAP_SPIT) {
					sap++;
				} else {
					return false;
				}
			}
		}
		return sap > 0 && !tool.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		int sap = 0;
		ItemStack tool = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof ICorrodible) {
					tool = stack;
				} else if(stack.getItem() == ItemRegistry.SAP_SPIT) {
					sap++;
				}
			}
		}
		tool = tool.copy();
		ICorrodible corrodible = (ICorrodible) tool.getItem();
		corrodible.setCorrosion(tool, Math.max(0, corrodible.getCorrosion(tool) - MathHelper.ceil(sap * corrodible.getMaxCorrosion(tool) / 3.0F)));
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
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		int requiredSap = 0;

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof ICorrodible) {
					ICorrodible corrodible = (ICorrodible) stack.getItem();
					requiredSap += MathHelper.ceil(corrodible.getCorrosion(stack) / (corrodible.getMaxCorrosion(stack) / 3.0F));
				}
			}
		}

		for (int i = 0; i < remaining.size() ;++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() == ItemRegistry.SAP_SPIT) {
				if(requiredSap > 0) {
					requiredSap--;
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