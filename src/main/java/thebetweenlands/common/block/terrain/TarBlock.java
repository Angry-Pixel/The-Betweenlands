package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import thebetweenlands.common.datagen.tags.BLDimensionTypeTagProvider;
import thebetweenlands.common.fluid.TarFlowingFluid;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;

public class TarBlock extends LiquidBlock {
	public static final BooleanProperty BOILING = TarFlowingFluid.BOILING;
	
	public TarBlock(FlowingFluid fluid, Properties properties) {
		super(fluid, properties);
		registerDefaultState(defaultBlockState().setValue(BOILING, false));
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity /*!(entity instanceof TarBeast)*/ && !(entity instanceof Player player && player.getAbilities().invulnerable)) {
			if (entity.isEyeInFluidType(FluidTypeRegistry.TAR.get())) {
				entity.hurt(level.damageSources().drown(), 2.0F);
			}
			if(state.getValue(BOILING)) {
				entity.hurt(level.damageSources().inFire(), 1.0F);
			}
		}
	}
	
	@Override
	protected FluidState getFluidState(BlockState state) {
		return super.getFluidState(state).setValue(BOILING, state.getValue(BOILING));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BOILING);
	}


	public static boolean shouldFloorCauseBoiling(BlockAndTintGetter level, BlockPos pos) {
		return shouldFloorCauseBoiling(level.getBlockState(pos), level, pos);
	}
	
	/**
	 * Checks if a specific {@code state} in a specific {@code level} at
	 * a specific {@code pos}ition should cause tar above it to boil
	 * @param state the state that might cause boiling
	 * @param level the level the state is in
	 * @param pos the position the state is in
	 * @return
	 */
	public static boolean shouldFloorCauseBoiling(BlockState state, BlockAndTintGetter level, BlockPos pos) {
		if(state.is(BlockRegistry.OCTINE_BLOCK)) {
			return true;
		} else {
			FluidState fluidState = state.getFluidState();
			return fluidState != null && fluidState.getOptionalValue(TarFlowingFluid.BOILING).orElse(false);
		}
	}

	/**
	 * Checks if tar should boil in a specific {@code level} at a specific {@code pos}
	 * @param level the level the state is in
	 * @param pos the position the state is in
	 * @return
	 */
	public static boolean shouldLocationCauseBoiling(LevelReader level, BlockPos pos) {
		// if boiling doesn't get removed, add a tag to disable ultraWarm boiling
		if(level.dimensionType().ultraWarm()) {
			return true;
		}
		if(level instanceof Level) {
			return ((Level)level).dimensionTypeRegistration().is(BLDimensionTypeTagProvider.TAR_BOILS);
		}
		return false;
	}
	
	public static boolean shouldBeBoiling(LevelReader level, BlockPos pos) {
		return shouldBeBoiling(level, pos, level.getBlockState(pos), level.getBlockState(pos.below()));
	}
	
	public static boolean shouldBeBoiling(LevelReader level, BlockPos pos, BlockState state, BlockState belowState) {
		return shouldLocationCauseBoiling(level, pos) || shouldFloorCauseBoiling(belowState, level, pos.below());
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		BlockPos facingPos = context.getClickedPos().relative(context.getClickedFace()).below();
//		TheBetweenlands.LOGGER.info("Placing tar: Clicked {} on face {}, checking for boiling status at {}", context.getClickedPos(), context.getClickedFace(), facingPos);
		state = state.setValue(BOILING, shouldBeBoiling(context.getLevel(), facingPos));
		return state;
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		BlockState newState = super.updateShape(state, facing, facingState, level, currentPos, facingPos);
		if(facing == Direction.DOWN) {
			newState = newState.setValue(BOILING, shouldBeBoiling(level, facingPos, newState, facingState));
		} else {
			newState = newState.setValue(BOILING, state.getValue(BOILING));
		}
		return newState;
	}
	
//	// Fix: always defaults to default state when placed via bucket
//	@Override
//	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
//		boolean shouldBoil = shouldCauseBoiling(level.getBlockState(pos.below()), level, pos.below());
//		if(state.getValue(BOILING) != shouldBoil) {
//			level.setBlock(pos, state.setValue(BOILING, shouldBoil), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
//		}
//		
//		super.onPlace(state, level, pos, oldState, isMoving);
//	}
}
