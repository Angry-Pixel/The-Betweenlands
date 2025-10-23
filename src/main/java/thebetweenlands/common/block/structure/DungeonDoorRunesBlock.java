package thebetweenlands.common.block.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.common.block.misc.HorizontalBaseEntityBlock;
import thebetweenlands.common.block.entity.DungeonDoorRunesBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class DungeonDoorRunesBlock extends HorizontalBaseEntityBlock {

	public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
	public final DungeonDoorRunesBlockEntity.DoorType type;

	public DungeonDoorRunesBlock(DungeonDoorRunesBlockEntity.DoorType doorType, Properties properties) {
		super(properties);
		this.type = doorType;
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(INVISIBLE, false));
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		BlockState invisiBlock = this.defaultBlockState().setValue(INVISIBLE, true);

		Direction facing = state.getValue(FACING);
		boolean invis = state.getValue(INVISIBLE);
		for (BlockPos checkPos : BlockPos.betweenClosed(pos.above(invis ? 1 : 0).offset(-facing.getStepZ(), -1, -facing.getStepX()), pos.above(invis ? 1 : 0).offset(facing.getStepZ(), 1, facing.getStepX()))) {
			boolean newInv = !checkPos.equals(pos.above(invis ? 1 : 0));
			level.setBlockAndUpdate(checkPos, this.defaultBlockState().setValue(INVISIBLE, newInv).setValue(FACING, state.getValue(FACING)));
		}
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean invis = false;
		Direction facing = context.getHorizontalDirection().getOpposite();

		if (!context.getLevel().getBlockState(context.getClickedPos().below()).canBeReplaced()) {
			invis = true;
		}

		for (BlockPos checkPos : BlockPos.betweenClosed(context.getClickedPos().above(invis ? 1 : 0).offset(-facing.getStepZ(), -1, -facing.getStepX()), context.getClickedPos().above(invis ? 1 : 0).offset(facing.getStepZ(), 1, facing.getStepX()))) {
			if (!context.getLevel().getBlockState(checkPos).canBeReplaced()) {
				return null;
			}
		}

		return this.defaultBlockState().setValue(FACING, facing).setValue(INVISIBLE, invis);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!state.getValue(INVISIBLE) && !level.isClientSide() && hitResult.getDirection() == state.getValue(FACING)) {
			if (level.getBlockEntity(pos) instanceof DungeonDoorRunesBlockEntity runes && !runes.is_gate_entrance) {
				if (stack.is(ItemRegistry.RUNE_DOOR_KEY)) {
					runes.top_state = runes.top_code;
					runes.mid_state = runes.mid_code;
					runes.bottom_state = runes.bottom_code;
					stack.consume(1, player);
					runes.setChanged();
					return ItemInteractionResult.SUCCESS;
				}
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result) {
		if (!state.getValue(INVISIBLE) && result.getDirection() == state.getValue(FACING)) {
			if (level.getBlockEntity(pos) instanceof DungeonDoorRunesBlockEntity runes && !runes.is_gate_entrance) {
				if (player.isCreative() && player.isShiftKeyDown()) {
					runes.enterLockCode(level, pos);
					player.displayClientMessage(Component.translatable("block.thebetweenlands.dungeon_door_runes.locked"), true);
				} else {
					boolean changed = false;
					double hitY = result.getLocation().y() - pos.getY();
					if (hitY >= 0.0625F && hitY < 0.375F) {
						if (!level.isClientSide()) {
							runes.cycleBottomState(level, pos);
						}
						changed = true;
					}
					if (hitY >= 0.375F && hitY < 0.625F) {
						if (!level.isClientSide()) {
							runes.cycleMidState(level, pos);
						}
						changed = true;
					}
					if (hitY >= 0.625F && hitY <= 0.9375F) {
						if (!level.isClientSide()) {
							runes.cycleTopState(level, pos);
						}
						changed = true;
					}
					if (changed) {
						return InteractionResult.sidedSuccess(level.isClientSide());
					}
				}
			}
		}
		return super.useWithoutItem(state, level, pos, player, result);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockPos center = null;
		Direction facing = state.getValue(FACING);
		if (state.getValue(INVISIBLE)) {
			//part of the door, but not the main block. Find the main block position
			for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-facing.getStepZ(), -1, -facing.getStepX()), pos.offset(facing.getStepZ(), 1, facing.getStepX()))) {
				if (level.getBlockState(checkPos).is(this) && !level.getBlockState(checkPos).getValue(INVISIBLE)) {
					center = checkPos;
					break;
				}
			}
		} else {
			//breaking the main door. Set as the center
			center = pos;
		}

		if (center != null) {
			breakAllDoorBlocks(level, center, facing, false, true);
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	public static void breakAllDoorBlocks(Level level, BlockPos pos, Direction facing, boolean breakFloorBelow, boolean particles) {
		for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-facing.getStepZ(), breakFloorBelow ? -2 : -1, -facing.getStepX()), pos.offset(facing.getStepZ(), 1, facing.getStepX()))) {
			if (particles) {
				level.destroyBlock(checkPos, false);
			} else {
				level.removeBlock(checkPos, false);
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return !state.getValue(INVISIBLE) ? new DungeonDoorRunesBlockEntity(pos, state, this.type) : null;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.DUNGEON_DOOR_RUNES.get(), DungeonDoorRunesBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(INVISIBLE));
	}

	@Override
	protected boolean useShapeForLightOcclusion(BlockState state) {
		return false;
	}

	@Override
	protected float getShadeBrightness(BlockState p_308911_, BlockGetter p_308952_, BlockPos p_308918_) {
		return 1.0F;
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
		return true;
	}
}
