package thebetweenlands.recipes.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;

public class RecipeLurkerSkinPouchUpgrades implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting craftMatrix, World world) {
		int size = craftMatrix.getSizeInventory();
		ItemStack is;

		if (size < 9)
			return false;

		for (int a = 0; a < size; a++) {
			if ((is = craftMatrix.getStackInSlot(a)) == null)
				return false;

			if (a == 4) {
				if (!(is.getItem() == BLItemRegistry.lurkerSkinPouch && (is.getItemDamage() < 5)))
					return false;
			} else if (!(is.getItem() == BLItemRegistry.itemsGeneric && is.getItemDamage() == EnumItemGeneric.LURKER_SKIN.id))
				return false;
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
		ItemStack is = null;

		for (int a = 0; a < craftMatrix.getSizeInventory(); a++) {
			is = craftMatrix.getStackInSlot(a);
			if (is != null && is.getItem() == BLItemRegistry.lurkerSkinPouch)
				break;
		}

		if (is == null)
			return null;
		is = is.copy();
		is.setItemDamage(is.getItemDamage() + 1);
		return is;
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(BLItemRegistry.lurkerSkinPouch);
	}
}
