package thebetweenlands.common.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.block.IDungeonFogBlock;

public class BlockSludgeDungeonHangingPlant extends BlockHangingPlant {
	@Override
	protected boolean canGrowAt(World world, BlockPos pos, IBlockState state) {
		if(super.canGrowAt(world, pos, state)) {
			for(BlockPos.MutableBlockPos checkPos : BlockPos.getAllInBoxMutable(pos.add(-6, -4, -6), pos.add(6, 0, 6))) {
				if(world.isBlockLoaded(checkPos)) {
					IBlockState offsetState = world.getBlockState(checkPos);
					Block offsetBlock = offsetState.getBlock();
					if(offsetBlock instanceof IDungeonFogBlock && ((IDungeonFogBlock) offsetBlock).isCreatingDungeonFog(world, checkPos, offsetState)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
