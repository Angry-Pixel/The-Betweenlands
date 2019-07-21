package thebetweenlands.common.block.plant;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;

public class BlockSludgeDungeonPlant extends BlockPlant {
	@Override
	public boolean isFarmable(World world, BlockPos pos, IBlockState state) {
		IBlockState soil = world.getBlockState(pos.down());
		if(soil.getBlock() instanceof BlockGenericDugSoil) {
			return soil.getValue(BlockGenericDugSoil.FOGGED);
		}
		return false;
	}
}
