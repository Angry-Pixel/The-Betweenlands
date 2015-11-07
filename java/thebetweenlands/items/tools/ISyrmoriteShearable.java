package thebetweenlands.items.tools;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

public interface ISyrmoriteShearable {
	public ItemStack getSyrmoriteShearableSpecialDrops(Block block, int x, int y, int z, int meta);
	public boolean isSyrmoriteShearable(ItemStack item, IBlockAccess world, int x, int y, int z);
}
