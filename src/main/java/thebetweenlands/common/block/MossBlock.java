package thebetweenlands.common.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.util.OpenSimplexNoise;

import javax.annotation.Nullable;
import java.util.Map;

public class MossBlock extends DirectionalBlock {

	private static final VoxelShape DOWN_AABB = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape UP_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0);
	private static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	private static final VoxelShape WEST_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Util.make(Maps.newEnumMap(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH_AABB);
		map.put(Direction.EAST, EAST_AABB);
		map.put(Direction.SOUTH, SOUTH_AABB);
		map.put(Direction.WEST, WEST_AABB);
		map.put(Direction.UP, UP_AABB);
		map.put(Direction.DOWN, DOWN_AABB);
	});

	private final OpenSimplexNoise spreadingClusterNoise1 = new OpenSimplexNoise(1337);
	private final OpenSimplexNoise spreadingClusterNoise2 = new OpenSimplexNoise(42);

	public MossBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return null;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing = context.getNearestLookingDirection().getOpposite();
		BlockState facingState = context.getLevel().getBlockState(context.getClickedPos().relative(facing));
		if (facingState.isFaceSturdy(context.getLevel(), context.getClickedPos().relative(facing), facing.getOpposite())) {
			return this.defaultBlockState().setValue(FACING, facing);
		} else {
			for (Direction direction : Direction.values()) {
				if (context.getLevel().getBlockState(context.getClickedPos().relative(direction.getOpposite())).isFaceSturdy(context.getLevel(), context.getClickedPos().relative(direction.getOpposite()), direction)) {
					return this.defaultBlockState().setValue(FACING, direction);
				}
			}
			return null;
		}
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.getOrDefault(state.getValue(FACING), Shapes.empty());
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).isFaceSturdy(level, pos.relative(state.getValue(FACING).getOpposite()), state.getValue(FACING));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.canSurvive(level, pos) ? super.updateShape(state, direction, neighborState, level, pos, neighborPos) : Blocks.AIR.defaultBlockState();
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		double noiseScale = 0.06;
		double ridgedNoise = Math.min(
			Math.abs(this.spreadingClusterNoise1.eval((pos.getX() + 0.5) * noiseScale, (pos.getY() + 0.5) * noiseScale, (pos.getZ() + 0.5) * noiseScale)),
			Math.abs(this.spreadingClusterNoise2.eval((pos.getX() + 0.5) * noiseScale, (pos.getY() + 0.5) * noiseScale, (pos.getZ() + 0.5) * noiseScale)));

		boolean spread = ridgedNoise >= 0.065;

		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
		byte radius = 2;
		int attempt;
		for (int xx = pos.getX() - radius; xx <= pos.getX() + radius; ++xx) {
			for (int zz = pos.getZ() - radius; zz <= pos.getZ() + radius; ++zz) {
				for (int yy = pos.getY() - radius; yy <= pos.getY() + radius; ++yy) {
					if (!level.isLoaded(checkPos.set(xx, yy, zz))) {
						return;
					}
				}
			}
		}
		if (random.nextInt(3) == 0) {
			if (spread) {
				int maxNearbyMossBlocks = 6;
				for (int xx = pos.getX() - radius; xx <= pos.getX() + radius; ++xx) {
					for (int zz = pos.getZ() - radius; zz <= pos.getZ() + radius; ++zz) {
						for (int yy = pos.getY() - radius; yy <= pos.getY() + radius; ++yy) {
							if (level.getBlockState(checkPos.set(xx, yy, zz)).getBlock() == this) {
								--maxNearbyMossBlocks;
								if (maxNearbyMossBlocks <= 0) {
									return;
								}
							}
						}
					}
				}
				for (attempt = 0; attempt < 30; attempt++) {
					int xx = pos.getX() + random.nextInt(3) - 1;
					int yy = pos.getY() + random.nextInt(3) - 1;
					int zz = pos.getZ() + random.nextInt(3) - 1;
					int offsetDir = 0;
					if (xx != pos.getX()) offsetDir++;
					if (yy != pos.getY()) offsetDir++;
					if (zz != pos.getZ()) offsetDir++;
					if (offsetDir > 1)
						continue;
					BlockPos offsetPos = new BlockPos(xx, yy, zz);
					if (level.isEmptyBlock(offsetPos)) {
						Direction facing = Direction.getRandom(random);
						Direction.Axis axis = facing.getAxis();
						Direction oppositeFacing = facing.getOpposite();
						boolean isInvalid = false;
						if (axis.isHorizontal() && !level.getBlockState(offsetPos.relative(oppositeFacing)).isFaceSturdy(level, offsetPos.relative(oppositeFacing), facing)) {
							isInvalid = true;
						} else if (axis.isVertical() && !state.canSurvive(level, offsetPos.relative(oppositeFacing))) {
							isInvalid = true;
						}
						if (!isInvalid) {
							level.setBlockAndUpdate(offsetPos, this.defaultBlockState().setValue(FACING, facing));
							break;
						}
					}
				}
			}
		} else if (random.nextInt(27) == 0) {
			level.removeBlock(pos, false);
		}
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
