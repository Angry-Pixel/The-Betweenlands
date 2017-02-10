package thebetweenlands.api.block;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ISickleHarvestable {
	/**
	 * Returns whether this block can be harvested by a sickle
	 * @param item
	 * @param world
	 * @param pos
	 * @return
	 */
	public boolean isHarvestable(ItemStack item, IBlockAccess world, BlockPos pos);

	/**
	 * Returns the drops from harvesting this block with a sickle
	 * @param item
	 * @param world
	 * @param pos
	 * @param fortune
	 * @return
	 */
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune);
}