package thebetweenlands.common.block.plant;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockBladderwortStalk extends BlockStackablePlantUnderwater {
	@Override
	protected boolean canGrowUp(World world, BlockPos pos, IBlockState state, int height) {
		return world.getBlockState(pos.up()) != this && 
				(world.getBlockState(pos.up()).getMaterial() == Material.WATER || (world.getBlockState(pos).getMaterial() == Material.WATER && world.isAirBlock(pos.up()))) 
				&& (this.maxHeight == -1 || height < this.maxHeight);
	}

	@Override
	protected void growUp(World world, BlockPos pos) {
		if(!world.getBlockState(pos.up()).getMaterial().isLiquid()) {
			world.setBlockState(pos.up(), BlockRegistry.BLADDERWORT_FLOWER.getDefaultState());
		} else {
			world.setBlockState(pos.up(), this.getDefaultState());
		}
	}
}
