package thebetweenlands.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.items.BLItemRegistry;

public class RecipeImprovedRubberBoots implements IRecipe {
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack boots = null;
		ItemStack vial = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == BLItemRegistry.aspectVial) {
					if(vial != null || stack.stackSize > 1 || AspectManager.getDynamicAspects(stack).isEmpty() || AspectManager.getDynamicAspects(stack).get(0).type != AspectRegistry.BYRGINAZ || AspectManager.getDynamicAspects(stack).get(0).getAmount() < 0.2F)
						return false;
					vial = stack;
				} else if(stack.getItem() == BLItemRegistry.rubberBoots) {
					if(boots != null) {
						return false;
					}
					boots = stack;
				} else {
					return false;
				}
			}
		}
		return vial != null && boots != null;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack boots = null;
		ItemStack vial = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == BLItemRegistry.aspectVial) {
					vial = stack;
				} else if(stack.getItem() == BLItemRegistry.rubberBoots) {
					boots = stack;
				}
			}
		}
		Aspect vialAspect = AspectManager.getDynamicAspects(vial).get(0);
		ItemStack newVial = new ItemStack(BLItemRegistry.aspectVial, vial.stackSize, vial.getItemDamage());
		Aspect newVialAspect = new Aspect(vialAspect.type, vialAspect.amount - 0.2F);
		AspectManager.addDynamicAspects(newVial, newVialAspect);
		if(vial.stackTagCompound == null)
			vial.stackTagCompound = new NBTTagCompound();
		vial.stackTagCompound.setTag("containerItem", newVial.writeToNBT(new NBTTagCompound()));
		return new ItemStack(BLItemRegistry.rubberBootsImproved);
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