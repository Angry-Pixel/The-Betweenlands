package thebetweenlands.api.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import thebetweenlands.common.items.recipe.MultiStackInput;
import thebetweenlands.common.registries.BlockRegistry;

public interface DruidAltarRecipe extends Recipe<MultiStackInput> {

	int processTime();

	/**
	 * Called when the recipe starts crafting
	 * @param level The world
	 * @param pos The position of the druid altar
	 * @param input The input stacks
	 */
	default void onStartCrafting(Level level, BlockPos pos, MultiStackInput input) {

	}

	/**
	 * Called every tick while the recipe is crafting
	 * @param level The world
	 * @param pos The position of the druid altar
	 * @param input The input stacks
	 */
	default void onCrafting(Level level, BlockPos pos, MultiStackInput input) {

	}

	/**
	 * Called when the recipe has finished crafting.
	 * The default implementation removes the spawner below the druid altar
	 * @param level The world
	 * @param pos The position of the druid altar
	 * @param input The input stacks
	 * @param output The output stack
	 */
	default void onCrafted(Level level, BlockPos pos, MultiStackInput input, ItemStack output) {
		if (level.getBlockState(pos.below()).is(BlockRegistry.MOB_SPAWNER)) {
			level.setBlockAndUpdate(pos.below(), Blocks.GRASS_BLOCK.defaultBlockState());
		}
	}
}
