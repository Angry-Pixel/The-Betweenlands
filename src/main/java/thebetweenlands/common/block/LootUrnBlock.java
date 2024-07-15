package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import thebetweenlands.common.block.entity.LootUrnBlockEntity;

import javax.annotation.Nullable;

public class LootUrnBlock extends HorizontalBaseEntityBlock {

	public LootUrnBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			if (level.getBlockEntity(pos) instanceof LootUrnBlockEntity urn) {
				InvWrapper wrapper = new InvWrapper(urn);
				if (!stack.isEmpty()) {
					ItemStack prevStack = stack.copy();
					for (int i = 0; i < wrapper.getSlots() && !stack.isEmpty(); i++) {
						stack = wrapper.insertItem(i, stack, false);
					}
					if (stack.isEmpty() || stack.getCount() != prevStack.getCount()) {
						if (!player.isCreative()) {
							player.setItemInHand(hand, stack);
						}
						return ItemInteractionResult.SUCCESS;
					}
				}
			}
		} else {
			return ItemInteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof LootUrnBlockEntity urn) {
			InvWrapper wrapper = new InvWrapper(urn);
			if (player.isCrouching()) {
				for (int i = 0; i < wrapper.getSlots(); i++) {
					ItemStack extracted = wrapper.extractItem(i, 1, false);
					if (!extracted.isEmpty()) {
						ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
						item.setDeltaMovement(Vec3.ZERO);
						level.addFreshEntity(item);
						return InteractionResult.SUCCESS;
					}
				}
			}
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		//TODO
		if (!level.isClientSide()) {
//			if (level.getRandom().nextInt(3) == 0) {
//				Termite entity = new Termite(level);
//				entity.getAttribute(Termite.SMALL).setBaseValue(1);
//				entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
//				level.addFreshEntity(entity);
//			}
		}
		super.playerDestroy(level, player, pos, state, blockEntity, tool);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, moving);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LootUrnBlockEntity(pos, state);
	}
}
