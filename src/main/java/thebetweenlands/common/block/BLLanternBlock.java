package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.registries.ParticleRegistry;

import java.util.Optional;

public class BLLanternBlock extends LanternBlock implements SwampWaterLoggable {

	public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
	protected static final VoxelShape AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 7.0D, 12.0D);
	protected static final VoxelShape HANGING_AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

	public BLLanternBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(ROTATION, 0).setValue(HANGING, false).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(HANGING) ? HANGING_AABB : AABB;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = null;
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

		for (Direction direction : context.getNearestLookingDirections()) {
			if (direction.getAxis() == Direction.Axis.Y) {
				BlockState blockstate = this.defaultBlockState().setValue(HANGING, Boolean.valueOf(direction == Direction.UP));
				if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
					state = blockstate.setValue(WATER_TYPE, WaterType.getFromFluid(fluidstate.getType()));
				}
			}
		}

		if (state != null) {
			int rotation = Mth.floor(((context.getPlayer().getYRot() + 180.0F) * 8.0F / 360.0F) + 0.5D) & 7;
			if (context.getLevel().getBlockState(context.getClickedPos().above()).getBlock() instanceof RopeBlock) {
				rotation -= rotation % 2;
			}
			state.setValue(ROTATION, rotation);
		}
		return state;
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return getConnectedDirection(state).getOpposite() == direction && !state.canSurvive(level, pos)
			? Blocks.AIR.defaultBlockState()
			: state;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		double px = (double) pos.getX() + 0.5D;
		double py = (double) pos.getY() + 0.3D;
		double pz = (double) pos.getZ() + 0.5D;

		if (random.nextInt(20) == 0 && level.canSeeSky(pos)) {
			if (random.nextBoolean()) {
				TheBetweenlands.createParticle(ParticleRegistry.MOTH.get(), level, px, py, pz);
			} else {
				TheBetweenlands.createParticle(ParticleRegistry.FLY.get(), level, px, py, pz);
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HANGING, ROTATION, WATER_TYPE);
	}

	@Override
	public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return SwampWaterLoggable.super.canPlaceLiquid(player, level, pos, state, fluid);
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		return SwampWaterLoggable.super.placeLiquid(level, pos, state, fluidState);
	}

	@Override
	public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
		return SwampWaterLoggable.super.pickupBlock(player, level, pos, state);
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return SwampWaterLoggable.super.getPickupSound();
	}
}
