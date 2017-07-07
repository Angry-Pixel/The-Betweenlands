package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipesLifeCrystal implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		int hearts = 0;
		ItemStack crystal = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == ItemRegistry.LIFE_CRYSTAL) {
					if(crystal != null)
						return false;
					if(stack.getItemDamage() == 0)
						return false;
					crystal = stack;
				} else if(stack.getItem() == ItemRegistry.WIGHT_HEART) {
					hearts++;
				} else {
					return false;
				}
			}
		}
		return hearts > 0 && crystal != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		int hearts = 0;
		ItemStack crystal = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == ItemRegistry.LIFE_CRYSTAL) {
					crystal = stack;
				} else if(stack.getItem() == ItemRegistry.WIGHT_HEART) {
					hearts++;
				}
			}
		}
		crystal = crystal.copy();
		crystal.setItemDamage(Math.max(0, crystal.getItemDamage() - MathHelper.ceil(hearts * crystal.getMaxDamage() / 8.0F)));
		return crystal;
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

		int requiredHearts = 0;

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == ItemRegistry.LIFE_CRYSTAL) {
					requiredHearts += MathHelper.ceil(stack.getItemDamage() / (stack.getMaxDamage() / 8.0F));
				}
			}
		}

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && stack.getItem() == ItemRegistry.WIGHT_HEART) {
				if(requiredHearts > 0) {
					requiredHearts--;
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