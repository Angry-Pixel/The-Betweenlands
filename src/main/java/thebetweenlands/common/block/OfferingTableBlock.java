package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.OfferingTableBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class OfferingTableBlock extends HorizontalBaseEntityBlock {

	protected static final VoxelShape X_SHAPE = Block.box(3.5D, 0.0D, 2.0D, 12.5D, 5.0D, 14.0D);
	protected static final VoxelShape Z_SHAPE = Block.box(2.0D, 0.0D, 3.5D, 14.0D, 5.0D, 12.5D);

	public OfferingTableBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof OfferingTableBlockEntity table) {
			if (table.getTheItem().isEmpty()) {
				table.setTheItem(stack);
				player.setItemInHand(hand, ItemStack.EMPTY);
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof OfferingTableBlockEntity table && !table.getTheItem().isEmpty()) {
			if (!player.getInventory().add(table.getTheItem())) {
				player.drop(table.getTheItem(), false);
			}
			table.removeTheItem();
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OfferingTableBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.OFFERING_TABLE.get(), OfferingTableBlockEntity::tick);
	}
}
