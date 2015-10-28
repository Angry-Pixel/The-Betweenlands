package thebetweenlands.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public interface ISyrmoriteShearable {
	/**
	 * Can return a special item instead of the default block item.
	 * Return null if not needed.
	 * @param block
	 * @param x
	 * @param y
	 * @param z
	 * @param meta
	 * @return
	 */
	public ItemStack getSpecialDrop(Block block, int x, int y, int z, int meta);
}
