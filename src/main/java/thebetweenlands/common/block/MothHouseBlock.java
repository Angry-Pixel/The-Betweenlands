package thebetweenlands.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.MothHouseBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Map;

public class MothHouseBlock extends HorizontalBaseEntityBlock {

	public static final BooleanProperty ON_WALL = BooleanProperty.create("on_wall");
	public static final VoxelShape Z_SHAPE = Block.box(2.0D, 0.0D, 4.0D, 14.0D, 16.0D, 12.0D);
	public static final VoxelShape X_SHAPE = Block.box(4.0D, 0.0D, 2.0D, 12.0D, 16.0D, 14.0D);
	private static final Map<Direction, VoxelShape> WALL_SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(2.0D, 0.0D, 0.0D, 14.0D, 16.0D, 5.0D),
		Direction.WEST, Block.box(0.0D, 0.0D, 2.0D, 5.0D, 16.0D, 14.0D),
		Direction.SOUTH, Block.box(2.0D, 0.0D, 11.0D, 14.0D, 16.0D, 16.0D),
		Direction.EAST, Block.box(11.0D, 0.0D, 2.0D, 16.0D, 16.0D, 14.0D)
	));

	public MothHouseBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(ON_WALL, false));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MothHouseBlockEntity house) {
			if (placer instanceof Player player)
				house.setPlacer(player);
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(ON_WALL, context.getClickedFace().getAxis() != Direction.Axis.Y);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (state.getValue(ON_WALL)) {
			return WALL_SHAPE_BY_DIRECTION.getOrDefault(state.getValue(FACING), Shapes.empty());
		} else {
			return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
		}
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (state.getValue(ON_WALL)) {
			return Block.canSupportCenter(level, pos, state.getValue(FACING));
		} else {
			return Block.canSupportCenter(level, pos, Direction.UP);
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.canSurvive(level, pos) ? super.updateShape(state, direction, neighborState, level, pos, neighborPos) : Blocks.AIR.defaultBlockState();
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MothHouseBlockEntity house) {
			if (stack.is(ItemRegistry.SILK_GRUB)) {
				int grubCount = house.addGrubs(stack);
				stack.consume(grubCount, player);
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MothHouseBlockEntity house) {
			this.extractItems(player, house, false);

			if (player.isCrouching()) {
				this.extractItems(player, house, true);
			}
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	protected void extractItems(Player player, MothHouseBlockEntity house, boolean grubs) {
		ItemStack itemStack = house.getItem(grubs ? MothHouseBlockEntity.SLOT_GRUBS : MothHouseBlockEntity.SLOT_SILK);

		if (!itemStack.isEmpty()) {
			player.getInventory().add(itemStack.copy());
			house.setItem(grubs ? MothHouseBlockEntity.SLOT_GRUBS : MothHouseBlockEntity.SLOT_SILK, ItemStack.EMPTY);
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MothHouseBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.MOTH_HOUSE.get(), MothHouseBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(ON_WALL));
	}
}
