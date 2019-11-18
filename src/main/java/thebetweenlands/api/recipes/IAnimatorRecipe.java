package thebetweenlands.api.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAnimatorRecipe {
	/**
	 * Returns whether this recipe matches the item stack
	 * @param stack
	 * @return
	 */
	public boolean matchesInput(ItemStack stack);

	/**
	 * Returns the amount of required fuel
	 * @param stack
	 * @return
	 */
	public int getRequiredFuel(ItemStack stack);

	/**
	 * Returns the amount of required life crystal
	 * @param stack
	 * @return
	 */
	public int getRequiredLife(ItemStack stack);

	/**
	 * Returns the entity to be rendered when animating the item
	 * @param stack
	 * @return
	 */
	@Nullable
	@SideOnly(Side.CLIENT)
	public Entity getRenderEntity(ItemStack stack);

	/**
	 * Returns the resulting item when this recipe is finished
	 * @param stack
	 * @return
	 */
	@Nonnull
	public ItemStack getResult(ItemStack stack);

	/**
	 * Returns the entity that is spawned when this recipe is finished
	 * @param stack
	 * @return
	 */
	@Nullable
	public Class<? extends Entity> getSpawnEntityClass(ItemStack stack);

	/**
	 * Called when the item is animated. Can return the resulting ItemStack (overrides {@link IAnimatorRecipe#getResult()}).
	 * Also used to spawn entities from animator once animated
	 * @param world
	 * @param pos
	 * @param stack
	 * @return
	 */
	@Nonnull
	public ItemStack onAnimated(World world, BlockPos pos, ItemStack stack);

	/**
	 * Use {@link #onRetrieved(EntityPlayer, BlockPos, ItemStack)} instead
	 */
	@Deprecated
	public boolean onRetrieved(World world, BlockPos pos, ItemStack stack);

	/**
	 * Called when the animator has finished animating and is right clicked.
	 * Return true if GUI should be opened on first click
	 * @param world
	 * @param pos
	 * @param stack
	 */
	public default boolean onRetrieved(EntityPlayer player, BlockPos pos, ItemStack stack) {
		return this.onRetrieved(player.world, pos, stack);
	}
	
	/**
	 * Returns whether the GUI should close when the animator has finished
	 * @param stack
	 * @return
	 */
	public boolean getCloseOnFinish(ItemStack stack);
}
