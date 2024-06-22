package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Random;

public class OctineOreBlock extends Block {

	public static final BooleanProperty SETUP = BooleanProperty.create("setup");

	public OctineOreBlock(Properties p_49795_) {
		super(p_49795_);
		this.registerDefaultState(this.stateDefinition.any().setValue(SETUP, Boolean.valueOf(true)));
	}

	// tags the block on next tick to self update ligting
	// alows me to avoid using copyrighted code
	public BlockState FeaturePlacementBlockState() {
		return defaultBlockState().setValue(SETUP, Boolean.valueOf(false));
	}

	@Override
	public void randomTick(BlockState p_60551_, ServerLevel p_60552_, BlockPos p_60553_, Random p_60554_) {
		p_60552_.markAndNotifyBlock(p_60553_, p_60552_.getChunkAt(p_60553_), p_60551_, p_60551_, UPDATE_ALL_IMMEDIATE, UPDATE_ALL);
		p_60552_.setBlockAndUpdate(p_60553_, p_60551_.setValue(SETUP, true));
	}

	@Override
	public boolean isRandomlyTicking(BlockState p_49921_) {
		return !p_49921_.getValue(SETUP);
	}

	// Add propertys to block
	@Override
	public void createBlockStateDefinition(Builder<Block, BlockState> p_153309_) {
		p_153309_.add(SETUP);
	}

}
