package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class WorldGenWeedwoodBush extends WorldGenerator {
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		IBlockState blockState;
		Block block;
		MutableBlockPos offsetPos = new MutableBlockPos();
		int y = pos.getY();
		do {
			offsetPos.setPos(pos.getX(), y, pos.getZ());
			blockState = world.getBlockState(offsetPos);
			block = blockState.getBlock();
			if (!(block.isLeaves(blockState, world, offsetPos) || world.isAirBlock(offsetPos))) {
				break;
			}
			--y;
		} while (y > 0);
		++y;
		pos = new BlockPos(pos.getX(), y, pos.getZ());
		int x = pos.getX();
		int z = pos.getZ();

		this.setBlockAndNotifyAdequately(world, pos, BlockRegistry.WEEDWOOD_BUSH.getDefaultState());

		for (int by = y; by <= y + (world.rand.nextInt(2) == 0 ? world.rand.nextInt(4) + 1 : 1); ++by) {
			int yo = by - y;
			int radius = (int)((world.rand.nextInt(3) - yo) / (world.rand.nextFloat() * (yo * 2 + 1) * 0.1 + 1.0));

			for (int bx = x - radius; bx <= x + radius; ++bx) {
				int xo = bx - x;

				for (int bz = z - radius; bz <= z + radius; ++bz) {
					int zo = bz - z;

					offsetPos.setPos(bx, by, bz);

					IBlockState checkBlockState = world.getBlockState(offsetPos);

					if ((Math.abs(xo) != radius || Math.abs(zo) != radius || rand.nextInt(4) == 0) && checkBlockState.getBlock().canBeReplacedByLeaves(checkBlockState, world, offsetPos) && rand.nextInt((int)((xo*xo+zo*zo)*1.5+1)) < 2) {
						IBlockState blockStateBelow = world.getBlockState(offsetPos.setPos(bx, by - 1, bz));
						if(blockStateBelow.getBlock() == BlockRegistry.WEEDWOOD_BUSH || SurfaceType.GRASS_AND_DIRT.matches(blockStateBelow)) {
							this.setBlockAndNotifyAdequately(world, new BlockPos(bx, by, bz), BlockRegistry.WEEDWOOD_BUSH.getDefaultState());
						}
					}
				}
			}
		}

		return true;
	}
}