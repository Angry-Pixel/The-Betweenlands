package thebetweenlands.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Class for cutting down repeated logic in Features. Feel free to repurpose for other worldgen features.
 */
public class WorldGenUtil {

	//TODO: Could be MutableBlockPos, but I think this is also handled best in PlacedFeature
	public static BlockPos loopUntilSolid(WorldGenLevel level, BlockPos origin) {
		BlockPos.MutableBlockPos mutable = origin.mutable();
		for (BlockState state = level.getBlockState(mutable); (state.isAir() || state.is(BlockTags.LEAVES)) && mutable.getY() > level.getMinBuildHeight(); state = level.getBlockState(mutable)) {
			mutable.below();
		}
		return mutable;
	}

	//TODO: MutableBlockPos?
	public static BlockPos randomOffset(RandomSource rand, BlockPos origin, int offsetX, int offsetY, int offsetZ) {
		return origin.offset(
			rand.nextInt(offsetX) - rand.nextInt(offsetX),
			rand.nextInt(offsetY) - rand.nextInt(offsetY),
			rand.nextInt(offsetZ) - rand.nextInt(offsetZ));
	}
}
