package thebetweenlands.common.world.gen.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;

public class WorldGenSpeleothem extends WorldGenerator {
	private IBlockState speleothem = BlockRegistry.STALACTITE.getDefaultState();

	private boolean isBlockSupported(Block block) {
		return block == BlockRegistry.BETWEENSTONE;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		if (world.isAirBlock(pos) && isBlockSupported(world.getBlockState(pos.up()).getBlock())) {
			int height = 1;
			while (world.isAirBlock(pos.add(0, -height, 0))) {
				height++;
			}
			if (height < 3) {
				return false;
			}
			boolean hasStalagmite = isBlockSupported(world.getBlockState(pos.add(0, -height, 0)).getBlock());
			int length = rand.nextInt(height < 11 ? height / 2 : 5) + 1;
			boolean isColumn = length == height / 2;
			for (int dy = 0; dy < length; dy++) {
				this.setBlockAndNotifyAdequately(world, pos.add(0, -dy, 0), speleothem);
				if (hasStalagmite && (isColumn || dy < length - 1)) {
					this.setBlockAndNotifyAdequately(world, pos.add(0, -height + dy + 1, 0), speleothem);
				}
			}
			return true;
		}
		return false;
	}
}
