package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import thebetweenlands.common.block.entity.DungeonDoorRunesBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class DungeonDoorRunesBlock extends HorizontalBaseEntityBlock {

	public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
	public final boolean mimic;
	public final boolean barrishee;

	public DungeonDoorRunesBlock(boolean mimic, boolean barishee, Properties properties) {
		super(properties);
		this.mimic = mimic;
		this.barrishee = barishee;
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(INVISIBLE, false));
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		BlockState invisiBlock = this.defaultBlockState().setValue(INVISIBLE, true);
		if (!state.getValue(INVISIBLE)) {
			if (state.getValue(FACING).getAxis() == Direction.Axis.X) {
				for (int z = -1; z <= 1; z++)
					for (int y = -1; y <= 1; y++)
						if (pos.offset(0, y, z) != pos)
							level.setBlockAndUpdate(pos.offset(0, y, z), invisiBlock.setValue(FACING, state.getValue(FACING)));
			} else {
				for (int x = -1; x <= 1; x++)
					for (int y = -1; y <= 1; y++) {
						if (pos.offset(x, y, 0) != pos)
							level.setBlockAndUpdate(pos.offset(x, y, 0), invisiBlock.setValue(FACING, state.getValue(FACING)));
					}
			}
		}
		level.sendBlockUpdated(pos, state, state, 3);
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
					level.sendBlockUpdated(pos, state, state, 3);
					return ItemInteractionResult.SUCCESS;
				}
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!state.getValue(INVISIBLE) && !level.isClientSide() && hitResult.getDirection() == state.getValue(FACING)) {
			if (level.getBlockEntity(pos) instanceof DungeonDoorRunesBlockEntity runes && !runes.is_gate_entrance) {
				if (player.isCreative() && player.isCrouching()) {
					runes.enterLockCode(level, pos);
					player.displayClientMessage(Component.translatable("block.thebetweenlands.dungeon_door_runes.locked"), true);
				} else {
					double hitY = hitResult.getLocation().y();
					if (hitY >= 0.0625F && hitY < 0.375F && runes.bottom_rotate == 0)
						runes.cycleBottomState(level, pos);
					if (hitY >= 0.375F && hitY < 0.625F && runes.mid_rotate == 0)
						runes.cycleMidState(level, pos);
					if (hitY >= 0.625F && hitY <= 0.9375F && runes.top_rotate == 0)
						runes.cycleTopState(level, pos);
				}
			}
			level.sendBlockUpdated(pos, state, state, 3);
			return InteractionResult.SUCCESS;
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return !state.getValue(INVISIBLE) ? new DungeonDoorRunesBlockEntity(pos, state, this.mimic, this.barrishee) : null;
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.DUNGEON_DOOR_RUNES.get(), DungeonDoorRunesBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(INVISIBLE));
	}
}
