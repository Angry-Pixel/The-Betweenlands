package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class VenusFlyTrapBlock extends PlantBlock {

	public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");

	public VenusFlyTrapBlock(Properties properties) {
		super(PlantBlock.GRASS_SHAPE, false, properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(BLOOMING, false));
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);
		if (random.nextInt(300) == 0) {
			if (!state.getValue(BLOOMING)) {
				if (random.nextInt(3) == 0)
					level.setBlockAndUpdate(pos, state.setValue(BLOOMING, true));
			} else {
				level.setBlockAndUpdate(pos, state.setValue(BLOOMING, false));
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BLOOMING);
	}
}
