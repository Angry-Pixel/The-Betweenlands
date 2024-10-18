package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class LifeCrystalRechargeRecipe extends CustomRecipe {

	public LifeCrystalRechargeRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack crystal = null;
		int hearts = 0;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack checkedStack = input.getItem(i);
			if (!checkedStack.isEmpty()) {
				if (checkedStack.is(ItemRegistry.LIFE_CRYSTAL) && checkedStack.isDamaged()) {
					crystal = checkedStack;
				} else if (checkedStack.is(ItemRegistry.WIGHT_HEART)) {
					hearts++;
				}
			}
		}
		return crystal != null && hearts > 0;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		int hearts = 0;
		ItemStack crystal = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack itemstack = input.getItem(i);
			if (!itemstack.isEmpty()) {
				if (itemstack.is(ItemRegistry.LIFE_CRYSTAL)) {
					if (crystal.isEmpty()) {
						crystal = itemstack;
					} else {
						//Only accept 1 crystal
						return ItemStack.EMPTY;
					}
				} else if (itemstack.is(ItemRegistry.WIGHT_HEART)) {
					//add all hearts in the grid to a list to determine the amount to repair
					hearts++;
				}
			}
		}

		if (hearts > 0 && !crystal.isEmpty() && crystal.isDamaged()) {
			ItemStack newCrystal = ItemRegistry.LIFE_CRYSTAL.toStack();
			newCrystal.setDamageValue(Math.max(0, crystal.getDamageValue() - Mth.ceil(hearts * crystal.getMaxDamage() / 8.0F)));
			return newCrystal;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.LIFE_CRYSTAL_RECHARGE.get();
	}
}
