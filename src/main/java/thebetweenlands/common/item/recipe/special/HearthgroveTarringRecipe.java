package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class HearthgroveTarringRecipe extends CustomRecipe {

	public HearthgroveTarringRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		boolean tar = false;
		int hearthgroveLogs = 0;
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(BlockRegistry.HEARTHGROVE_LOG.asItem())) {
					hearthgroveLogs++;
				} else if (stack.has(DataComponentRegistry.STORED_FLUID)) {
					if (tar) {
						return false;
					}
					var fluid = stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY);
					if (fluid.is(FluidTypeRegistry.TAR.get()) && fluid.getAmount() == FluidType.BUCKET_VOLUME) {
						tar = true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return tar && hearthgroveLogs > 0 && hearthgroveLogs <= 8;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		int tarredLogs = 0;
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.is(BlockRegistry.HEARTHGROVE_LOG.asItem())) {
					tarredLogs++;
				}
			}
		}
		return new ItemStack(BlockRegistry.TARRED_HEARTHGROVE_LOG, tarredLogs);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.HEARTHGROVE_TARRING.get();
	}
}
