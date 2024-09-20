package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.block.FarmablePlant;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

import javax.annotation.Nullable;

//TODO consider allowing underwater plants to exist outside water now that waterlogging isnt forced
//to do this change LiquidBlockContainer to SimpleWaterloggedBlock, add the property, and check for the property before scheduling fluid ticks
public class UnderwaterPlantBlock extends Block implements LiquidBlockContainer, FarmablePlant {

	protected static final VoxelShape PLANT_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 13.0D, 15.0D);

	public UnderwaterPlantBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return fluidstate.is(FluidRegistry.SWAMP_WATER_STILL.get()) && fluidstate.getAmount() == 8 ? super.getStateForPlacement(context) : null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return PLANT_AABB;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		level.scheduleTick(pos, FluidRegistry.SWAMP_WATER_STILL.get(), FluidRegistry.SWAMP_WATER_STILL.get().getTickDelay(level));
		return state.canSurvive(level, pos) ? state : BlockRegistry.SWAMP_WATER.get().defaultBlockState();
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return FluidRegistry.SWAMP_WATER_STILL.get().getSource(false);
	}

	@Override
	public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}

	@Override
	public float getSpreadChance(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return 0.25F;
	}

	@Override
	public boolean isFarmable(Level level, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean canSpreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		BlockState block = level.getBlockState(targetPos);
		if (state.is(BlockRegistry.SWAMP_WATER) && ((LiquidBlock) block.getBlock()).fluid.isSource(level.getFluidState(targetPos))) {
			return state.canSurvive(level, targetPos);
		}
		return false;
	}

	@Override
	public int getCompostCost(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return 4;
	}

	@Override
	public void decayPlant(Level level, BlockPos pos, BlockState state, RandomSource random) {
		level.setBlockAndUpdate(pos, BlockRegistry.SWAMP_WATER.get().defaultBlockState());
	}

	@Override
	public void spreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		level.setBlockAndUpdate(targetPos, this.defaultBlockState());
	}
}
