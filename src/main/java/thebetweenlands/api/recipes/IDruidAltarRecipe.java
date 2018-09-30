package thebetweenlands.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public interface IDruidAltarRecipe {
	/**
	 * Returns whether this recipe contains the specified input item
	 * @param input The input stacks
	 * @return
	 */
	public boolean containsInputItem(ItemStack input);

	/**
	 * Returns whether this recipe matches the 4 input item stacks
	 * @param input The input stacks
	 * @return
	 */
	public boolean matchesInput(ItemStack[] input);

	/**
	 * Returns the output for the specified 4 input item stacks
	 * @param input The input stacks
	 * @return
	 */
	public ItemStack getOutput(ItemStack[] input);
	
	/**
	 * Called when the recipe starts crafting
	 * @param world The world
	 * @param pos The position of the druid altar
	 * @param input The input stacks
	 */
	public default void onStartCrafting(World world, BlockPos pos, ItemStack[] input) {
		
	}
	
	/**
	 * Called every tick while the recipe is crafting
	 * @param world The world
	 * @param pos The position of the druid altar
	 * @param input The input stacks
	 */
	public default void onCrafting(World world, BlockPos pos, ItemStack[] input) {
		
	}
	
	/**
	 * Called when the recipe has finished crafting.
	 * The default implementation removes the spawner below the druid altar
	 * @param world The world
	 * @param pos The position of the druid altar
	 * @param input The input stacks
	 * @param output The output stack
	 */
	public default void onCrafted(World world, BlockPos pos, ItemStack[] input, ItemStack output) {
		if (world.getBlockState(pos.down()).getBlock() == BlockRegistry.MOB_SPAWNER) {
			world.setBlockState(pos.down(), world.getBiome(pos).topBlock);
		}
	}
}
