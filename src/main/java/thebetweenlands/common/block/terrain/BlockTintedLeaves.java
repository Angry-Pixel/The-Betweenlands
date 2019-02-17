package thebetweenlands.common.block.terrain;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.BiomeColors;
import thebetweenlands.common.block.ITintedBlock;

public class BlockTintedLeaves extends BlockLeavesBetweenlands implements ITintedBlock {
	@Override
	public int getColorMultiplier(IBlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColors.getFoliageColor(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
	}
}
