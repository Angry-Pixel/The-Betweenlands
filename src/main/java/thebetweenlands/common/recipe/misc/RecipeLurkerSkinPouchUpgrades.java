package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeLurkerSkinPouchUpgrades  extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting craftMatrix, World world) {
		int size = craftMatrix.getSizeInventory();
		ItemStack is;

		if (size < 9) {
			return false;
		}

		for (int i = 0; i < size; i++) {
			if ((is = craftMatrix.getStackInSlot(i)).isEmpty()) {
				return false;
			}

			if (i == 4) {
				if (!(is.getItem() == ItemRegistry.LURKER_SKIN_POUCH && is.getItemDamage() < is.getMaxDamage())) {
					return false;
				}
			} else if (!EnumItemMisc.LURKER_SKIN.isItemOf(is)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
		ItemStack is = ItemStack.EMPTY;

		for (int a = 0; a < craftMatrix.getSizeInventory(); a++) {
			is = craftMatrix.getStackInSlot(a);
			if (!is.isEmpty() && is.getItem() == ItemRegistry.LURKER_SKIN_POUCH) {
				break;
			}
		}

		if (is.isEmpty()) {
			return ItemStack.EMPTY;
		}

		is = is.copy();
		is.setItemDamage(is.getItemDamage() + 1);
		return is;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 9;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ItemRegistry.LURKER_SKIN_POUCH);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack>  remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			remaining.set(i, ForgeHooks.getContainerItem(itemstack));
		}

		return remaining;
	}
}
