package thebetweenlands.common.item.tools;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

public interface ISickleHarvestable {
	public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z);
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune);
}