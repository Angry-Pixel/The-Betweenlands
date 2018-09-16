package thebetweenlands.common.block.farming;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityDugSoil;

public class BlockDugDirt extends BlockGenericDugSoil {
	public BlockDugDirt(boolean purified) {
		super(Material.GROUND, purified);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);

		if(!world.isRemote) {
			TileEntityDugSoil te = getTile(world, pos);

			if(te != null && !te.isComposted() && rand.nextInt(20) == 0) {
				if(this.isPurified(world, pos, state)) {
					world.setBlockState(pos, BlockRegistry.PURIFIED_SWAMP_DIRT.getDefaultState());
				} else {
					world.setBlockState(pos, BlockRegistry.SWAMP_DIRT.getDefaultState());
				}
			}
		}
	}

	@Override
	public IBlockState getUnpurifiedDugSoil(World world, BlockPos pos, IBlockState state) {
		return BlockRegistry.DUG_SWAMP_DIRT.getDefaultState().withProperty(COMPOSTED, state.getValue(COMPOSTED)).withProperty(DECAYED, state.getValue(DECAYED));
	}
}
