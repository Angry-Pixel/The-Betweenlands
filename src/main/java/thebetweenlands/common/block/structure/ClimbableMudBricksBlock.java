package thebetweenlands.common.block.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForgeConfig;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;

import javax.annotation.Nullable;
import java.util.Map;

public class ClimbableMudBricksBlock extends HorizontalDirectionalBlock implements SwampWaterLoggable {

	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(0.0D, 0.0D, 2.0D, 16.0D, 16.0D, 16.0D),
		Direction.WEST, Block.box(2.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
		Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 14.0D),
		Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, 14.0D, 16.0D, 16.0D)
	));

	public ClimbableMudBricksBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return null;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	//holy hacks batman!
	//ladder checks don't work on nearly full blocks!
	//neoforge does, however, add a config to change this check. We can't enable it by default so this will do.
	//therefore we'll only perform our logic if we're considered climbable and the config is off.
	//logic here is based off of LivingEntity.handleOnClimbable, with values slightly adjusted to better match actual ladder physics as Entity.move changes the actual movement a bit
	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (state.is(BlockTags.CLIMBABLE) && NeoForgeConfig.SERVER.fullBoundingBoxLadders.isFalse()) {
			entity.resetFallDistance();
			double x = Mth.clamp(entity.getDeltaMovement().x(), -0.15D, 0.15D);
			double z = Mth.clamp(entity.getDeltaMovement().z(), -0.15D, 0.15D);

			double y = Math.max(entity.getDeltaMovement().y(), -0.025D);

			if (y < 0.0D && (entity instanceof LivingEntity living && living.isSuppressingSlidingDownLadder())) {
				y = 0.0D;
			}

			if (entity.horizontalCollision)
				y = 0.175D;

			entity.setDeltaMovement(x, y, z);
		}
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.get(state.getValue(FACING));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATER_TYPE);
	}
}
