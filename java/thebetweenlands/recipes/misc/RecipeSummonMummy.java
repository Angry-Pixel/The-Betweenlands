package thebetweenlands.recipes.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.herblore.ItemAspectVial;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;

public class RecipeSummonMummy implements IRecipe {
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(InventoryCrafting crafter, World world) {
		ItemStack vial = null;
		ItemStack shimmerstone = null;
		ItemStack heart = null;
		ItemStack sludge = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() instanceof ItemAspectVial && AspectManager.getDynamicAspects(stack).size() == 1) {
					if(vial != null)
						return false;
					if(stack.stackSize > 1)
						return false;
					if(!AspectManager.getDynamicAspects(stack).get(0).type.equals(AspectRegistry.ARMANIIS) || AspectManager.getDynamicAspects(stack).get(0).amount < 1.0F) {
						return false;
					}
					vial = stack;
				} else if(stack.getItem() == BLItemRegistry.shimmerStone) {
					if(shimmerstone != null)
						return false;
					shimmerstone = stack;
				} else if(stack.getItem() instanceof ItemGeneric && stack.getItemDamage() == EnumItemGeneric.SLUDGE_BALL.id) {
					if(sludge != null)
						return false;
					sludge = stack;
				} else if(stack.getItem() instanceof ItemGeneric && stack.getItemDamage() == EnumItemGeneric.TAR_BEAST_HEART_ANIMATED.id) {
					if(heart != null)
						return false;
					heart = stack;
				} else {
					return false;
				}
			}
		}
		return vial != null && shimmerstone != null && heart != null && sludge != null;
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

		Aspect vialAspect = AspectManager.getDynamicAspects(vial).get(0);
		ItemStack newVial = new ItemStack(BLItemRegistry.aspectVial, vial.stackSize, vial.getItemDamage());
		Aspect newVialAspect = new Aspect(vialAspect.type, vialAspect.amount - 1.0F);
		AspectManager.addDynamicAspects(newVial, newVialAspect);
		if(vial.stackTagCompound == null)
			vial.stackTagCompound = new NBTTagCompound();
		vial.stackTagCompound.setTag("containerItem", newVial.writeToNBT(new NBTTagCompound()));

		return new ItemStack(BLItemRegistry.summonMummy);
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