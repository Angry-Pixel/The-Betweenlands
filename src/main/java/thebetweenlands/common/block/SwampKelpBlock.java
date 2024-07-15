package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.block.FarmablePlant;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

import javax.annotation.Nullable;

public class SwampKelpBlock extends GrowingPlantHeadBlock implements LiquidBlockContainer, FarmablePlant {

	public static final MapCodec<SwampKelpBlock> CODEC = simpleCodec(SwampKelpBlock::new);
	protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

	public SwampKelpBlock(Properties properties) {
		super(properties, Direction.UP, SHAPE, true, 0.14);
	}

	@Override
	protected MapCodec<? extends GrowingPlantHeadBlock> codec() {
		return CODEC;
	}

	@Override
	protected Block getBodyBlock() {
		return BlockRegistry.SWAMP_KELP_PLANT.get();
	}

	@Override
	protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
		return 1;
	}

	@Override
	protected boolean canGrowInto(BlockState state) {
		return state.is(BlockRegistry.SWAMP_WATER);
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
	protected FluidState getFluidState(BlockState state) {
		return FluidRegistry.SWAMP_WATER_STILL.get().getSource(false);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return fluidstate.is(FluidRegistry.SWAMP_WATER_STILL.get()) && fluidstate.getAmount() == 8 ? super.getStateForPlacement(context) : null;
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
