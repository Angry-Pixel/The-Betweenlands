package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.api.capability.lifecrystal.ILifeCrystalHandler;
import thebetweenlands.common.capability.lifecrystal.LifeCrystalHelper;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class LifeCrystalRechargeRecipe extends CustomRecipe {

	public LifeCrystalRechargeRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack crystal = ItemStack.EMPTY;
		int hearts = 0;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			
			if (stack.is(ItemRegistry.WIGHT_HEART)) {
				hearts++;
			} else if(!crystal.isEmpty()) {
				return false;
			} else {
				ILifeCrystalHandler handler = LifeCrystalHelper.getLifeCrystalHandler(stack);
				if(handler == null || handler.getLifePower() >= handler.getMaxLifePower())
					return false;
				crystal = stack;
			}
		}
		return crystal != null && hearts > 0;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		int hearts = 0;
		ItemStack crystal = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(ItemRegistry.WIGHT_HEART)) {
					//add all hearts in the grid to a list to determine the amount to repair
					hearts++;
				} else {
					if (crystal.isEmpty()) {
						crystal = stack;
					} else {
						//Only accept 1 crystal
						return ItemStack.EMPTY;
					}
				}
			}
		}

		if (hearts == 0 || crystal.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ILifeCrystalHandler handler = LifeCrystalHelper.getLifeCrystalHandler(crystal);
		return LifeCrystalHelper.withLifeCharge(crystal, Mth.ceil(hearts * handler.getMaxLifePower() / 8.0F));
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
