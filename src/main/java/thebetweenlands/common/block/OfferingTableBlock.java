package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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

	protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);

	public OfferingTableBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OfferingTableBlockEntity table) {
			if (table.getTheItem().isEmpty()) {
				if (!level.isClientSide()) {
					table.setTheItem(stack);
					player.setItemInHand(hand, ItemStack.EMPTY);
				}

				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OfferingTableBlockEntity table && !table.getTheItem().isEmpty()) {
			ItemHandlerHelper.giveItemToPlayer(player, table.getTheItem());
			table.removeTheItem();
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!newState.is(this)) {
			if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OfferingTableBlockEntity table && !table.getTheItem().isEmpty()) {
				Block.popResource(level, pos, table.getTheItem());
			}
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
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
