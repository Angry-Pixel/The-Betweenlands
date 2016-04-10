package thebetweenlands.recipes.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.herblore.ItemAspectVial;

public class RecipesAspectVials implements IRecipe {
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack vial = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemAspectVial && AspectManager.getDynamicAspects(stack).size() == 1) {
					if(vial != null) {
						return false;
					}
					vial = stack;
				} else {
					return false;
				}
			}
		}
		return vial != null;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack vial = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemAspectVial && AspectManager.getDynamicAspects(stack).size() == 1) {
					vial = stack;
				}
			}
		}
		ItemStack newStack = new ItemStack(BLItemRegistry.dentrothystVial, vial.stackSize, vial.getItemDamage() == 1 ? 2 : 0);
		return newStack;
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