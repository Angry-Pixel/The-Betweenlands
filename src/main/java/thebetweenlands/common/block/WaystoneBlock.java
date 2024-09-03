package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.common.block.entity.SmokingRackBlockEntity;
import thebetweenlands.common.block.entity.WaystoneBlockEntity;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

import java.util.List;
import java.util.Locale;

public class WaystoneBlock extends BaseEntityBlock implements SwampWaterLoggable {

	public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

	public WaystoneBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(PART, Part.BOTTOM).setValue(ACTIVE, false).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(currentPos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}
		return !this.isValidWaystone(level, currentPos, state) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		return blockpos.getY() < level.getMaxBuildHeight() - 2 && level.getBlockState(blockpos.above()).canBeReplaced(context) && level.getBlockState(blockpos.above(2)).canBeReplaced(context)
			? super.getStateForPlacement(context).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()))
			: null;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (state.getValue(PART) == Part.MIDDLE && level.getBlockState(pos.below()).is(this)) {
			this.useWithoutItem(level.getBlockState(pos.below()), level, pos.below(), player, hitResult);
		} else if (state.getValue(PART) == Part.TOP && level.getBlockState(pos.below(2)).is(this)) {
			this.useWithoutItem(level.getBlockState(pos.below()), level, pos.below(2), player, hitResult);
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof WaystoneBlockEntity waystone) waystone.setRotation(level.getRandom().nextFloat() * 360.0F);
		level.setBlockAndUpdate(pos.above(), DoublePlantBlock.copyWaterloggedFrom(level, pos, state).setValue(PART, Part.MIDDLE));
		level.setBlockAndUpdate(pos.above(2), DoublePlantBlock.copyWaterloggedFrom(level, pos, state).setValue(PART, Part.TOP));
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getValue(PART) == Part.BOTTOM) return super.canSurvive(state, level, pos);
		return this.isValidWaystone(level, pos, state);
	}

	public boolean isValidWaystone(LevelReader level, BlockPos pos, BlockState state) {
		switch (state.getValue(PART)) {
			case TOP: {
				BlockState down1 = level.getBlockState(pos.below());
				BlockState down2 = level.getBlockState(pos.below(2));
				return down1.getBlock() == this && down1.getValue(PART) == Part.MIDDLE && down2.getBlock() == this && down2.getValue(PART) == Part.BOTTOM;
			}
			case MIDDLE: {
				BlockState down1 = level.getBlockState(pos.below());
				BlockState up1 = level.getBlockState(pos.above());
				return down1.getBlock() == this && down1.getValue(PART) == Part.BOTTOM && up1.getBlock() == this && up1.getValue(PART) == Part.TOP;
			}
			default:
			case BOTTOM: {
				BlockState up1 = level.getBlockState(pos.above());
				BlockState up2 = level.getBlockState(pos.above(2));
				return up1.getBlock() == this && up1.getValue(PART) == Part.MIDDLE && up2.getBlock() == this && up2.getValue(PART) == Part.TOP;
			}
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		super.onRemove(state, level, pos, newState, movedByPiston);
		var worldStorage = BetweenlandsWorldStorage.get(level);
		if (worldStorage != null) {
//			ILocalStorageHandler localStorageHandler = worldStorage.getLocalStorageHandler();
//			List<LocationStorage> waystoneLocations = localStorageHandler.getLocalStorages(LocationStorage.class, new AABB(pos), storage -> storage.getType() == EnumLocationType.WAYSTONE);
//			for (LocationStorage waystoneLocation : waystoneLocations) {
//				localStorageHandler.removeLocalStorage(waystoneLocation);
//			}
		}
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide()) {
			if (player.isCreative()) {
				this.onlyDropBottomPart(level, pos, state, player);
			} else {
				dropResources(state, level, pos, null, player, player.getMainHandItem());
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
		super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
	}

	protected void onlyDropBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
		Part part = state.getValue(PART);
		if (part == Part.BOTTOM) {
			BlockPos blockpos = pos.above();
			BlockState blockstate = level.getBlockState(blockpos);
			if (blockstate.is(state.getBlock()) && blockstate.getValue(PART) == Part.MIDDLE) {
				level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
				level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
			}

			blockpos = pos.above(2);
			blockstate = level.getBlockState(blockpos);
			if (blockstate.is(state.getBlock()) && blockstate.getValue(PART) == Part.TOP) {
				level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
				level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
			}
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		for (int i = 0; i < 16; i++) {
			level.addParticle(ParticleTypes.ASH, pos.getX() + 0.5D + (random.nextBoolean() ? -1 : 1) * Math.pow(random.nextFloat(), 2) * 16, pos.getY() + 0.5D + random.nextFloat() * 6 - 3, pos.getZ() + 0.5D + (random.nextBoolean() ? -1 : 1) * Math.pow(random.nextFloat(), 2) * 16, 0, 0.2D, 0);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(PART) == Part.BOTTOM ? new WaystoneBlockEntity(pos, state) : null;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PART, ACTIVE, WATER_TYPE);
	}

	public enum Part implements StringRepresentable {
		TOP,
		MIDDLE,
		BOTTOM;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
