package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipesLifeCrystal extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		int hearts = 0;
		ItemStack crystal = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.LIFE_CRYSTAL) {
					if(!crystal.isEmpty())
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
		return hearts > 0 && !crystal.isEmpty();
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		int hearts = 0;
		ItemStack crystal = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
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

		int requiredHearts = 0;

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.LIFE_CRYSTAL) {
					requiredHearts += MathHelper.ceil(stack.getItemDamage() / (stack.getMaxDamage() / 8.0F));
				}
			}
		}

		for (int i = 0; i < remaining.size() ;++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() == ItemRegistry.WIGHT_HEART) {
				if(requiredHearts > 0) {
					requiredHearts--;
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