package thebetweenlands.common.block.misc;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;

import java.util.List;
import java.util.Map;

public class GlowingGoopBlock extends DirectionalBlock implements SwampWaterLoggable {

	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(2.0D, 2.0D, 12.0D, 14.0D, 14.0D, 16.0D),
		Direction.WEST, Block.box(12.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D),
		Direction.SOUTH, Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 4.0D),
		Direction.EAST, Block.box(0.0D, 2.0D, 2.0D, 4.0D, 14.0D, 14.0D),
		Direction.UP, Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D),
		Direction.DOWN, Block.box(2.0D, 12.0D, 2.0D, 14.0D, 16.0D, 14.0D)
	));

	public GlowingGoopBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return null;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.getOrDefault(state.getValue(FACING), Shapes.empty());
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return Block.canSupportCenter(level, pos.relative(state.getValue(FACING).getOpposite()), state.getValue(FACING));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}
		return state.canSurvive(level, pos) ? super.updateShape(state, direction, neighborState, level, pos, neighborPos) : Blocks.AIR.defaultBlockState();
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATER_TYPE);
	}
}
