package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

// All vars and functions are public and set for override use
public class BetweenlandsHangingBlock extends BetweenlandsBlock {

	public final int growthChance;

	// Block properties
	public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

	public BetweenlandsHangingBlock(Properties p_49795_, int growthChance) {
		super(p_49795_);
		this.registerDefaultState(this.stateDefinition.any().setValue(BOTTOM, Boolean.valueOf(true)));
		this.growthChance = growthChance;
	}

	// Get a tick to ocure if block dosent have another of the same below
	public BlockState updateShape(BlockState p_153302_, Direction p_153303_, BlockState p_153304_, LevelAccessor p_153305_, BlockPos p_153306_, BlockPos p_153307_) {

		p_153305_.scheduleTick(p_153306_, this, 0);

		// if bottom block set tag to true
		boolean bottomBlock = true;
		if (p_153305_.getBlockState(p_153306_.below()).is(this)) {
			bottomBlock = false;
		}
		p_153305_.setBlock(p_153306_, p_153302_.setValue(BOTTOM, bottomBlock), UPDATE_ALL);

		return p_153302_;
	}

	// Only allow placment only on cealings
	@Override
	public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {

		// if block above is not of the same block name
		if (!p_52784_.getBlockState(p_52785_.above()).is(this)) {
			return p_52784_.getBlockState(p_52785_.above()).isFaceSturdy(p_52784_, p_52785_.above(), Direction.DOWN);
		}

		return true;
	}

	// On tick update, check for block connected to solid block
	@Override
	public void tick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, RandomSource p_60465_) {

		// do can suvive check
		if (!this.canSurvive(p_60462_, p_60463_, p_60464_)) {
			p_60463_.destroyBlock(p_60464_, false);
		}
	}

	@Override
	public void randomTick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, RandomSource p_60465_) {

		// Do grow check
		if (canGrow(p_60464_, p_60463_) && p_60465_.nextInt(this.growthChance) <= 0) {
			doGrow(p_60464_, p_60463_);
		}
	}

	// Grow block
	public void doGrow(BlockPos blockpos, LevelAccessor level) {
		level.setBlock(blockpos.below(), level.getBlockState(blockpos).getBlock().defaultBlockState(), UPDATE_ALL);
	}

	// Growing conditions
	public boolean canGrow(BlockPos blockpos, LevelAccessor level) {

		if (level.getBlockState(blockpos).getValue(BOTTOM)) {
			return level.getBlockState(blockpos.below()) == Blocks.AIR.defaultBlockState();
		}

		return false;
	}

	// Add propertys to block
	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153309_) {
		p_153309_.add(BOTTOM);
	}
}
