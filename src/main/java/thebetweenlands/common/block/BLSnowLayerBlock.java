package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.world.event.WinterEvent;

public class BLSnowLayerBlock extends SnowLayerBlock {
	public BLSnowLayerBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!WinterEvent.isFroooosty(level) || level.getBrightness(LightLayer.BLOCK, pos) > 11) {
			int layers = state.getValue(LAYERS);
			if (layers > 1) {
				level.setBlockAndUpdate(pos, state.setValue(LAYERS, layers - 1));
			} else {
				level.removeBlock(pos, false);
			}
		}
	}
}
