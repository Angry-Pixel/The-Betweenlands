package thebetweenlands.recipes.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.items.herblore.ItemAspectVial;
import thebetweenlands.items.herblore.ItemDentrothystVial;

public class RecipesAspectVials implements IRecipe {
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack vial = null;
		ItemStack empty = null;
		int vials = 0;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemAspectVial && AspectManager.getDynamicAspects(stack).size() == 1) {
					if(stack.stackSize > 1)
						return false;
					if(vial != null) {
						if(!AspectManager.getDynamicAspects(vial).get(0).type.equals(AspectManager.getDynamicAspects(stack).get(0).type)) {
							return false;
						}
					}
					vial = stack;
					vials++;
				} else if(stack.getItem() instanceof ItemDentrothystVial && (stack.getItemDamage() == 0 || stack.getItemDamage() == 2)) {
					if(empty != null) {
						if(empty.getItemDamage() != stack.getItemDamage()) {
							return false;
						}
					}
					empty = stack;
				} else {
					return false;
				}
			}
		}
		return (vial != null && vials >= 1 && empty != null)/* || (vial != null && empty == null && vials == 1)*/;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		List<ItemStack> emptyVials = new ArrayList<ItemStack>();
		List<ItemStack> vials = new ArrayList<ItemStack>();
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemAspectVial) {
					vials.add(stack);
				} else if(stack.getItem() instanceof ItemDentrothystVial) {
					emptyVials.add(stack);
				}
			}
		}
		ItemStack firstVial = vials.get(0);
		if(emptyVials.size() == 0 && vials.size() == 1) {
			return null;
		} else {
			for(ItemStack emptyVial : emptyVials) {
				if(emptyVial.stackTagCompound != null)
					emptyVial.stackTagCompound.removeTag("containerItem");
			}
			for(ItemStack vial : vials) {
				if(vial.stackTagCompound != null)
					vial.stackTagCompound.removeTag("containerItem");
			}
			ItemStack firstEmptyVial = emptyVials.get(0);
			IAspectType type = AspectManager.getDynamicAspects(firstVial).get(0).type;
			float amount = 0.0F;
			for(ItemStack stack : vials) {
				amount += AspectManager.getDynamicAspects(stack).get(0).getAmount() * stack.stackSize;
			}
			ItemStack newStack = new ItemStack(firstVial.getItem(), emptyVials.size(), firstEmptyVial.getItemDamage() == 2 ? 1 : 0);
			AspectManager.addDynamicAspects(newStack, new Aspect(type, amount / emptyVials.size()));
			return newStack;
		}
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