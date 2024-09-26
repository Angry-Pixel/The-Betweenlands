package thebetweenlands.api.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.RecipeRegistry;

import javax.annotation.Nullable;

public interface AnimatorRecipe extends Recipe<SingleRecipeInput> {

	/**
	 * Returns the amount of required fuel
	 * @param input
	 * @return
	 */
	int getRequiredFuel(SingleRecipeInput input);

	/**
	 * Returns the amount of required life crystal
	 * @param input
	 * @return
	 */
	int getRequiredLife(SingleRecipeInput input);

	/**
	 * Returns the entity to be rendered when animating the item
	 * @param input
	 * @return
	 */
	@Nullable
	Entity getRenderEntity(SingleRecipeInput input, Level level);

	/**
	 * Returns the entity that is spawned when this recipe is finished
	 * @param input
	 * @return
	 */
	@Nullable
	EntityType<?> getSpawnEntity(SingleRecipeInput input);

	@Nullable
	EntityType<?> getSpawnEntity();

	/**
	 * Called when the item is animated. Can return the resulting ItemStack (overrides {@link AnimatorRecipe#assemble(RecipeInput, HolderLookup.Provider)}).
	 * Also used to spawn entities from animator once animated
	 * @param level
	 * @param pos
	 * @param input
	 * @return
	 */
	ItemStack onAnimated(ServerLevel level, BlockPos pos, SingleRecipeInput input);

	/**
	 * Called when the animator has finished animating and is right-clicked.
	 * Return true if GUI should be opened on first click
	 * @param player
	 * @param pos
	 * @param input
	 */
	boolean onRetrieved(Player player, BlockPos pos, SingleRecipeInput input);

	/**
	 * Returns whether the GUI should close when the animator has finished
	 * @param input
	 * @return
	 */
	boolean getCloseOnFinish(SingleRecipeInput input);

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	default RecipeType<?> getType() {
		return RecipeRegistry.ANIMATOR_RECIPE.get();
	}

	@Override
	default boolean isIncomplete() {
		return true;
	}
}
