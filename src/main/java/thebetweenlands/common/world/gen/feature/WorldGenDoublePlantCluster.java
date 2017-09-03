package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.plant.BlockDoublePlantBL;

public class WorldGenDoublePlantCluster extends WorldGenerator {
	private final BlockDoublePlantBL block;
	private final int offset;
	private final int attempts;
	private boolean isUnderwater = false;

	public WorldGenDoublePlantCluster(BlockDoublePlantBL block, int offset, int attempts) {
		this.block = block;
		this.offset = offset;
		this.attempts = attempts;
	}

	public WorldGenDoublePlantCluster(BlockDoublePlantBL block) {
		this(block, 8, 128);
	}

	public WorldGenDoublePlantCluster setUnderwater(boolean underwater) {
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

			if ((worldIn.isAirBlock(blockpos) || (this.isUnderwater && worldIn.getBlockState(blockpos).getMaterial().isLiquid())) && (worldIn.provider.hasSkyLight() || blockpos.getY() < 254) && this.block.canPlaceBlockAt(worldIn, blockpos)) {
				this.block.placeAt(worldIn, blockpos, 2);
				generated = true;
			}
		}

		return generated;
	}
}