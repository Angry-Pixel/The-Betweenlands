package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.terrain.BlockBetweenstonePebblePile;
import thebetweenlands.common.block.terrain.BlockBetweenstonePebblePileWater;

public class WorldGenPebbleCluster extends WorldGenerator {
	private final IBlockState blockState;
	private final Block block;
	private final int offset;
	private final int attempts;
	private boolean isUnderwater = false;

	public WorldGenPebbleCluster(IBlockState blockState, int offset, int attempts) {
		this.blockState = blockState;
		this.block = blockState.getBlock();
		this.offset = offset;
		this.attempts = attempts;
	}

	public WorldGenPebbleCluster(IBlockState blockState) {
		this(blockState, 8, 128);
	}

	public WorldGenPebbleCluster setUnderwater(boolean underwater) {
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
			
			if (state.getBlock().isAir(state, worldIn, blockpos) && this.block instanceof BlockBetweenstonePebblePile && this.block.canPlaceBlockAt(worldIn, blockpos)) {
				int randomPebbleType = rand.nextInt(BlockBetweenstonePebblePile.EnumPileType.values().length + 1);
				this.setBlockAndNotifyAdequately(worldIn, blockpos, this.blockState.withProperty(BlockBetweenstonePebblePile.PILE_TYPE, BlockBetweenstonePebblePile.EnumPileType.byMetadata(randomPebbleType)));
				generated = true;
			}

			if (this.isUnderwater && state.getMaterial().isLiquid() && state.getBlock().isReplaceable(worldIn, blockpos) && this.block instanceof BlockBetweenstonePebblePileWater && this.block.canPlaceBlockAt(worldIn, blockpos)) {
				int randomPebbleType = rand.nextInt(BlockBetweenstonePebblePileWater.EnumPileType.values().length + 1);
				this.setBlockAndNotifyAdequately(worldIn, blockpos, this.blockState.withProperty(BlockBetweenstonePebblePileWater.PILE_TYPE, BlockBetweenstonePebblePileWater.EnumPileType.byMetadata(randomPebbleType)));
				generated = true;
			}
		}

		return generated;
	}
}