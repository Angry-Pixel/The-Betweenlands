package thebetweenlands.api.recipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface AnimatorRecipe<T extends RecipeInput> extends Recipe<T> {
	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	boolean matchesInput(ItemStack stack);

	/**
	 * Returns the amount of required fuel
	 * @param stack
	 * @return
	 */
	int getRequiredFuel(ItemStack stack);

	/**
	 * Returns the amount of required life crystal
	 * @param stack
	 * @return
	 */
	int getRequiredLife(ItemStack stack);

	/**
	 * Returns the entity to be rendered when animating the item
	 * @param stack
	 * @return
	 */
	@Nullable
	Entity getRenderEntity(ItemStack stack);

	/**
	 * Returns the resulting item when this recipe is finished
	 * @param stack
	 * @return
	 */
	ItemStack getResult(ItemStack stack, RegistryAccess access);

	/**
	 * Returns the entity that is spawned when this recipe is finished
	 * @param stack
	 * @return
	 */
	@Nullable
	Class<? extends Entity> getSpawnEntityClass(ItemStack stack);

	/**
	 * Called when the item is animated. Can return the resulting ItemStack (overrides {@link AnimatorRecipe#getResult()}).
	 * Also used to spawn entities from animator once animated
	 * @param level
	 * @param pos
	 * @param stack
	 * @return
	 */
	ItemStack onAnimated(Level level, BlockPos pos, ItemStack stack);

	/**
	 * Called when the animator has finished animating and is right clicked.
	 * Return true if GUI should be opened on first click
	 * @param player
	 * @param pos
	 * @param stack
	 */
	boolean onRetrieved(Player player, BlockPos pos, ItemStack stack);

	/**
	 * Returns whether the GUI should close when the animator has finished
	 * @param stack
	 * @return
	 */
	boolean getCloseOnFinish(ItemStack stack);
}
