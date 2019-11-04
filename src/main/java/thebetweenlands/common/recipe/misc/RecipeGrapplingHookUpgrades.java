package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipeGrapplingHookUpgrades  extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting craftMatrix, World world) {
		int size = craftMatrix.getSizeInventory();

		if (size < 3) {
			return false;
		}

		ItemStack hook = ItemStack.EMPTY;
		int tongues = 0;
		int teeth = 0;

		for (int i = 0; i < size; i++) {
			ItemStack stack = craftMatrix.getStackInSlot(i);

			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.GRAPPLING_HOOK) {
					hook = stack;
				} else if(stack.getItem() == ItemRegistry.SHAMBLER_TONGUE) {
					tongues++;
				} else if(EnumItemMisc.ANGLER_TOOTH.isItemOf(stack)) {
					teeth++;
				}
			}
		}

		return !hook.isEmpty() && hook.getItemDamage() < hook.getMaxDamage() && tongues > 0 && teeth >= 2;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
		ItemStack hook = ItemStack.EMPTY;
		int tongues = 0;
		int teeth = 0;

		for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
			ItemStack stack = craftMatrix.getStackInSlot(i);

			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.GRAPPLING_HOOK) {
					hook = stack;
				} else if(stack.getItem() == ItemRegistry.SHAMBLER_TONGUE) {
					tongues++;
				} else if(EnumItemMisc.ANGLER_TOOTH.isItemOf(stack)) {
					teeth++;
				}
			}
		}

		int nodesPerUpgrade = 3;
		
		int newDamage = Math.min(hook.getItemDamage() + Math.min(tongues, teeth / 2) * nodesPerUpgrade, hook.getMaxDamage());

		hook = hook.copy();
		hook.setItemDamage(newDamage);

		return hook;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 3;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ItemRegistry.GRAPPLING_HOOK);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		ItemStack hook = ItemStack.EMPTY;
		int tongues = 0;
		int teeth = 0;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);

			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.GRAPPLING_HOOK) {
					hook = stack;
				} else if(stack.getItem() == ItemRegistry.SHAMBLER_TONGUE) {
					tongues++;
				} else if(EnumItemMisc.ANGLER_TOOTH.isItemOf(stack)) {
					teeth++;
				}
			}
		}

		int nodesPerUpgrade = 3;
		
		int newDamage = Math.min(hook.getItemDamage() + Math.min(tongues, teeth / 2) * nodesPerUpgrade, hook.getMaxDamage());

		int upgrades = MathHelper.ceil((newDamage - hook.getItemDamage()) / (float)nodesPerUpgrade);

		int tonguesToRemove = upgrades;
		int teethToRemove = upgrades * 2;

		NonNullList<ItemStack>  remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);

			boolean consume = true;

			if(stack.getItem() == ItemRegistry.SHAMBLER_TONGUE) {
				if(tonguesToRemove > 0) {
					tonguesToRemove--;
				} else {
					consume = false;
				}
			} else if(EnumItemMisc.ANGLER_TOOTH.isItemOf(stack)) {
				if(teethToRemove > 0) {
					teethToRemove--;
				} else {
					consume = false;
				}
			}

			if(consume) {
				remaining.set(i, ForgeHooks.getContainerItem(stack));
			} else {
				stack = stack.copy();
				stack.setCount(1);
				remaining.set(i, stack);
			}
		}

		return remaining;
	}
}
