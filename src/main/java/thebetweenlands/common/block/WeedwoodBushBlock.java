package thebetweenlands.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.block.FarmablePlant;
import thebetweenlands.common.datagen.BetweenlandsEntityTagProvider;

import java.util.Map;

public class WeedwoodBushBlock extends Block implements FarmablePlant {

	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH);
		map.put(Direction.EAST, EAST);
		map.put(Direction.SOUTH, SOUTH);
		map.put(Direction.WEST, WEST);
		map.put(Direction.UP, UP);
		map.put(Direction.DOWN, DOWN);
	}));

	public WeedwoodBushBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.getStateWithConnections(context.getLevel(), context.getClickedPos(), this.defaultBlockState());
	}

	public BlockState getStateWithConnections(BlockGetter level, BlockPos pos, BlockState state) {
		return state.trySetValue(DOWN, this.canConnectTo(level, pos, Direction.DOWN))
			.trySetValue(UP, this.canConnectTo(level, pos, Direction.UP))
			.trySetValue(NORTH, this.canConnectTo(level, pos, Direction.NORTH))
			.trySetValue(EAST, this.canConnectTo(level, pos, Direction.EAST))
			.trySetValue(SOUTH, this.canConnectTo(level, pos, Direction.SOUTH))
			.trySetValue(WEST, this.canConnectTo(level, pos, Direction.WEST));
	}

	public boolean canConnectTo(BlockGetter level, BlockPos pos, Direction direction) {
		BlockState state = level.getBlockState(pos.relative(direction));
		return state.is(this) && !(state.getBlock() instanceof NestBlock);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext ctx && ctx.getEntity() != null && ctx.getEntity().getType().is(BetweenlandsEntityTagProvider.IGNORES_WEEDWOOD_BUSHES)) {
			return Shapes.empty();
		}
		return super.getCollisionShape(state, level, pos, context);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.canConnectTo(level, pos, direction));
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof Player) {
			entity.makeStuckInBlock(state, new Vec3(0.06D, 1.0D, 0.06D));
		}

		//todo step sound attachment
	}

	protected void spawnLeafParticles(Level level, BlockPos pos, float strength) {
		int leafCount = (int) (60 * strength) + 1;
		float x = pos.getX() + 0.5F;
		float y = pos.getY() + 0.5F;
		float z = pos.getZ() + 0.5F;
		while (leafCount-- > 0) {
			float dx = level.getRandom().nextFloat() * 2 - 1;
			float dy = level.getRandom().nextFloat() * 2 - 0.5F;
			float dz = level.getRandom().nextFloat() * 2 - 1;
			float mag = 0.01F + level.getRandom().nextFloat() * 0.07F;
			//BLParticles.WEEDWOOD_LEAF.spawn(level, x, y, z, ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	@Override
	public boolean isFarmable(Level level, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean canSpreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return level.isEmptyBlock(targetPos) && state.canSurvive(level, targetPos);
	}

	@Override
	public float getSpreadChance(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		return 0.35F;
	}

	@Override
	public int getCompostCost(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return 2;
	}

	@Override
	public void decayPlant(Level level, BlockPos pos, BlockState state, RandomSource random) {
		level.removeBlock(pos, false);
	}

	@Override
	public void spreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		level.setBlockAndUpdate(targetPos, this.defaultBlockState());
	}
}
