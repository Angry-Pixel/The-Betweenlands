package thebetweenlands.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class DungeonWallCandleBlock extends HorizontalDirectionalBlock {

	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(4.0D, 0.0D, 4.0D, 12.0D, 14.0D, 16.0D),
		Direction.WEST, Block.box(4.0D, 0.0D, 4.0D, 16.0D, 14.0D, 12.0D),
		Direction.SOUTH, Block.box(4.0D, 0.0D, 0.0D, 12.0D, 14.0D, 12.0D),
		Direction.EAST, Block.box(0.0D, 0.0D, 4.0D, 12.0D, 14.0D, 12.0D)
	));

	public DungeonWallCandleBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return null;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = this.defaultBlockState();
		LevelReader levelreader = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Direction[] adirection = context.getNearestLookingDirections();

		for (Direction direction : adirection) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction1 = direction.getOpposite();
				blockstate = blockstate.setValue(FACING, direction1);
				if (blockstate.canSurvive(levelreader, blockpos)) {
					return blockstate;
				}
			}
		}

		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.get(state.getValue(FACING));
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		BlockState blockstate = level.getBlockState(blockpos);
		return direction.getAxis().isHorizontal() && blockstate.isFaceSturdy(level, blockpos, direction);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		state = state.cycle(LIT);
		level.setBlockAndUpdate(pos, state);
		if (state.getValue(LIT)) {
			level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.05F, 1F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
		} else {
			level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 1F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(LIT)) {
			Direction facing = state.getValue(FACING);
			double offSetX = facing.getStepX() * 0.09375;
			double offSetZ = facing.getStepZ() * 0.09375;
			double offSetWaxX = 0D + random.nextDouble() * 0.125D - random.nextDouble() * 0.125D;
			double offSetWaxZ = 0D + random.nextDouble() * 0.125D - random.nextDouble() * 0.125D;

			double x = (double) pos.getX() + 0.5D;
			double y = (double) pos.getY() + 0.9375D;
			double z = (double) pos.getZ() + 0.5D;

			level.addParticle(ParticleTypes.SMOKE, x + offSetX, y, z + offSetZ, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.FLAME, x + offSetX, y, z + offSetZ, 0.0D, 0.0D, 0.0D);
			if (random.nextInt(10) == 0) {
				//BLParticles.TAR_BEAST_DRIP.spawn(level , x + offSetX + offSetWaxX, y - 0.938D, z + offSetZ +offSetWaxZ).setRBGColorF(1F, 1F, 1F);
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT);
	}
}
