package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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
		crystal.setItemDamage(Math.max(0, crystal.getItemDamage() - MathHelper.ceiling_double_int(hearts * crystal.getMaxDamage() / 8.0F)));
		return crystal;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack[] remaining = new ItemStack[inv.getSizeInventory()];

		int requiredHearts = 0;

		for (int i = 0; i < remaining.length; ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == ItemRegistry.LIFE_CRYSTAL) {
					requiredHearts += MathHelper.ceiling_float_int(stack.getItemDamage() / (stack.getMaxDamage() / 8.0F));
				}
			}
		}

		for (int i = 0; i < remaining.length; ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && stack.getItem() == ItemRegistry.WIGHT_HEART) {
				if(requiredHearts > 0) {
					requiredHearts--;
				} else {
					remaining[i] = stack.copy();
					remaining[i].stackSize = 1;
					continue;
				}
			} else {
				remaining[i] = ForgeHooks.getContainerItem(stack);
			}
		}

		return remaining;
	}
}