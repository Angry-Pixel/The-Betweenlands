package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class BladderwortFlowerBlock extends PlantBlock {
	public BladderwortFlowerBlock(Properties properties) {
		super(PlantBlock.GRASS_SHAPE, false, properties);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return state.is(BlockRegistry.BLADDERWORT_STALK);
	}
}
