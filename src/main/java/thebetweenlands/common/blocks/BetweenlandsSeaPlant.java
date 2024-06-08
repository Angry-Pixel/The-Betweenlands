package thebetweenlands.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import thebetweenlands.common.blocks.blockstates.BetweenlandsStateProperties;
import thebetweenlands.common.registries.FluidRegistry;

public class BetweenlandsSeaPlant extends BetweenlandsBlock implements SimpleWaterloggedBlock {

	// Block properties
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty TOP = BetweenlandsStateProperties.TOP;
	
	public BetweenlandsSeaPlant(Properties p_48756_) {
		super(p_48756_);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED,Boolean.valueOf(false)).setValue(TOP, Boolean.valueOf(true)));
	}

	// Get a tick to ocure if block dosent have another of the same below
	public BlockState updateShape(BlockState p_153302_, Direction p_153303_, BlockState p_153304_, LevelAccessor p_153305_, BlockPos p_153306_, BlockPos p_153307_) {
	
		// State updates
		p_153305_.scheduleTick(p_153306_, this, 0);
		if (p_153302_.getValue(WATERLOGGED)) {
			p_153305_.scheduleTick(p_153306_, FluidRegistry.SWAMP_WATER_STILL.get(), FluidRegistry.SWAMP_WATER_STILL.get().getTickDelay(p_153305_));
		}
		
		// if top block set tag to true
		boolean topBlock = true;
		if (p_153305_.getBlockState(p_153306_.above()).is(this)) {
			topBlock = false;
		}
		p_153305_.setBlock(p_153306_, p_153302_.setValue(TOP, topBlock), UPDATE_ALL);
	
		return p_153302_;
	}

	// Alow placment in swamp water and 
	public BlockState getStateForPlacement(BlockPlaceContext p_153824_) {
		Level level = p_153824_.getLevel();
		BlockPos blockpos = p_153824_.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);
		
		BlockState outstate = this.defaultBlockState();
		
		if (!level.getFluidState(blockpos).isEmpty()) {
			outstate = outstate.setValue(WATERLOGGED, true);
		}
		
		return outstate;
	}
	
	// Only allow placment only on ground
	@Override
	public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
	
		// if block below is not of the same block name
		if (!p_52784_.getBlockState(p_52785_.below()).is(this))
		{
			return p_52784_.getBlockState(p_52785_.below()).isFaceSturdy(p_52784_, p_52785_.below(), Direction.UP);
		}
	
		return true;
	}

	// On tick update, check for block connected to solid block
	@Override
	public void tick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, Random p_60465_) {
	
		// do can suvive check
		if (!this.canSurvive(p_60462_, p_60463_, p_60464_))
		{
			p_60463_.destroyBlock(p_60464_, false);
		}
	}
	
	@Override
	public FluidState getFluidState(BlockState p_153311_) {
		if (p_153311_.getValue(WATERLOGGED)) {
			return FluidRegistry.SWAMP_WATER_STILL.get().getSource(false);
		}
		return Fluids.EMPTY.defaultFluidState();
	}
	
	// Add propertys to block
	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153309_) {
		p_153309_.add(WATERLOGGED);
		p_153309_.add(TOP);
	}
}
