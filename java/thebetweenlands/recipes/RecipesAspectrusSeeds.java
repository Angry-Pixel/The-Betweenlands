package thebetweenlands.recipes;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;

public class RecipesAspectrusSeeds implements IRecipe {
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack fruit = null;
		int vials = 0;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == BLItemRegistry.itemsGeneric && stack.getItemDamage() == EnumItemGeneric.ASPECTRUS_FRUIT.id && !AspectManager.getDynamicAspects(stack).isEmpty()) {
					if(fruit != null) {
						return false;
					}
					fruit = stack;
				} else {
					return false;
				}
			}
		}
		return fruit != null;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack fruit = null;
		int vials = 0;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == BLItemRegistry.itemsGeneric && stack.getItemDamage() == EnumItemGeneric.ASPECTRUS_FRUIT.id) {
					fruit = stack;
					break;
				}
			}
		}
		List<Aspect> aspects = AspectManager.getDynamicAspects(fruit);
		ItemStack newStack = new ItemStack(BLItemRegistry.aspectrusCropSeed, fruit.stackSize);
		AspectManager.addDynamicAspects(newStack, aspects.toArray(new Aspect[0]));
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