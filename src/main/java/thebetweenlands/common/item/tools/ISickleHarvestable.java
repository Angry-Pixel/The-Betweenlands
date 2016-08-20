package thebetweenlands.common.item.tools;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ISickleHarvestable {
	public boolean isHarvestable(ItemStack item, IBlockAccess world, BlockPos pos);
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune);
}