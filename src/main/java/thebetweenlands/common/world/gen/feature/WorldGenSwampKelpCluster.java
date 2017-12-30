package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class WorldGenSwampKelpCluster extends WorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		boolean generated = false;

		for (IBlockState iblockstate = world.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, world, position) || iblockstate.getBlock().isLeaves(iblockstate, world, position)) && position.getY() > 0; iblockstate = world.getBlockState(position)) {
			position = position.down();
		}

		for (int i = 0; i < 128; ++i) {
			BlockPos pos = position.add(rand.nextInt(10) - rand.nextInt(10), rand.nextInt(8) - rand.nextInt(8), rand.nextInt(10) - rand.nextInt(10));

			if(SurfaceType.WATER.matches(world, pos.up()) && SurfaceType.DIRT.matches(world, pos)) {
				if(this.generateSwampKelpStack(world, rand, pos.up()))
					generated = true;
			}
		}

		return generated;
	}

	private boolean generateSwampKelpStack(World world, Random rand, BlockPos pos) {
		int height = 0;
		MutableBlockPos checkPos = new MutableBlockPos();
		for(int yo = 0; yo < 128; yo++) {
			checkPos.setPos(pos.getX(), pos.getY() + yo, pos.getZ());
			if(!SurfaceType.WATER.matches(world, checkPos)) {
				height = yo;
				break;
			}
		}
		if(height < 2)
			return false;
		height = Math.min(height, 7);
		height = rand.nextInt(height) + 1;
		for(int yo = 0; yo < height; yo++) {
			BlockPos offsetPos = pos.add(0, yo, 0);
			IBlockState state = world.getBlockState(offsetPos);
			if(SurfaceType.WATER.matches(state)) {
				world.setBlockState(offsetPos, BlockRegistry.SWAMP_KELP.getDefaultState(), 2 | 16);
				//this.setBlockAndNotifyAdequately(world, offsetPos, BlockRegistry.SWAMP_KELP.getDefaultState());
			}
		}
		return true;
	}
}