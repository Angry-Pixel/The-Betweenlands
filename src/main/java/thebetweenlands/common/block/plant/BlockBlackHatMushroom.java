package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockBlackHatMushroom extends BlockMushroomBetweenlands {
	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.BLACK_HAT_MUSHROOM_ITEM;
	}

	@Override
	public boolean isShearable(ItemStack item, IWorldReader world, BlockPos pos) {
		return false;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}
}
