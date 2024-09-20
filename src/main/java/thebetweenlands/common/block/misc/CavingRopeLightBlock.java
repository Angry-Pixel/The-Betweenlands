package thebetweenlands.common.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CavingRopeLightBlock extends AirBlock {

	public CavingRopeLightBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
//		List<RopeNode> ropes = level.getCapability(EntityRopeNode.class, new AABB(pos));
//		if(ropes.isEmpty()) {
//			level.removeBlock(pos, false);
//		}
	}
}
