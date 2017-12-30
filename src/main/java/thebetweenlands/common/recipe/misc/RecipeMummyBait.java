package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeMummyBait extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		boolean vial = false;
		boolean shimmerstone = false;
		boolean heart = false;
		boolean sludge = false;

		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.SHIMMER_STONE) {
					if(shimmerstone)
						return false;
					shimmerstone = true;
				} else if(EnumItemMisc.TAR_BEAST_HEART_ANIMATED.isItemOf(stack)) {
					if(heart)
						return false;
					heart = true;
				} else if(stack.getItem() == ItemRegistry.SLUDGE_BALL) {
					if(sludge)
						return false;
					sludge = true;
				} else if(stack.getItem() == ItemRegistry.ASPECT_VIAL) {
					if(vial)
						return false;
					ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(stack);
					int amount = aspectContainer.get(AspectRegistry.ARMANIIS);
					if(amount >= 1000)
						vial = true;
				}
			}
		}
		return vial && shimmerstone && heart && sludge;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		return new ItemStack(ItemRegistry.MUMMY_BAIT);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 4;
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
				int leftAmount = aspectContainer.get(AspectRegistry.ARMANIIS) - 1000;
				aspectContainer.set(AspectRegistry.ARMANIIS, leftAmount);
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