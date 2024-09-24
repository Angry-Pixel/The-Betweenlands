package thebetweenlands.common.block.terrain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.api.block.FarmablePlant;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

import javax.annotation.Nullable;
import java.util.Map;

public class PuddleBlock extends Block {

	private static final VoxelShape AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
	public final static IntegerProperty AMOUNT = IntegerProperty.create("amount", 0, 15);

	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.SOUTH, SOUTH, Direction.EAST, EAST, Direction.WEST, WEST));

	public PuddleBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(AMOUNT, 0));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = this.defaultBlockState();
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState neighborState = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
			BlockState belowNeighborState = context.getLevel().getBlockState(context.getClickedPos().relative(direction).below());
			state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), !neighborState.is(this) && belowNeighborState.isFaceSturdy(context.getLevel(), context.getClickedPos().relative(direction).below(), Direction.UP));
		}
		return state;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int amount = state.getValue(AMOUNT);
		if (!BetweenlandsWorldStorage.isEventActive(level, EnvironmentEventRegistry.HEAVY_RAIN)) {
			level.removeBlock(pos, false);
			amount = 0;
		} else if (level.canSeeSky(pos)) {
			amount = Math.min(amount + random.nextInt(6), 15);
			level.setBlock(pos, state.setValue(AMOUNT, amount), 2);
		}
		if (amount > 2) {
			amount = Math.max(0, amount - 3);
			level.setBlock(pos, state.setValue(AMOUNT, amount), 2);
			for (int xo = -1; xo <= 1; xo++) {
				for (int zo = -1; zo <= 1; zo++) {
					BlockPos newPos = pos.offset(xo, 0, zo);
					if ((xo == 0 && zo == 0) || xo * xo == zo * zo) continue;
					BlockState offsetState = level.getBlockState(newPos);
					if ((level.isEmptyBlock(newPos) || (offsetState.getBlock() instanceof FarmablePlant plant && plant.canBeDestroyedByPuddles(level, newPos, offsetState))) && this.defaultBlockState().canSurvive(level, newPos)) {
						level.setBlockAndUpdate(newPos, this.defaultBlockState());
					} else if (level.getBlockState(newPos).is(this)) {
						level.setBlock(newPos, state.setValue(AMOUNT, Math.min(amount + random.nextInt(6), 15)), 2);
					}
				}
			}
		}
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return (state.canBeReplaced() || (state.getBlock() instanceof FarmablePlant plant && plant.canBeDestroyedByPuddles(level, pos, state))) && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.canSurvive(level, pos) ? state.setValue(PROPERTY_BY_DIRECTION.get(direction), !neighborState.is(this) && level.getBlockState(neighborPos.below()).isFaceSturdy(level, neighborPos.below(), Direction.UP)) : Blocks.AIR.defaultBlockState();
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.isClientSide() && entity instanceof Player player && player.getY() <= pos.getY() + 0.01f && entity.tickCount % 5 == 0) {
			double strength = Math.sqrt(entity.getDeltaMovement().x() * entity.getDeltaMovement().x() * 0.2D + entity.getDeltaMovement().y() * entity.getDeltaMovement().y() + entity.getDeltaMovement().z() * entity.getDeltaMovement().z() * 0.2D) * 0.2f;

			if (strength > 0.01D) {
				entity.playSound(SoundEvents.GENERIC_SWIM, (float) (strength * 8), 1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.4F);

				for (int j = 0; (float) j < 10.0F + entity.getBbWidth() * 20.0F; ++j) {
					float rx = (level.getRandom().nextFloat() * 2.0F - 1.0F) * entity.getBbWidth();
					float rz = (level.getRandom().nextFloat() * 2.0F - 1.0F) * entity.getBbWidth();
					level.addParticle(ParticleTypes.SPLASH, entity.getX() + rx, pos.getY() + 0.1f, entity.getZ() + rz, entity.getDeltaMovement().y() + (level.getRandom().nextFloat() - 0.5f) * strength * 20, entity.getDeltaMovement().z(), entity.getDeltaMovement().z() + (level.getRandom().nextFloat() - 0.5f) * strength * 20);
				}
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, WEST, SOUTH, EAST, AMOUNT);
	}
}
