package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeMarshRunnerBoots extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		boolean vial = false;
		boolean boots = false;

		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.RUBBER_BOOTS) {
					if(boots)
						return false;
					boots = true;
				} else if(stack.getItem() == ItemRegistry.ASPECT_VIAL) {
					if(vial)
						return false;
					ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(stack);
					int amount = aspectContainer.get(AspectRegistry.BYRGINAZ);
					if(amount >= 1000)
						vial = true;
				}
			}
		}
		return vial && boots;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		return new ItemStack(ItemRegistry.MARSH_RUNNER_BOOTS);
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

		for (int i = 0; i < remaining.size() ;++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() == ItemRegistry.ASPECT_VIAL) {
				ItemStack newStack = stack.copy();
				ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(newStack);
				int leftAmount = aspectContainer.get(AspectRegistry.BYRGINAZ) - 1000;
				aspectContainer.set(AspectRegistry.BYRGINAZ, leftAmount);
				if (leftAmount <= 0) {
					remaining.set(i, ForgeHooks.getContainerItem(stack));
				} else {
					remaining.set(i, newStack);
				}
			} else {
				remaining.set(i, ForgeHooks.getContainerItem(stack));
			}
		}

		return remaining;
	}
}