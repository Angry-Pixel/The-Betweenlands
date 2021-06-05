package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenPlantCluster extends WorldGenerator {
	private final IBlockState blockState;
	private final Block block;
	private final int offset;
	private final int attempts;
	private boolean isUnderwater = false;

	public WorldGenPlantCluster(IBlockState blockState, int offset, int attempts) {
		this.blockState = blockState;
		this.block = blockState.getBlock();
		this.offset = offset;
		this.attempts = attempts;
	}
	
	public WorldGenPlantCluster(IBlockState blockState) {
		this(blockState, 8, 128);
	}
	
	public WorldGenPlantCluster setUnderwater(boolean underwater) {
		this.isUnderwater = underwater;
		return this;
	}

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		boolean generated = false;
		
		for (IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position)) {
			position = position.down();
		}

		for (int i = 0; i < this.attempts; ++i) {
			BlockPos blockpos = position.add(rand.nextInt(this.offset) - rand.nextInt(this.offset), rand.nextInt(this.offset/2+1) - rand.nextInt(this.offset/2+1), rand.nextInt(this.offset) - rand.nextInt(this.offset));

			IBlockState state = worldIn.getBlockState(blockpos);
			
			if ((state.getBlock().isAir(state, worldIn, blockpos) || (this.isUnderwater && state.getMaterial().isLiquid() && state.getBlock().isReplaceable(worldIn, blockpos))) && this.block.canPlaceBlockAt(worldIn, blockpos)) {
				this.setBlockAndNotifyAdequately(worldIn, blockpos, this.blockState);
				generated = true;
			}
		}

		return generated;
	}
}